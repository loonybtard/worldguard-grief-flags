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

import java.util.HashMap;

public class WorldGuardGriefFlagsPlugin extends JavaPlugin implements Listener {

    public HashMap<String, StateFlag> wgFlags = new HashMap<String, StateFlag>();

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
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        String[] flagsToInit = {
                "grief-allow-falling", // done
                "grief-allow-wither", // done
                "grief-allow-piston",
                "grief-allow-hopper", // done
                "grief-allow-explosions", // done
        };

        for (String flagName : flagsToInit) {
            try {
                StateFlag flag = new StateFlag(flagName, false);
                registry.register(flag);
                wgFlags.put(flagName, flag);
            } catch (IllegalStateException e) { // when plugin reloaded via plugin manager
                wgFlags.put(flagName, (StateFlag) registry.get(flagName));
            } catch (FlagConflictException e) {
                // we don't give a fuck
            }
        }


    }

    private String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}