package dev.lichi.onyxPads.listeners;

import dev.lichi.onyxPads.OnyxPads;
import dev.lichi.onyxPads.models.JumpPad;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JumpPadListener implements Listener {

    private final OnyxPads plugin;
    private final Set<UUID> cooldown = new HashSet<>();
    private final Set<UUID> jumpCooldown = new HashSet<>();

    public JumpPadListener(OnyxPads plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfigManager().getConfig().getBoolean("protection.prevent-breaking", true)) {
            return;
        }

        Block block = event.getBlock();
        if (plugin.getJumpPadManager().isJumpPad(block)) {
            event.setCancelled(true);
            plugin.getMessageManager().sendMessage(event.getPlayer(), "CannotBreakJumpPad");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.getConfigManager().getConfig().getBoolean("protection.prevent-block-place", true)) {
            return;
        }

        Block against = event.getBlockAgainst();
        if (plugin.getJumpPadManager().isJumpPad(against)) {
            event.setCancelled(true);
            plugin.getMessageManager().sendMessage(event.getPlayer(), "CannotPlaceOnJumpPad");
            return;
        }

        // También verificar si el bloque que se está colocando es donde hay un jump pad
        Block placed = event.getBlockPlaced();
        if (plugin.getJumpPadManager().isJumpPad(placed)) {
            event.setCancelled(true);
            plugin.getMessageManager().sendMessage(event.getPlayer(), "CannotPlaceOnJumpPad");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;

        // Primero manejar la activación del jump pad (PHYSICAL)
        if (event.getAction() == Action.PHYSICAL) {
            handleJumpPadActivation(event, block);
            return;
        }

        // Luego manejar otras interacciones (protección)
        if (plugin.getJumpPadManager().isJumpPad(block)) {
            if (plugin.getConfigManager().getConfig().getBoolean("protection.prevent-interact", true)) {
                // Permitir PHYSICAL pero cancelar otras interacciones
                if (event.getAction() != Action.PHYSICAL) {
                    event.setCancelled(true);
                    plugin.getMessageManager().sendMessage(event.getPlayer(), "CannotInteractWithJumpPad");
                }
            }
        }
    }

    private void handleJumpPadActivation(PlayerInteractEvent event, Block block) {
        // Verificar si es una placa de presión
        if (!isPressurePlate(block.getType())) return;

        JumpPad pad = plugin.getJumpPadManager().getJumpPad(block);
        if (pad == null) return;

        Player player = event.getPlayer();

        // Cooldown para evitar múltiples activaciones
        if (jumpCooldown.contains(player.getUniqueId())) return;

        // Check combat
        if (plugin.getConfigManager().getConfig().getBoolean("combat.enabled", false)) {
            if (plugin.getJumpPadManager().isInCombat(player.getUniqueId()) &&
                    !player.hasPermission("onyxpads.bypass.combat")) {
                long timeLeft = plugin.getJumpPadManager().getCombatTimeLeft(player.getUniqueId());
                plugin.getMessageManager().sendMessage(player, "InCombat", "time", String.valueOf(timeLeft));
                return;
            }
        }

        // CHECK PERMISSION - VERSIÓN MEJORADA
        if (pad.hasPermission()) {
            String requiredPermission = pad.getRequiredPermission();

            // Si el permiso es null, vacío o "none", NO requiere permiso
            if (requiredPermission == null || requiredPermission.isEmpty() || requiredPermission.equalsIgnoreCase("none")) {
                // No se requiere permiso, continuar normalmente
                plugin.getLogger().fine("Jump pad has no permission required, allowing usage");
            } else {
                // Verificar si el jugador tiene el permiso requerido
                if (!player.hasPermission(requiredPermission)) {
                    // Enviar mensaje con el permiso requerido
                    plugin.getMessageManager().sendMessage(player, "NoJumpPadPermission", "permission", requiredPermission);
                    return;
                }
            }
        }

        // Aplicar velocidad
        applyJump(player, pad);

        // Efectos
        spawnEffects(player, pad);

        // Añadir cooldown
        jumpCooldown.add(player.getUniqueId());
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            jumpCooldown.remove(player.getUniqueId());
        }, 10L); // 10 ticks cooldown (0.5 segundos)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!plugin.getConfigManager().getConfig().getBoolean("protection.protect-from-explosions", true)) {
            return;
        }

        event.blockList().removeIf(block -> plugin.getJumpPadManager().isJumpPad(block));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!plugin.getConfigManager().getConfig().getBoolean("protection.protect-from-explosions", true)) {
            return;
        }

        event.blockList().removeIf(block -> plugin.getJumpPadManager().isJumpPad(block));
    }

    private boolean isPressurePlate(Material material) {
        List<String> allowedBlocks = plugin.getConfigManager().getConfig()
                .getStringList("allowed-blocks");

        return allowedBlocks.contains(material.name());
    }

    private void applyJump(Player player, JumpPad pad) {
        // Obtener la dirección hacia donde mira el jugador
        Vector direction = player.getLocation().getDirection().normalize();

        // Calcular velocidad basada en poder y ángulo
        double power = pad.getPower();
        double angle = Math.toRadians(pad.getAngle());

        // Componentes de velocidad
        double horizontal = power * 0.5 * Math.cos(angle);
        double vertical = power * 0.8 * Math.sin(angle);

        // Asegurar un mínimo de velocidad vertical
        if (vertical < 0.2) vertical = 0.4;

        // Crear vector de velocidad
        Vector velocity = new Vector(
                direction.getX() * horizontal,
                vertical,
                direction.getZ() * horizontal
        );

        // Aplicar velocidad
        player.setVelocity(velocity);

        // Marcar que el jugador está en el aire para evitar daño de caída
        player.setFallDistance(0);
    }

    private void spawnEffects(Player player, JumpPad pad) {
        if (pad.hasParticles() && plugin.getConfigManager().getConfig().getBoolean("particles.enabled", true)) {
            String particleType = plugin.getConfigManager().getConfig().getString("particles.type", "FLAME");
            int count = plugin.getConfigManager().getConfig().getInt("particles.count", 20);
            double speed = plugin.getConfigManager().getConfig().getDouble("particles.speed", 0.1);

            try {
                Particle particle = Particle.valueOf(particleType);
                player.getWorld().spawnParticle(particle, player.getLocation(), count, 0.5, 0.5, 0.5, speed);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid particle type in config: " + particleType);
            }
        }

        if (pad.hasSound() && plugin.getConfigManager().getConfig().getBoolean("sounds.enabled", true)) {
            String soundType = plugin.getConfigManager().getConfig().getString("sounds.use", "ENTITY_ENDER_DRAGON_SHOOT");
            float volume = (float) plugin.getConfigManager().getConfig().getDouble("sounds.volume", 0.5);
            float pitch = (float) plugin.getConfigManager().getConfig().getDouble("sounds.pitch", 1.0);

            try {
                Sound sound = Sound.valueOf(soundType);
                player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid sound type in config: " + soundType);
            }
        }
    }
}