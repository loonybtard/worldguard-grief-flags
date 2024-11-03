package ru.overwrite.wggf.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import ru.overwrite.wggf.WorldGuardGriefFixPlugin;


public class PlayerListener implements Listener {

    WorldGuardGriefFixPlugin plugin;

    List<Material> fallingBlocks = Arrays.asList(Material.SAND, Material.GRAVEL, Material.ANVIL); // Временно, потому перепишу
    List<Material> interactBlocks = Arrays.asList(Material.AIR, Material.WATER, Material.SNOW, Material.LAVA);

    public PlayerListener(WorldGuardGriefFixPlugin plugin) {
        this.plugin = plugin;
        this.worldGuard = WorldGuard.getInstance();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("enable-pistons")) return;
        for (Block block : event.getBlocks()) {
            if (!config.getStringList("excluded-blocks").contains(block.getType().toString()) && checkLocation(block.getLocation())) {
                event.setCancelled(false);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRetract(BlockPistonRetractEvent event) {
        if (!plugin.getConfig().getBoolean("enable-pistons")) return;

        for (Block block : event.getBlocks())
            if (this.checkLocation(block.getLocation())) {
                event.setCancelled(false);
                return;
            }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemMove(InventoryMoveItemEvent event) {
        if (plugin.getConfig().getBoolean("enable-minecart")) {
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWitherBlockDamage(EntityChangeBlockEvent event) {
        if (event.getEntityType() != EntityType.WITHER)
            return;

        event.setCancelled(false);
        for (String blocks : plugin.getConfig().getStringList("excluded-blocks"))
            if (blocks.contains(event.getBlock().getType().toString())) {
                event.setCancelled(true);
            }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        val entity = event.getEntity();
        val block = event.getBlock();
        val to = event.getTo();

        if (!(entity instanceof FallingBlock) || !this.plugin.getConfig().getBoolean("enable-falling-block"))
            return;

        if (this.interactBlocks.contains(block.getType()) &&
                this.fallingBlocks.contains(to) &&
                this.checkLocation(block.getLocation()) &&
                !this.plugin.getConfig().getStringList("excluded-blocks").contains(block.getType().name())) {
            event.setCancelled(true); // Отменяем евент, чтобы сыпущий блок не выпал после падения
            block.setType(to); // Ставим тот же блок
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (checkLocation(event.getLocation()) && plugin.getConfig().getBoolean("enable-any-explotions")) {
            event.setCancelled(false);
        } else if (checkLocation(event.getLocation()) && plugin.getConfig().getBoolean("enable-wither-skull")
                && event.getEntityType() == EntityType.WITHER_SKULL) {
            event.setCancelled(false);
        }
    }

    // FIXME: remove method
    private boolean checkLocation(Location location) {
        if (location == null)
            return false;

        return true;
    }
}