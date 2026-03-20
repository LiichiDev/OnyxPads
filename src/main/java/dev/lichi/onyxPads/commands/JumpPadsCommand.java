package dev.lichi.onyxPads.commands;

import dev.lichi.onyxPads.OnyxPads;
import dev.lichi.onyxPads.models.JumpPad;
import dev.lichi.onyxPads.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class JumpPadsCommand implements CommandExecutor, TabCompleter {

    private final OnyxPads plugin;
    private final List<String> subCommands = Arrays.asList("help", "create", "delete", "set", "info", "list", "reload");
    private final List<String> attributes = Arrays.asList("power", "angle", "particles", "sound", "permission");
    private final List<String> booleanValues = Arrays.asList("true", "false");

    public JumpPadsCommand(OnyxPads plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // ========== VERIFICACIÓN DE PERMISO PRINCIPAL ==========
        if (!sender.hasPermission("onyxpads.use")) {
            plugin.getMessageManager().sendMessage(sender, "InvalidPermission");
            return true;
        }
        // =======================================================

        // Comando de teletransporte interno
        if (args.length >= 2 && args[0].equalsIgnoreCase("tp")) {
            if (!(sender instanceof Player)) {
                plugin.getMessageManager().sendMessage(sender, "PlayerOnly");
                return true;
            }

            // Verificar permiso específico para tp (usando list ya que es similar)
            if (!sender.hasPermission("onyxpads.list")) {
                plugin.getMessageManager().sendMessage(sender, "InvalidPermission");
                return true;
            }

            try {
                int index = Integer.parseInt(args[1]) - 1;
                teleportToPad((Player) sender, index);
            } catch (NumberFormatException e) {
                plugin.getMessageManager().sendMessage(sender, "InvalidArguments", "command", label);
            }
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            return helpCommand(sender, label);
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                return createCommand(sender, args);
            case "delete":
                return deleteCommand(sender);
            case "set":
                return setCommand(sender, args);
            case "info":
                return infoCommand(sender);
            case "list":
                return listCommand(sender, args);
            case "reload":
                return reloadCommand(sender);
            default:
                plugin.getMessageManager().sendMessage(sender, "InvalidArguments", "command", label);
                return true;
        }
    }

    private void teleportToPad(Player player, int index) {
        List<JumpPad> pads = new ArrayList<>(plugin.getJumpPadManager().getAllJumpPads());
        if (index >= 0 && index < pads.size()) {
            JumpPad pad = pads.get(index);
            Location loc = pad.getLocation().clone();
            loc.add(0.5, 0, 0.5);
            player.teleport(loc);
            plugin.getMessageManager().sendMessage(player, "TeleportedToList", "id", String.valueOf(index + 1));
        }
    }

    private boolean helpCommand(CommandSender sender, String label) {
        plugin.getMessageManager().sendMessage(sender, "HelpHeader");

        List<String> helpCommands = plugin.getConfigManager().getMessages()
                .getStringList("Messages.HelpCommands");

        for (String cmdLine : helpCommands) {
            String formattedLine = cmdLine.replace("%command%", label);
            sender.sendMessage(ColorUtils.format(formattedLine));
        }

        plugin.getMessageManager().sendMessage(sender, "HelpFooter");
        return true;
    }

    private boolean createCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "PlayerOnly");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("onyxpads.create")) {
            plugin.getMessageManager().sendMessage(sender, "InvalidPermission");
            return true;
        }

        if (args.length < 2) {
            plugin.getMessageManager().sendMessage(sender, "InvalidArguments", "command", "jumppads");
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
            plugin.getMessageManager().sendMessage(sender, "InvalidTarget");
            return true;
        }

        if (!plugin.getJumpPadManager().isValidJumpPadLocation(targetBlock.getLocation())) {
            plugin.getMessageManager().sendMessage(sender, "InvalidTarget");
            return true;
        }

        if (plugin.getJumpPadManager().isJumpPad(targetBlock)) {
            plugin.getMessageManager().sendMessage(sender, "NotAJumpPad");
            return true;
        }

        double power;
        try {
            power = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            plugin.getMessageManager().sendMessage(sender, "InvalidPowerValue",
                    "max", String.valueOf(plugin.getConfigManager().getConfig().getInt("max-power", 5)));
            return true;
        }

        int maxPower = plugin.getConfigManager().getConfig().getInt("max-power", 5);
        if (power < 1 || power > maxPower) {
            plugin.getMessageManager().sendMessage(sender, "InvalidPowerValue", "max", String.valueOf(maxPower));
            return true;
        }

        if (!plugin.getJumpPadManager().canUsePower(player, power)) {
            int maxAllowedPower = plugin.getJumpPadManager().getMaxAllowedPower(player);
            plugin.getMessageManager().sendMessage(sender, "InvalidPower", "maxPower", String.valueOf(maxAllowedPower));
            return true;
        }

        double angle = plugin.getConfigManager().getConfig().getDouble("default-angle", 0);
        if (args.length >= 3) {
            try {
                angle = Double.parseDouble(args[2]);
                if (angle < 0 || angle > 360) {
                    plugin.getMessageManager().sendMessage(sender, "InvalidAngleValue");
                    return true;
                }
            } catch (NumberFormatException e) {
                plugin.getMessageManager().sendMessage(sender, "InvalidAngleValue");
                return true;
            }
        }

        JumpPad pad = new JumpPad(targetBlock.getLocation(), power, angle);
        plugin.getJumpPadManager().addJumpPad(pad);

        plugin.getMessageManager().sendMessage(sender, "Created");
        plugin.getMessageManager().sendMessage(sender, "CreatedInfo",
                "power", String.valueOf(power),
                "angle", String.valueOf(angle));

        return true;
    }

    private boolean deleteCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "PlayerOnly");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("onyxpads.delete")) {
            plugin.getMessageManager().sendMessage(sender, "InvalidPermission");
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            plugin.getMessageManager().sendMessage(sender, "NotAJumpPad");
            return true;
        }

        if (!plugin.getJumpPadManager().isJumpPad(targetBlock)) {
            plugin.getMessageManager().sendMessage(sender, "NotAJumpPad");
            return true;
        }

        plugin.getJumpPadManager().removeJumpPad(targetBlock.getLocation());
        plugin.getMessageManager().sendMessage(sender, "Deleted");

        return true;
    }

    private boolean setCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "PlayerOnly");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("onyxpads.set")) {
            plugin.getMessageManager().sendMessage(sender, "InvalidPermission");
            return true;
        }

        if (args.length < 2) {
            plugin.getMessageManager().sendMessage(sender, "InvalidArguments", "command", "jumppads");
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            plugin.getMessageManager().sendMessage(sender, "NotAJumpPad");
            return true;
        }

        JumpPad pad = plugin.getJumpPadManager().getJumpPad(targetBlock);
        if (pad == null) {
            plugin.getMessageManager().sendMessage(sender, "NotAJumpPad");
            return true;
        }

        String attribute = args[1].toLowerCase();

        if (!attributes.contains(attribute)) {
            plugin.getMessageManager().sendMessage(sender, "InvalidAttribute");
            return true;
        }

        if (args.length < 3) {
            // Remove attribute (set to default)
            switch (attribute) {
                case "power":
                    pad.setPower(plugin.getConfigManager().getConfig().getDouble("default-power", 2));
                    break;
                case "angle":
                    pad.setAngle(plugin.getConfigManager().getConfig().getDouble("default-angle", 0));
                    break;
                case "particles":
                    pad.setParticles(true);
                    break;
                case "sound":
                    pad.setSound(true);
                    break;
                case "permission":
                    pad.setRequiredPermission(null);
                    plugin.getMessageManager().sendMessage(sender, "PermissionRemoved");
                    return true;
            }
            plugin.getMessageManager().sendMessage(sender, "RemovedAttribute", "attribute", attribute);
        } else {
            // Set attribute
            String value = args[2];

            switch (attribute) {
                case "power":
                    try {
                        double power = Double.parseDouble(value);
                        int maxPower = plugin.getConfigManager().getConfig().getInt("max-power", 5);
                        if (power < 1 || power > maxPower) {
                            plugin.getMessageManager().sendMessage(sender, "InvalidPowerValue",
                                    "max", String.valueOf(maxPower));
                            return true;
                        }
                        pad.setPower(power);
                    } catch (NumberFormatException e) {
                        plugin.getMessageManager().sendMessage(sender, "InvalidPowerValue",
                                "max", String.valueOf(plugin.getConfigManager().getConfig().getInt("max-power", 5)));
                        return true;
                    }
                    break;

                case "angle":
                    try {
                        double angle = Double.parseDouble(value);
                        if (angle < 0 || angle > 360) {
                            plugin.getMessageManager().sendMessage(sender, "InvalidAngleValue");
                            return true;
                        }
                        pad.setAngle(angle);
                    } catch (NumberFormatException e) {
                        plugin.getMessageManager().sendMessage(sender, "InvalidAngleValue");
                        return true;
                    }
                    break;

                case "particles":
                    pad.setParticles(value.equalsIgnoreCase("true"));
                    break;

                case "sound":
                    pad.setSound(value.equalsIgnoreCase("true"));
                    break;

                case "permission":
                    if (value.equalsIgnoreCase("none") || value.equalsIgnoreCase("null") || value.isEmpty()) {
                        pad.setRequiredPermission(null);
                        plugin.getMessageManager().sendMessage(sender, "PermissionRemoved");
                        return true;
                    } else {
                        // Validación de permisos personalizados
                        boolean allowCustom = plugin.getConfigManager().getConfig()
                                .getBoolean("permissions.allow-custom-permissions", true);
                        List<String> predefined = plugin.getConfigManager().getConfig()
                                .getStringList("permissions.predefined-permissions");

                        // Si NO se permiten permisos personalizados, verificar que esté en la lista
                        if (!allowCustom && !predefined.contains(value) && !value.startsWith("onyxpads.")) {
                            plugin.getMessageManager().sendMessage(sender, "InvalidPermissionValue", "permission", value);
                            return true;
                        }

                        pad.setRequiredPermission(value);

                        if (predefined.contains(value) || value.startsWith("onyxpads.")) {
                            plugin.getMessageManager().sendMessage(sender, "PermissionSetPredefined", "permission", value);
                        } else {
                            plugin.getMessageManager().sendMessage(sender, "PermissionSetCustom", "permission", value);
                        }
                        return true;
                    }
            }

            plugin.getMessageManager().sendMessage(sender, "AddedAttribute",
                    "attribute", attribute, "value", value);
        }

        plugin.getJumpPadManager().saveAll();
        return true;
    }

    private boolean infoCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "PlayerOnly");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("onyxpads.info")) {
            plugin.getMessageManager().sendMessage(sender, "InvalidPermission");
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            plugin.getMessageManager().sendMessage(sender, "NotAJumpPad");
            return true;
        }

        JumpPad pad = plugin.getJumpPadManager().getJumpPad(targetBlock);
        if (pad == null) {
            plugin.getMessageManager().sendMessage(sender, "NotAJumpPad");
            return true;
        }

        Location loc = pad.getLocation();

        plugin.getMessageManager().sendMessage(sender, "InfoHeader", "id", pad.getId().toString().substring(0, 8));
        plugin.getMessageManager().sendMessage(sender, "InfoLocation",
                "world", loc.getWorld() != null ? loc.getWorld().getName() : "unknown",
                "x", String.valueOf(loc.getBlockX()),
                "y", String.valueOf(loc.getBlockY()),
                "z", String.valueOf(loc.getBlockZ()));
        plugin.getMessageManager().sendMessage(sender, "InfoPower", "power", String.valueOf(pad.getPower()));
        plugin.getMessageManager().sendMessage(sender, "InfoAngle", "angle", String.valueOf(pad.getAngle()));
        plugin.getMessageManager().sendMessage(sender, "InfoParticles", "particles", String.valueOf(pad.hasParticles()));
        plugin.getMessageManager().sendMessage(sender, "InfoSound", "sound", String.valueOf(pad.hasSound()));

        String permission = pad.hasPermission() ? pad.getRequiredPermission() : "Ninguno";
        plugin.getMessageManager().sendMessage(sender, "InfoPermission", "permission", permission);

        plugin.getMessageManager().sendMessage(sender, "InfoFooter");

        return true;
    }

    private boolean listCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("onyxpads.list")) {
            plugin.getMessageManager().sendMessage(sender, "InvalidPermission");
            return true;
        }

        int page = 1;
        if (args.length >= 2) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        List<JumpPad> pads = new ArrayList<>(plugin.getJumpPadManager().getAllJumpPads());
        int itemsPerPage = 10;
        int totalPages = (int) Math.ceil(pads.size() / (double) itemsPerPage);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, pads.size());

        plugin.getMessageManager().sendMessage(sender, "ListHeader",
                "page", String.valueOf(page),
                "total", String.valueOf(totalPages));

        if (pads.isEmpty()) {
            plugin.getMessageManager().sendMessage(sender, "ListEmpty");
        } else {
            for (int i = start; i < end; i++) {
                JumpPad pad = pads.get(i);
                Location loc = pad.getLocation();
                String worldName = loc.getWorld() != null ? loc.getWorld().getName() : "unknown";

                String entry = plugin.getMessageManager().getMessage("ListEntry")
                        .replace("%id%", String.valueOf(i + 1))
                        .replace("%world%", worldName)
                        .replace("%x%", String.valueOf(loc.getBlockX()))
                        .replace("%y%", String.valueOf(loc.getBlockY()))
                        .replace("%z%", String.valueOf(loc.getBlockZ()))
                        .replace("%power%", String.valueOf(pad.getPower()));

                if (sender instanceof Player) {
                    Component message = ColorUtils.format(entry);

                    String hover = plugin.getMessageManager().getMessage("ListEntryHover");

                    String tpCommand = "/onyxpads tp " + (i + 1);

                    message = message.hoverEvent(HoverEvent.showText(
                            ColorUtils.format(hover)
                    )).clickEvent(ClickEvent.runCommand(tpCommand));

                    sender.sendMessage(message);
                } else {
                    sender.sendMessage(ColorUtils.formatString(entry));
                }
            }
        }

        plugin.getMessageManager().sendMessage(sender, "ListFooter");
        if (totalPages > 1) {
            plugin.getMessageManager().sendMessage(sender, "ListNavigation");
        }

        return true;
    }

    private boolean reloadCommand(CommandSender sender) {
        if (!sender.hasPermission("onyxpads.reload")) {
            plugin.getMessageManager().sendMessage(sender, "InvalidPermission");
            return true;
        }

        plugin.getConfigManager().reloadConfigs();
        plugin.getMessageManager().reload();
        plugin.getJumpPadManager().loadAll();

        plugin.getMessageManager().sendMessage(sender, "ConfigurationsReloaded");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        Player player = (Player) sender;

        // Si no tiene permiso básico, no mostrar nada
        if (!player.hasPermission("onyxpads.use")) {
            return completions;
        }

        if (args.length == 1) {
            List<String> availableCommands = new ArrayList<>();
            for (String sub : subCommands) {
                if (hasPermissionForSubCommand(player, sub)) {
                    availableCommands.add(sub);
                }
            }
            StringUtil.copyPartialMatches(args[0], availableCommands, completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                if (player.hasPermission("onyxpads.set")) {
                    StringUtil.copyPartialMatches(args[1], attributes, completions);
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                if (player.hasPermission("onyxpads.create")) {
                    completions.add("<power>");
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                if (player.hasPermission("onyxpads.list")) {
                    completions.add("<page>");
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set") && player.hasPermission("onyxpads.set")) {
                String attribute = args[1].toLowerCase();
                if (attribute.equals("particles") || attribute.equals("sound")) {
                    StringUtil.copyPartialMatches(args[2], booleanValues, completions);
                } else if (attribute.equals("power")) {
                    int maxPower = plugin.getConfigManager().getConfig().getInt("max-power", 5);
                    for (int i = 1; i <= maxPower; i++) {
                        completions.add(String.valueOf(i));
                    }
                } else if (attribute.equals("angle")) {
                    completions.add("0");
                    completions.add("45");
                    completions.add("90");
                    completions.add("180");
                    completions.add("270");
                    completions.add("360");
                } else if (attribute.equals("permission")) {
                    List<String> predefined = plugin.getConfigManager().getConfig()
                            .getStringList("permissions.predefined-permissions");
                    completions.addAll(predefined);
                    completions.add("none");
                }
            } else if (args[0].equalsIgnoreCase("create") && player.hasPermission("onyxpads.create")) {
                completions.add("0");
                completions.add("45");
                completions.add("90");
            }
        }

        return completions;
    }

    private boolean hasPermissionForSubCommand(Player player, String subCommand) {
        switch (subCommand) {
            case "help":
                return true; // help no requiere permiso específico, solo el base
            case "create":
                return player.hasPermission("onyxpads.create");
            case "delete":
                return player.hasPermission("onyxpads.delete");
            case "set":
                return player.hasPermission("onyxpads.set");
            case "info":
                return player.hasPermission("onyxpads.info");
            case "list":
                return player.hasPermission("onyxpads.list");
            case "reload":
                return player.hasPermission("onyxpads.reload");
            default:
                return false;
        }
    }
}