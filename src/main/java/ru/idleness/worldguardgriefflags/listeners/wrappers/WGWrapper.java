package ru.idleness.worldguardgriefflags.listeners.wrappers;

import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.util.logging.Logger;

public abstract class WGWrapper implements Listener {
    final Plugin plugin;
    final Logger logger;

    protected RegisteredListener listener;

    protected boolean skipNext = false;

    public WGWrapper(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void setListener(RegisteredListener listener) {
        this.listener = listener;
    }

    public RegisteredListener getListener() {
        return listener;
    }

    public void skip() {
        skipNext = true;
    }

    private boolean isCallSkipped() {
        boolean ret = skipNext;
        skipNext = false;
        return ret;
    }

    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (listener == null) return;
        if (isCallSkipped()) return;

        try {
            listener.callEvent(event);
        } catch (EventException e) {
            throw new RuntimeException(e);
        }
    }
}
