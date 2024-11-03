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

    WorldGuard worldGuard;

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
        ApplicableRegionSet regions = getRegionSet(event.getSource().getLocation());
        if (regions == null)
            return;

        if (regions.testState(null, plugin.wgFlags.get("grief-allow-hopper")))
            event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWitherBlockDamage(EntityChangeBlockEvent event) {
        if (event.getEntityType() != EntityType.WITHER)
            return;

        Block block = event.getBlock();

        ApplicableRegionSet regions = this.getRegionSet(block.getLocation());
        if (regions == null)
            return;

        if (regions.testState(null, plugin.wgFlags.get("grief-allow-wither")))
            event.setCancelled(false);

    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        Block block = event.getBlock();
        Material to = event.getTo();

        if (!(entity instanceof FallingBlock))
            return;

        ApplicableRegionSet regions = this.getRegionSet(block.getLocation());
        if (regions == null)
            return;

        if (regions.testState(null, plugin.wgFlags.get("grief-allow-falling"))) {
            event.setCancelled(true);
            block.setType(to);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        ApplicableRegionSet regions = this.getRegionSet(event.getLocation());
        if (regions == null)
            return;

        if (regions.testState(null, plugin.wgFlags.get("grief-allow-explosions")))
            event.setCancelled(false);
    }

    private ApplicableRegionSet getRegionSet(Location location) {
        RegionContainer container = this.worldGuard.getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet regionSet = query.getApplicableRegions(BukkitAdapter.adapt(location));

        if (regionSet == null || regionSet.getRegions().isEmpty())
            return null;

        return regionSet;
    }

    // FIXME: remove method
    private boolean checkLocation(Location location) {
        if (location == null)
            return false;

        return true;
    }
}