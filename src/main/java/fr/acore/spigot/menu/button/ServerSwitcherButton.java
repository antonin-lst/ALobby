package fr.acore.spigot.menu.button;

import com.google.gson.JsonObject;
import fr.acore.spigot.ACoreSpigotAPI;
import fr.acore.spigot.api.plugin.IPlugin;
import fr.acore.spigot.jedis.manager.RedisManager;
import fr.acore.spigot.jedis.packet.impl.queue.AddPlayerToServerQueuePacket;
import fr.acore.spigot.manager.ALobbyManager;
import fr.acore.spigot.menu.event.ItemButtonClickEvent;
import fr.acore.spigot.menu.item.MenuItemButton;
import fr.acore.spigot.utils.item.ItemBuilder;
import org.bukkit.entity.Player;

public class ServerSwitcherButton extends MenuItemButton {

    private ALobbyManager lobbyManager;

    private String serverName;
    public String getServerName() { return this.serverName;}

    public ServerSwitcherButton(ItemBuilder item, int place, ALobbyManager lobbyManager, String serverName){
        super(item, place);
        this.lobbyManager = lobbyManager;
        this.serverName = serverName;
    }

    @Override
    public void onClick(ItemButtonClickEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();

        //send changePlayerServerPacket
        ACoreSpigotAPI.getInstance().getManager(RedisManager.class).sendPacket(new AddPlayerToServerQueuePacket(player.getUniqueId().toString(), serverName));
    }
}
