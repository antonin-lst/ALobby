package fr.acore.spigot;

import fr.acore.spigot.api.hook.exception.HookFailException;
import fr.acore.spigot.hook.ALoginHook;
import fr.acore.spigot.manager.ALobbyManager;
import fr.acore.spigot.module.AModule;
import org.bukkit.ChatColor;

public class ALobby extends AModule {

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            registerHook(new ALoginHook(this));
        } catch (HookFailException e) {
            logWarn(ChatColor.RED + "ALogin is not disponible.");
        }
        registerManager(new ALobbyManager(this));
        log("ALobby Enabled");
    }
}
