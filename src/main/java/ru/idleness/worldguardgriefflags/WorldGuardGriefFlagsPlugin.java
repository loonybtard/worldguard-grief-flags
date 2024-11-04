package ru.idleness.worldguardgriefflags;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.idleness.worldguardgriefflags.listeners.PlayerListener;
import ru.idleness.worldguardgriefflags.listeners.wrappers.EventAbstractionListener;
import ru.idleness.worldguardgriefflags.listeners.wrappers.WorldGuardEntityListenerWrapper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldGuardGriefFlagsPlugin extends JavaPlugin implements Listener {

    public final WorldGuardEntityListenerWrapper worldGuardEntityListenerWrapper;
    public final EventAbstractionListener eventAbstractionListener;

    public WorldGuardGriefFlagsPlugin() {
        super();

        this.worldGuardEntityListenerWrapper = new WorldGuardEntityListenerWrapper(this);
        this.eventAbstractionListener = new EventAbstractionListener(this);

    }

    @Override
    public void onLoad() {
        wgFlagsRegister();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(this.worldGuardEntityListenerWrapper, this);
        Bukkit.getPluginManager().registerEvents(this.eventAbstractionListener, this);

        wrapWGHandlers();
    }

    @Override
    public void onDisable() {
        unwrapWGHandlers();
    }

    private void wrapWGHandlers() {
        for (RegisteredListener registeredListener : EntityChangeBlockEvent.getHandlerList().getRegisteredListeners()) {
            String className = registeredListener.getListener().getClass().getName();

            if (className.equals("com.sk89q.worldguard.bukkit.listener.WorldGuardEntityListener")) {
                this.worldGuardEntityListenerWrapper.setListener(registeredListener);
                EntityChangeBlockEvent.getHandlerList().unregister(registeredListener);
                continue;
            }

            if (className.equals("com.sk89q.worldguard.bukkit.listener.EventAbstractionListener")) {
                this.eventAbstractionListener.setListener(registeredListener);
                EntityChangeBlockEvent.getHandlerList().unregister(registeredListener);
                continue;
            }
        }
    }

    private void unwrapWGHandlers() {
        EntityChangeBlockEvent.getHandlerList().register(this.worldGuardEntityListenerWrapper.getListener());
        EntityChangeBlockEvent.getHandlerList().register(this.eventAbstractionListener.getListener());
    }

    private void wgFlagsRegister() {
        GriefFlag.prefix = this.getConfig().getString("wg-flag-prefix");

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        Logger log = Bukkit.getLogger();

        for (GriefFlag flag : GriefFlag.values()) {
            try {
                flag.setFlag(new StateFlag(flag.getName(), false));
                registry.register(flag.getFlag());
            } catch (IllegalStateException e) {
                // when plugin reloaded via plugin manager
                flag.setFlag((StateFlag) registry.get(flag.getName()));
            } catch (FlagConflictException e) {
                log.log(Level.WARNING, "Flag " + flag.getName() + " already defined. Try to change \"wg-flag-prefix\"");
                flag.setFlag((StateFlag) registry.get(flag.getName()));
            }
        }

    }
}