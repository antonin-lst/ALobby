package fr.acore.spigot.menu;

import fr.acore.spigot.api.menu.item.MenuItem;
import fr.acore.spigot.manager.ALobbyManager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class ALobbyMenu extends AMenu{

    private ALobbyManager lobbyManager;

    public ALobbyMenu(ALobbyManager lobbyManager){
        super(lobbyManager.getInvName(), lobbyManager.getInvSize());
        this.lobbyManager = lobbyManager;
        for(MenuItem item : lobbyManager.getInvContents()) {
            addItem(item);
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

}
