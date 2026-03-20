package dev.lichi.onyxPads.models;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("JumpPad")
public class JumpPad implements ConfigurationSerializable {

    private UUID id;
    private Location location;
    private double power;
    private double angle;
    private boolean particles;
    private boolean sound;
    private String requiredPermission;

    public JumpPad(Location location, double power, double angle) {
        this.id = UUID.randomUUID();
        this.location = location.clone(); // Clone to prevent modification
        this.power = power;
        this.angle = angle;
        this.particles = true;
        this.sound = true;
        this.requiredPermission = null;
    }

    public JumpPad(Map<String, Object> map) {
        this.id = UUID.fromString((String) map.get("id"));
        this.location = (Location) map.get("location");
        this.power = ((Number) map.getOrDefault("power", 2.0)).doubleValue();
        this.angle = ((Number) map.getOrDefault("angle", 0.0)).doubleValue();
        this.particles = (boolean) map.getOrDefault("particles", true);
        this.sound = (boolean) map.getOrDefault("sound", true);
        this.requiredPermission = (String) map.get("permission");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("location", location);
        map.put("power", power);
        map.put("angle", angle);
        map.put("particles", particles);
        map.put("sound", sound);
        if (requiredPermission != null && !requiredPermission.isEmpty() && !requiredPermission.equalsIgnoreCase("none")) {
            map.put("permission", requiredPermission);
        }
        return map;
    }

    public Vector calculateVelocity() {
        double radians = Math.toRadians(angle);
        double horizontal = power * 0.5 * Math.cos(radians);
        double vertical = power * 0.8 * Math.sin(radians);

        // Ensure minimum vertical velocity
        if (vertical < 0.3) vertical = 0.4;

        return new Vector(0, vertical, 0);
    }

    /**
     * Verifica si el jump pad requiere un permiso específico
     * @return true si requiere permiso (no es null, no está vacío y no es "none")
     */
    public boolean hasPermission() {
        // Un jump pad tiene permiso si requiredPermission no es null,
        // no está vacío y no es "none" (case insensitive)
        return requiredPermission != null &&
                !requiredPermission.isEmpty() &&
                !requiredPermission.equalsIgnoreCase("none");
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public Location getLocation() {
        return location.clone(); // Return clone to prevent modification
    }

    public void setLocation(Location location) {
        this.location = location.clone();
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public boolean hasParticles() {
        return particles;
    }

    public void setParticles(boolean particles) {
        this.particles = particles;
    }

    public boolean hasSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        JumpPad jumpPad = (JumpPad) obj;
        return id.equals(jumpPad.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}