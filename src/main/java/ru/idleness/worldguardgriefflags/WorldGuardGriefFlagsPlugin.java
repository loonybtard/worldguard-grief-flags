package ru.idleness.worldguardgriefflags;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.idleness.worldguardgriefflags.listeners.PlayerListener;

import java.util.HashMap;

public class WorldGuardGriefFlagsPlugin extends JavaPlugin implements Listener {

    private PlayerListener listener;

    public HashMap<String, StateFlag> wgFlags = new HashMap<String, StateFlag>();

    @Override
    public void onLoad() {
        super.onLoad();

        wgFlagsRegister();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.listener = new PlayerListener(this);
        Bukkit.getPluginManager().registerEvents(this.listener, this);
        this.getCommand("wggf").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("wggf.command.reload")) {
            sender.sendMessage(color("&cУ вас нет разрешений!"));
            return true;
        }

        if (args.length <= 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(color("&f[&6WGGF&f] /wggf reload - перезагрузить конфиг"));
            return true;
        }

        reloadConfig();
        sender.sendMessage(color("&f[&6WGGF&f] Конфиг был перезагружен!"));
        return true;
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