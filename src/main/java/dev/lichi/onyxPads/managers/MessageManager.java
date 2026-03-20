package dev.lichi.onyxPads.managers;

import dev.lichi.onyxPads.OnyxPads;
import dev.lichi.onyxPads.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    private final OnyxPads plugin;
    private String prefix;
    private String currentLanguage;

    public MessageManager(OnyxPads plugin) {
        this.plugin = plugin;
        loadPrefix();
    }

    private void loadPrefix() {
        FileConfiguration messages = plugin.getConfigManager().getMessages();
        if (messages != null) {
            this.prefix = messages.getString("Prefix", "<gradient:#ff1a12:#734845:#ff1a12><b>ONYXPADS</b></gradient> &8▹");
        } else {
            this.prefix = "<gradient:#ff1a12:#734845:#ff1a12><b>ONYXPADS</b></gradient> &8▹";
            plugin.getLogger().warning("Messages configuration is null, using default prefix");
        }
        this.currentLanguage = plugin.getConfigManager().getCurrentLanguage();
    }

    public void sendMessage(CommandSender sender, String key, String... placeholders) {
        FileConfiguration messages = plugin.getConfigManager().getMessages();
        if (messages == null) {
            sender.sendMessage("§cError: Messages configuration not loaded! Try /onyxpads reload");
            return;
        }

        // Check if it's a list or single message
        if (messages.isList("Messages." + key)) {
            List<String> messageList = messages.getStringList("Messages." + key);
            List<Component> components = new ArrayList<>();

            for (String message : messageList) {
                if (message == null) continue;

                // Process placeholders
                message = processPlaceholders(message, placeholders);

                // Replace prefix
                message = message.replace("%prefix%", prefix);
                message = message.replace("%lang%", currentLanguage);

                // Add to components
                if (message.isEmpty()) {
                    components.add(Component.empty());
                } else {
                    components.add(ColorUtils.format(message));
                }
            }

            // Send all messages
            for (Component component : components) {
                sender.sendMessage(component);
            }
        } else {
            String message = messages.getString("Messages." + key);

            if (message == null) {
                // Try to get from default language (English)
                FileConfiguration defaultMessages = plugin.getConfigManager().getMessagesEn();
                if (defaultMessages != null) {
                    message = defaultMessages.getString("Messages." + key);
                }

                if (message == null) {
                    plugin.getLogger().warning("Missing message key: " + key + " for language: " + currentLanguage);
                    sender.sendMessage("§cMissing message: " + key + " - Try /onyxpads reload");
                    return;
                }
            }

            // Process placeholders
            message = processPlaceholders(message, placeholders);

            // Replace prefix
            message = message.replace("%prefix%", prefix);
            message = message.replace("%lang%", currentLanguage);

            // Send message if not empty
            if (!message.isEmpty()) {
                sender.sendMessage(ColorUtils.format(message));
            }
        }
    }

    private String processPlaceholders(String message, String... placeholders) {
        String result = message;
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                result = result.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
            }
        }
        return result;
    }

    public void sendMessage(CommandSender sender, String key) {
        sendMessage(sender, key, new String[0]);
    }

    public String getMessage(String key) {
        FileConfiguration messages = plugin.getConfigManager().getMessages();
        if (messages == null) return "&cError loading message";

        if (messages.isList("Messages." + key)) {
            List<String> list = messages.getStringList("Messages." + key);
            return String.join("\n", list);
        } else {
            String message = messages.getString("Messages." + key, "");
            return message.replace("%prefix%", prefix).replace("%lang%", currentLanguage);
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void reload() {
        loadPrefix();
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }
}