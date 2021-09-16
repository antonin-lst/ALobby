package fr.acore.spigot.listener;

import fr.acore.spigot.event.PlayerAuthSuccessEvent;
import fr.acore.spigot.manager.ALobbyManager;
import fr.acore.spigot.menu.ALobbyMenu;
import fr.acore.spigot.player.manager.PlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

public class ALobbyListener implements Listener {

    private ALobbyManager lobbyManager;

    public ALobbyListener(ALobbyManager lobbyManager){
        this.lobbyManager = lobbyManager;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        if(lobbyManager.isForceSpawnLocation()) player.teleport(lobbyManager.getSpawnLocation());

        if(!lobbyManager.getLoginHook().isHooked()) {
            player.getInventory().clear();
            player.getInventory().setItem(3, lobbyManager.getNavItem());
            player.getInventory().setItem(5, lobbyManager.getShowHidePlayerItem());
            lobbyManager.addPlayerShowHidePlayer(player);
        }
    }

    @EventHandler
    public void onAuthSuccessEvent(PlayerAuthSuccessEvent event) {
        Player player = event.getPlayer().getPlayer();
        player.getInventory().clear();
        player.getInventory().setItem(3, lobbyManager.getNavItem());
        player.getInventory().setItem(5, lobbyManager.getShowHidePlayerItem());
        lobbyManager.addPlayerShowHidePlayer(player);

    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        lobbyManager.removeShowHidePlayer(player);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String cmdRoot = event.getMessage().contains(" ") ? event.getMessage().split(" ")[0] : event.getMessage();
        cmdRoot = cmdRoot.replaceFirst("/", "");
        if(!event.getPlayer().hasPermission("commands.bypass") && !lobbyManager.getLoginHook().getHook().getAuthCommands().contains(cmdRoot)) event.setCancelled(true);
    }

    @EventHandler
    public void onBreackBlock(BlockBreakEvent event) {
        if(!event.getPlayer().hasPermission("builder.bypass")) event.setCancelled(true);
    }

    @EventHandler
    public void playerBlocPlaceEvent(BlockPlaceEvent event) {
        if(!event.getPlayer().hasPermission("builder.bypass")) event.setCancelled(true);
    }

    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent event) {
        if(!event.getPlayer().hasPermission("builder.bypass")) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerChatEvent(AsyncPlayerChatEvent event) {
        if(!event.getPlayer().hasPermission("chat.bypass")) event.setCancelled(true);
    }

    @EventHandler
    public void onFeedUpdate(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onHealChange(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if(item.getType().equals(Material.COMPASS)) {
            lobbyManager.openMenu(lobbyManager.getManager(PlayerManager.class).getOnlinePlayer(player), new ALobbyMenu(lobbyManager));
        }else if(item.getType().equals(Material.EYE_OF_ENDER)) {
            lobbyManager.updateShowHidePlayer(player);
        }

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(!player.hasPermission("builder.bypass")) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if(!event.getPlayer().hasPermission("builder.bypass")) event.setCancelled(true);
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if (event.toWeatherState()) {

            event.setCancelled(true);
            event.getWorld().setStorm(false);
            event.getWorld().setThundering(false);
            event.getWorld().setWeatherDuration(0);
        }
    }

}
