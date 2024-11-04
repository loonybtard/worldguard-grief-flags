package ru.idleness.worldguardgriefflags.listeners.wrappers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;

/**
 * Wrapper for com.sk89q.worldguard.bukkit.listener.WorldGuardEntityListener:onEntityChangeBlock
 *
 * @link <a href="https://github.com/EngineHub/WorldGuard/blob/764d258ed02516fee655bbc7f4b00a2d986bd24b/worldguard-bukkit/src/main/java/com/sk89q/worldguard/bukkit/listener/WorldGuardEntityListener.java#L811"> WorldGuardEntityListener on github </a>
 */
public class WorldGuardEntityListenerWrapper extends WGWrapper implements Listener {

    public WorldGuardEntityListenerWrapper(Plugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        super.onEntityChangeBlock(event);
    }
}
