package fr.acore.spigot.manager;

import fr.acore.spigot.api.menu.MenuSize;
import fr.acore.spigot.api.menu.item.MenuItem;
import fr.acore.spigot.api.plugin.module.IModule;
import fr.acore.spigot.api.runnable.IRunnable;
import fr.acore.spigot.api.runnable.RunnableUsage;
import fr.acore.spigot.hook.ALoginHook;
import fr.acore.spigot.listener.ALobbyListener;
import fr.acore.spigot.menu.button.ServerSwitcherButton;
import fr.acore.spigot.module.AManager;
import fr.acore.spigot.utils.ServersInfo;
import fr.acore.spigot.utils.StatutsType;
import fr.acore.spigot.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALobbyManager extends AManager implements IRunnable {

    private ALoginHook loginHook;
    public ALoginHook getLoginHook() { return this.loginHook;}

    private boolean forceSpawnLocation;
    private Location spawnLocation;

    private boolean useChutDetection;
    public boolean isUseChutDetection() { return this.useChutDetection;}
    private int minY;
    public int getMinY() { return this.minY;}

    private List<ServersInfo> serversInfo;

    private String statusOfflineFormat;
    public String getStatusOfflineFormat() { return this.statusOfflineFormat;}

    private String statusWithelistedFormat;
    public String getStatusWithelistedFormat() { return this.statusWithelistedFormat;}

    private String statusOnlineFormat;
    public String getStatusOnlineFormat() { return this.statusOnlineFormat;}

    private String playersInformationFormat;
    public String getPlayersInformationFormat() { return this.playersInformationFormat;}

    private String showFormat;
    public String getShowFormat() { return this.showFormat;}

    private String hideFormat;
    public String getHideFormat() { return this.hideFormat;}

    private String showHideMessage;
    public String getShowHideMesage() { return this.showHideMessage;}

    private long refreshTime;

    private String name;
    public String getInvName() { return this.name;}
    private int rows;
    private int colones;
    public MenuSize getInvSize() { return new MenuSize(rows, colones);}
    private ItemStack navItem;
    public ItemStack getNavItem() { return this.navItem;}
    private List<MenuItem> contents;
    public List<MenuItem> getInvContents(){ return replaceAll(this.contents);}


    private ItemStack showHidePlayerItem;
    public ItemStack getShowHidePlayerItem() { return this.showHidePlayerItem;}

    private Map<Player, Boolean> showHidePlayers;

    private List<MenuItem> replaceAll(List<MenuItem> content) {
        List<MenuItem> returnedContent = new ArrayList<>();

        for(MenuItem item : content) {

            if(item instanceof ServerSwitcherButton) {
                ServerSwitcherButton serverSwitcher = (ServerSwitcherButton) item;
                ItemStack itemStack = serverSwitcher.getItem();
                String name = replaceAll(serverSwitcher.getServerName(), itemStack.getItemMeta().getDisplayName());
                List<String> lore = replaceAll(serverSwitcher.getServerName(), itemStack.getItemMeta().getLore());
                returnedContent.add(new ServerSwitcherButton(new ItemBuilder(itemStack.getType(), 1, name, lore), serverSwitcher.getPlace(), this, serverSwitcher.getServerName()));
            }else {
                returnedContent.add(item);
            }
        }
        return returnedContent;
    }


    public ALobbyManager(IModule key) {
        super(key, true);
        this.loginHook = getHook(ALoginHook.class);
        this.showHidePlayers = new HashMap<>();
        registerListener(new ALobbyListener(this));
        registerAsyncRunnable(RunnableUsage.RENDER, this);
    }

    @Override
    public void setup(FileConfiguration config) {
        forceSpawnLocation = config.getBoolean("spawn.forced");

        World world = Bukkit.getWorld(config.getString("spawn.world"));

        int x = config.getInt("spawn.x");
        int y = config.getInt("spawn.y");
        int z = config.getInt("spawn.z");

        spawnLocation = new Location(world, x, y, z);

        useChutDetection = config.getBoolean("chuteDetection.use");
        minY = config.getInt("chuteDetection.y");

        statusOfflineFormat = convertColor(config.getString("serverStatutsInfo.offline"));
        statusWithelistedFormat = convertColor(config.getString("serverStatutsInfo.withelisted"));
        statusOnlineFormat = convertColor(config.getString("serverStatutsInfo.online"));
        playersInformationFormat = convertColor(config.getString("serverStatutsInfo.players"));

        showFormat = convertColor(config.getString("showHideMessage.show"));
        hideFormat = convertColor(config.getString("showHideMessage.hide"));

        showHideMessage = convertColor(config.getString("showHideMessage.message"));

        refreshTime = (long) config.getInt("refreshTime");

        serversInfo = new ArrayList<>();

        for(String serverName : config.getConfigurationSection("servers").getKeys(false)) {
            log("Detection server : " + serverName);
            serversInfo.add(new ServersInfo(serverName, StatutsType.OFFLINE, convertColor(config.getString("servers." + serverName + ".description")), config.getInt("servers." + serverName + ".maxPlayer"), refreshTime));
        }

        navItem = new ItemBuilder(Material.getMaterial(config.getString("navItem.item")), 1, convertColor(config.getString("navItem.name")), convertColor(config.getStringList("navItem.lore"))).build();

        showHidePlayerItem = new ItemBuilder(Material.getMaterial(config.getString("showHidePlayerItem.item")), 1, convertColor(config.getString("showHidePlayerItem.name")), convertColor(config.getStringList("showHidePlayerItem.lore"))).build();


        contents = new ArrayList<>();

        name = convertColor(config.getString("navInventory.name"));
        rows = config.getInt("navInventory.rows");
        colones = config.getInt("navInventory.colones");
        for(String itemName : config.getConfigurationSection("navInventory.contents").getKeys(false)) {

            Material m = Material.getMaterial(config.getString("navInventory.contents." + itemName + ".item"));
            byte data = -1;
            if(m == null) {
                m = Material.getMaterial(config.getString("navInventory.contents." + itemName + ".item").split(":")[0]);
                data = (byte) Integer.parseInt(config.getString(config.getString("navInventory.contents." + itemName + ".item").split(":")[1]));
            }

            String name = convertColor(config.getString("navInventory.contents." + itemName + ".name"));
            int index = config.getInt("navInventory.contents." + itemName + ".index");
            List<String> lore = convertColor(config.getStringList("navInventory.contents." + itemName + ".lore"));

            ItemBuilder itemB = data == -1 ? new ItemBuilder(m, 1, name, lore) : new ItemBuilder(m, 1, name, data, lore);

            if(containServerInfo(itemName)) {
                contents.add(new ServerSwitcherButton(itemB, index, this, itemName));
            }else {
                contents.add(new MenuItem(itemB, index));
            }
        }
    }


    @Override
    public void ticks() {
        for(ServersInfo server : serversInfo) {
            if(server.needUpdate()) {
                server.update(getPlugin());
            }
        }

        if(isUseChutDetection()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.getLocation().getY() < minY) {
                    player.teleport(spawnLocation);
                }
            }
        }
    }

    public List<String> replaceAll(String serverName, List<String> message){
        List<String> returned = new ArrayList<>();
        for(String m : message) {
            returned.add(replaceAll(serverName, m));
        }
        return returned;
    }

    public String replaceAll(String serverName, String message) {
        ServersInfo info = getServerInfo(serverName);
        message = replace(message, "%onlinePlayer%", String.valueOf(info.getOnlinePlayer()));
        message = replace(message, "%maxPlayer%", String.valueOf(info.getMaxPlayer()));
        message = replace(message, "%description%", info.getDescription());
        message = replace(message, "%serverName%", info.getName());
        message = replace(message, "%serverStatuts%", info.getStatuts() == StatutsType.OFFLINE ? statusOfflineFormat : info.getStatuts() == StatutsType.WHITELISTED ? statusWithelistedFormat : statusOnlineFormat);
        if(message.contains("%players%"))
            message = replace(message, "%players%", replaceAll(serverName, playersInformationFormat));
        return message;

    }

    public String replaceShowHide(Player player, String message) {

        message = replace(message, "%status%", showHidePlayers.get(player) ? hideFormat : showFormat);

        return message;
    }

    public ServersInfo getServerInfo(String serverName) {
        for(ServersInfo server : serversInfo) {
            if(server.getName().equals(serverName)) return server;
        }
        return null;
    }

    public boolean containServerInfo(String serverName) {
        for(ServersInfo server : serversInfo) {
            if(server.getName().equals(serverName)) return true;
        }
        return false;
    }

    public boolean isForceSpawnLocation() {
        return forceSpawnLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void addPlayerShowHidePlayer(Player player) {
        this.showHidePlayers.put(player, false);
    }

    public void updateShowHidePlayer(Player player) {
        boolean hide = showHidePlayers.get(player);
        for(Player target : Bukkit.getOnlinePlayers()) {
            if(!hide) {
                player.hidePlayer(target);
            }else {
                player.showPlayer(target);
            }
            showHidePlayers.replace(player, !hide);
        }
        player.sendMessage(replaceShowHide(player, showHideMessage));
    }

    public void removeShowHidePlayer(Player player) {
        showHidePlayers.remove(player);
    }
}
