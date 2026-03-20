package dev.lichi.onyxPads.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class LocationUtils {

    /**
     * Check if a location is safe for teleportation
     */
    public static boolean isSafeLocation(Location location) {
        Block block = location.getBlock();
        Block below = block.getRelative(BlockFace.DOWN);
        Block above = block.getRelative(BlockFace.UP);

        // Check if block is safe to stand in
        if (block.getType().isSolid() || block.isLiquid()) {
            return false;
        }

        // Check if head space is safe
        if (above.getType().isSolid()) {
            return false;
        }

        // Check if ground is solid
        return below.getType().isSolid();
    }

    /**
     * Find a safe location near the given location
     */
    public static Location findSafeLocation(Location location, int maxDistance) {
        World world = location.getWorld();
        if (world == null) return null;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        // Search in a spiral pattern
        for (int dx = -maxDistance; dx <= maxDistance; dx++) {
            for (int dz = -maxDistance; dz <= maxDistance; dz++) {
                for (int dy = -maxDistance; dy <= maxDistance; dy++) {
                    Location check = new Location(world, x + dx, y + dy, z + dz);
                    if (isSafeLocation(check)) {
                        return check.add(0.5, 0, 0.5);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Format location for display
     */
    public static String formatLocation(Location location) {
        return String.format("%s, %d, %d, %d",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }
}