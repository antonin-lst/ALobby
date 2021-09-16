package fr.acore.spigot.hook;

import fr.acore.spigot.api.plugin.IPlugin;
import fr.acore.spigot.hook.impl.ManagerHook;
import fr.acore.spigot.manager.ALoginManager;

public class ALoginHook extends ManagerHook<ALoginManager> {

    public ALoginHook(IPlugin<?> hooker) {
        super(hooker, ALoginManager.class);
    }
}
