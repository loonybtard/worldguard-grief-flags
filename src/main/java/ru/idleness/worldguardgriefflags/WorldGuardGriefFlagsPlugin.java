package ru.idleness.worldguardgriefflags;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.idleness.worldguardgriefflags.listeners.PlayerListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldGuardGriefFlagsPlugin extends JavaPlugin implements Listener {

    @Override
    public void onLoad() {
        super.onLoad();

        wgFlagsRegister();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void wgFlagsRegister() {
        GriefFlag.prefix = this.getConfig().getString("wg-flag-prefix");;

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