package ru.idleness.worldguardgriefflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
import ru.idleness.worldguardgriefflags.GriefFlag;
import ru.idleness.worldguardgriefflags.WorldGuardGriefFlagsPlugin;


public class PlayerListener implements Listener {

    WorldGuardGriefFlagsPlugin plugin;

    WorldGuard worldGuard;

    public PlayerListener(WorldGuardGriefFlagsPlugin plugin) {
        this.plugin = plugin;
        this.worldGuard = WorldGuard.getInstance();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            Location locationDest = block.getLocation().add(
                    event.getDirection().getModX(),
                    event.getDirection().getModY(),
                    event.getDirection().getModZ()
            );
            ApplicableRegionSet regions = getRegionSet(locationDest);
            if (regions == null)
                continue;

            if (regions.testState(null, GriefFlag.PISTON.getFlag())) {
                event.setCancelled(false);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRetract(BlockPistonRetractEvent event) {

        for (Block block : event.getBlocks()) {
            ApplicableRegionSet regions = getRegionSet(block.getLocation());
            if (regions == null)
                continue;

            if (regions.testState(null, GriefFlag.PISTON.getFlag())) {
                event.setCancelled(false);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemMove(InventoryMoveItemEvent event) {
        ApplicableRegionSet regions = getRegionSet(event.getSource().getLocation());
        if (regions == null)
            return;

        if (regions.testState(null, GriefFlag.HOPPER.getFlag()))
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

        if (regions.testState(null, GriefFlag.WITHER.getFlag()))
            event.setCancelled(false);

    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        Block block = event.getBlock();

        if (!(entity instanceof FallingBlock))
            return;

        ApplicableRegionSet regions = this.getRegionSet(block.getLocation());
        if (regions == null)
            return;

        if (regions.testState(null, GriefFlag.FALLING.getFlag())) {
            plugin.eventAbstractionListener.skip();
            plugin.worldGuardEntityListenerWrapper.skip();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        ApplicableRegionSet regions = this.getRegionSet(event.getLocation());
        if (regions == null)
            return;

        if (regions.testState(null, GriefFlag.EXPLOSIONS.getFlag()))
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