package dev.lichi.onyxPads.managers;

import dev.lichi.onyxPads.OnyxPads;
import dev.lichi.onyxPads.models.JumpPad;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JumpPadManager {

    private final OnyxPads plugin;
    private final Map<Location, JumpPad> jumpPads = new ConcurrentHashMap<>();
    private final Map<UUID, Long> combatCooldowns = new ConcurrentHashMap<>();

    public JumpPadManager(OnyxPads plugin) {
        this.plugin = plugin;
        loadAll();
    }

    public void loadAll() {
        jumpPads.clear();
        FileConfiguration data = plugin.getConfigManager().getJumpPads();

        ConfigurationSection section = data.getConfigurationSection("jumppads");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    Object obj = section.get(key);
                    if (obj instanceof JumpPad) {
                        JumpPad pad = (JumpPad) obj;
                        jumpPads.put(pad.getLocation(), pad);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load jump pad: " + key);
                }
            }
        }

        plugin.getLogger().info("Loaded " + jumpPads.size() + " jump pads");
    }

    public void saveAll() {
        FileConfiguration data = plugin.getConfigManager().getJumpPads();
        data.set("jumppads", null);

        ConfigurationSection section = data.createSection("jumppads");
        int i = 0;
        for (JumpPad pad : jumpPads.values()) {
            section.set("pad" + (i++), pad);
        }

        plugin.getConfigManager().saveJumpPads();
    }

    public void addJumpPad(JumpPad pad) {
        jumpPads.put(pad.getLocation(), pad);
        saveAll();
    }

    public void removeJumpPad(Location location) {
        jumpPads.remove(location);
        saveAll();
    }

    public JumpPad getJumpPad(Location location) {
        return jumpPads.get(location);
    }

    public JumpPad getJumpPad(Block block) {
        if (block == null) return null;
        return getJumpPad(block.getLocation());
    }

    public boolean isJumpPad(Location location) {
        return jumpPads.containsKey(location);
    }

    public boolean isJumpPad(Block block) {
        if (block == null) return false;
        return isJumpPad(block.getLocation());
    }

    public Collection<JumpPad> getAllJumpPads() {
        return Collections.unmodifiableCollection(jumpPads.values());
    }

    public List<JumpPad> getJumpPadsInWorld(UUID worldId) {
        List<JumpPad> result = new ArrayList<>();
        for (JumpPad pad : jumpPads.values()) {
            if (pad.getLocation().getWorld() != null &&
                    pad.getLocation().getWorld().getUID().equals(worldId)) {
                result.add(pad);
            }
        }
        return result;
    }

    public void setCombatCooldown(UUID playerId) {
        long combatTime = plugin.getConfigManager().getConfig().getLong("combat.cooldown", 5) * 1000;
        combatCooldowns.put(playerId, System.currentTimeMillis() + combatTime);

        // Schedule removal after cooldown
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            removeCombatCooldown(playerId);
        }, combatTime / 50);
    }

    public void removeCombatCooldown(UUID playerId) {
        combatCooldowns.remove(playerId);
    }

    public boolean isInCombat(UUID playerId) {
        if (!combatCooldowns.containsKey(playerId)) return false;

        long expiry = combatCooldowns.get(playerId);
        if (System.currentTimeMillis() > expiry) {
            combatCooldowns.remove(playerId);
            return false;
        }

        return true;
    }

    public long getCombatTimeLeft(UUID playerId) {
        if (!combatCooldowns.containsKey(playerId)) return 0;

        long timeLeft = (combatCooldowns.get(playerId) - System.currentTimeMillis()) / 1000;
        return Math.max(0, timeLeft);
    }

    public boolean canUsePower(Player player, double power) {
        if (player == null) return false;

        int intPower = (int) Math.ceil(power);
        for (int i = 1; i <= intPower; i++) {
            if (player.hasPermission("onyxpads.power." + i)) {
                return true;
            }
        }
        return false;
    }

    public int getMaxAllowedPower(Player player) {
        if (player == null) return 1;

        int max = plugin.getConfigManager().getConfig().getInt("max-power", 5);

        for (int i = max; i >= 1; i--) {
            if (player.hasPermission("onyxpads.power." + i)) {
                return i;
            }
        }

        return 1;
    }

    public int getTotalCount() {
        return jumpPads.size();
    }

    public boolean isValidJumpPadLocation(Location location) {
        if (location == null || location.getWorld() == null) return false;

        List<String> allowedBlocks = plugin.getConfigManager().getConfig()
                .getStringList("allowed-blocks");

        String blockType = location.getBlock().getType().name();
        return allowedBlocks.contains(blockType);
    }

    public JumpPad getJumpPadById(String idPrefix) {
        for (JumpPad pad : jumpPads.values()) {
            if (pad.getId().toString().startsWith(idPrefix)) {
                return pad;
            }
        }
        return null;
    }
}