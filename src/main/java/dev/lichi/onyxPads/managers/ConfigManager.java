package dev.lichi.onyxPads.managers;

import dev.lichi.onyxPads.OnyxPads;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final OnyxPads plugin;
    private FileConfiguration config;
    private FileConfiguration jumpPads;

    private final Map<String, FileConfiguration> messages = new HashMap<>();
    private final Map<String, File> messageFiles = new HashMap<>();

    private String currentLanguage;
    private File messagesFolder;

    // Lista de idiomas soportados
    private final String[] supportedLanguages = {"en", "es", "fr", "de", "it", "pt", "ru", "zh", "ja", "ko"};

    public ConfigManager(OnyxPads plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        // Config.yml
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            plugin.getLogger().info("config.yml created successfully.");
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        // Load defaults for config
        InputStream defConfigStream = plugin.getResource("config.yml");
        if (defConfigStream != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
            config.options().copyDefaults(true);
            try {
                config.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save config.yml!");
                e.printStackTrace();
            }
        }

        // Get language setting
        currentLanguage = config.getString("language", "en");

        // Create messages folder if it doesn't exist
        createMessagesFolder();

        // Load all message files (with auto-regeneration if missing)
        loadMessageFiles();

        // Jump pads data file
        File jumpPadsFile = new File(plugin.getDataFolder(), "jumppads.yml");
        if (!jumpPadsFile.exists()) {
            try {
                jumpPadsFile.createNewFile();
                FileConfiguration empty = YamlConfiguration.loadConfiguration(jumpPadsFile);
                empty.set("jumppads", null);
                empty.save(jumpPadsFile);
                plugin.getLogger().info("jumppads.yml created successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        jumpPads = YamlConfiguration.loadConfiguration(jumpPadsFile);
    }

    private void createMessagesFolder() {
        messagesFolder = new File(plugin.getDataFolder(), "messages");
        if (!messagesFolder.exists()) {
            messagesFolder.mkdirs();
            plugin.getLogger().info("Messages folder created.");
        }
    }

    private void loadMessageFiles() {
        messages.clear();
        messageFiles.clear();

        // Ensure folder exists
        createMessagesFolder();

        // Check if folder is empty or was deleted
        File[] existingFiles = messagesFolder.listFiles((dir, name) -> name.startsWith("messages_") && name.endsWith(".yml"));

        if (existingFiles == null || existingFiles.length == 0) {
            plugin.getLogger().warning("Messages folder is empty or missing! Regenerating default language files...");
            regenerateAllLanguageFiles();
        } else {
            // Load existing files
            for (String lang : supportedLanguages) {
                loadLanguageFile(lang);
            }

            // Check if we have at least English (fallback)
            if (!messages.containsKey("en")) {
                plugin.getLogger().warning("English language file missing! Regenerating...");
                regenerateLanguageFile("en");
            }
        }

        plugin.getLogger().info("Loaded " + messages.size() + " language files.");
    }

    private void loadLanguageFile(String lang) {
        String fileName = "messages_" + lang + ".yml";
        File langFile = new File(messagesFolder, fileName);

        if (langFile.exists()) {
            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
            messages.put(lang, langConfig);
            messageFiles.put(lang, langFile);
        } else {
            // Try to copy from resources
            String resourcePath = "messages/" + fileName;
            InputStream resourceStream = plugin.getResource(resourcePath);

            if (resourceStream != null) {
                plugin.saveResource(resourcePath, false);
                // Move to messages folder if saved in root
                File tempFile = new File(plugin.getDataFolder(), fileName);
                if (tempFile.exists()) {
                    tempFile.renameTo(langFile);
                }
                // Load the newly created file
                if (langFile.exists()) {
                    FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
                    messages.put(lang, langConfig);
                    messageFiles.put(lang, langFile);
                    plugin.getLogger().info("Created " + fileName + " from resources.");
                }
            } else {
                // Create default file if resource doesn't exist
                createDefaultLanguageFile(lang, langFile);
            }
        }
    }

    private void createDefaultLanguageFile(String lang, File langFile) {
        String fileName = langFile.getName(); // Obtener el nombre del archivo
        try {
            langFile.createNewFile();
            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);

            // Set default English content as base
            langConfig.set("Prefix", "<gradient:#ff1a12:#734845:#ff1a12><b>ONYXPADS</b></gradient> &8▹");
            langConfig.set("Messages.PlayerOnly", "%prefix% &fThis command can only be executed by a player!");
            langConfig.set("Messages.InvalidPermission", "%prefix% &fYou do not have permission to use this command!");
            langConfig.set("Messages.InvalidArguments", "%prefix% &fInvalid arguments! &fUse &e/%command% help &ffor more help.");
            langConfig.set("Messages.InvalidTarget", "%prefix% &fThis block is not registered to be used as a jump pad!");
            langConfig.set("Messages.InvalidPower", "%prefix% &fYou don't have permission to use power greater than &e%maxPower%&f!");
            langConfig.set("Messages.ConfigurationsReloaded", "%prefix% &fConfiguration reloaded successfully!");
            langConfig.set("Messages.InCombat", "%prefix% &fYou cannot use jump pads while in combat! &fWait &e%time% &fseconds.");
            langConfig.set("Messages.NoJumpPadPermission", "%prefix% &fYou don't have permission to use this jump pad! &fRequired: &e%permission%");
            langConfig.set("Messages.PermissionSetPredefined", "%prefix% &fPermission &e%permission% &fset for this jump pad!");
            langConfig.set("Messages.PermissionSetCustom", "%prefix% &fCustom permission &e%permission% &fset for this jump pad!");
            langConfig.set("Messages.PermissionRemoved", "%prefix% &ePermission removed &7- now anyone can use this jump pad!");
            langConfig.set("Messages.CannotBreakJumpPad", "%prefix% &fYou cannot break a jump pad! &fUse &e/onyxpads delete &fto remove it.");
            langConfig.set("Messages.CannotPlaceOnJumpPad", "%prefix% &fYou cannot place blocks on a jump pad!");
            langConfig.set("Messages.CannotInteractWithJumpPad", "%prefix% &fYou cannot interact with this jump pad!");
            langConfig.set("Messages.Created", "%prefix% &fJump pad created successfully!");
            langConfig.set("Messages.CreatedInfo", "%prefix% &fPower: &e%power% &8| &fAngle: &e%angle%°");
            langConfig.set("Messages.Deleted", "%prefix% &fJump pad deleted successfully!");
            langConfig.set("Messages.NotAJumpPad", "%prefix% &fThe selected block is not a jump pad!");
            langConfig.set("Messages.InvalidAttribute", "%prefix% &fInvalid attribute! Use: &epower&f, &eangle&f, &eparticles&f, &esound&f, &epermission&f.");
            langConfig.set("Messages.AddedAttribute", "%prefix% &fAttribute &e%attribute% &fset to &e%value%&f!");
            langConfig.set("Messages.RemovedAttribute", "%prefix% &fAttribute &e%attribute% &frestored to default value!");
            langConfig.set("Messages.InvalidPowerValue", "%prefix% &fPower must be a number between &e1 &fand &6%max%&f!");
            langConfig.set("Messages.InvalidAngleValue", "%prefix% &fAngle must be a number between &e0 &fand &6360&f!");
            langConfig.set("Messages.InfoHeader", "&6═════ [ &eJump Pad Info &7(ID: %id%) &6]═════");
            langConfig.set("Messages.InfoLocation", " &d➤ &7World: &b%world% &7| X: &b%x% &7| Y: &b%y% &7| Z: &b%z%");
            langConfig.set("Messages.InfoPower", " &d➤ &7Power: &a%power%");
            langConfig.set("Messages.InfoAngle", " &d➤ &7Angle: &a%angle%°");
            langConfig.set("Messages.InfoParticles", " &d➤ &7Particles: &9%particles%");
            langConfig.set("Messages.InfoSound", " &d➤ &7Sound: &9%sound%");
            langConfig.set("Messages.InfoPermission", " &d➤ &7Required permission: &c%permission%");
            langConfig.set("Messages.InfoFooter", "&6═════════════════════════════");
            langConfig.set("Messages.ListHeader", "&6════════ [ &eJump Pads &7(Page %page%/%total%) &6]════════");
            langConfig.set("Messages.ListEntry", "&7%id%. &7[&e%world%&7] &7X: &e%x% &7Y: &e%y% &7Z: &e%z% &7- &ePower: %power% &7- &a[CLICK TO TP]");
            langConfig.set("Messages.ListEntryHover", "&eClick to teleport to the jump pad");
            langConfig.set("Messages.ListEmpty", "&#FFD700➤ &7There are no jump pads configured.");
            langConfig.set("Messages.ListFooter", "&6═════════════════════════════════");
            langConfig.set("Messages.ListNavigation", "&#FFD700➤ &7Use &e/jumppads list <page> &7to navigate");
            langConfig.set("Messages.TeleportedToList", "%prefix% &fTeleported to jump pad &e#%id%&f!");
            langConfig.set("Messages.UnsafeTeleport", "%prefix% &cNo safe location found to teleport!");
            langConfig.set("Messages.HelpHeader", "<gradient:#FF6B6B:#DC143C>╠═══════════════════════════════╣</gradient>");
            langConfig.set("Messages.HelpFooter", "<gradient:#FF6B6B:#DC143C>╠═══════════════════════════════╣</gradient>");

            // Help commands list
            String[] helpCommands = {
                    "",
                    "  <gradient:#ff1a12:#734845:#ff1a12><b>OnyxPads</b></gradient> &8- &7By @LichiDev",
                    "",
                    "  &e/%command% help &8• &7Shows this help message.",
                    "  &e/%command% create <power> [angle] &8• &7Creates a jump pad on the block you're looking at.",
                    "  &e/%command% delete &8• &7Deletes the jump pad you're looking at.",
                    "  &e/%command% set <attribute> [value] &8• &7Modifies the jump pad attributes.",
                    "  &e/%command% info &8• &7Shows information about the jump pad you're looking at.",
                    "  &e/%command% list [page] &8• &7Lists all jump pads.",
                    "  &e/%command% reload &8• &7Reloads all configuration files.",
                    ""
            };
            langConfig.set("Messages.HelpCommands", helpCommands);

            langConfig.save(langFile);
            messages.put(lang, langConfig);
            messageFiles.put(lang, langFile);
            plugin.getLogger().info("Created default " + fileName);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not create default " + fileName);
            e.printStackTrace();
        }
    }

    private void regenerateAllLanguageFiles() {
        for (String lang : supportedLanguages) {
            regenerateLanguageFile(lang);
        }
    }

    private void regenerateLanguageFile(String lang) {
        File langFile = new File(messagesFolder, "messages_" + lang + ".yml");
        String fileName = langFile.getName();

        // Delete if exists (to ensure clean regeneration)
        if (langFile.exists()) {
            langFile.delete();
        }

        // Try to copy from resources first
        String resourcePath = "messages/messages_" + lang + ".yml";
        InputStream resourceStream = plugin.getResource(resourcePath);

        if (resourceStream != null) {
            plugin.saveResource(resourcePath, false);
            // Move to messages folder if saved in root
            File tempFile = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
            if (tempFile.exists()) {
                tempFile.renameTo(langFile);
            }
        }

        // If still doesn't exist, create default
        if (!langFile.exists()) {
            createDefaultLanguageFile(lang, langFile);
        } else {
            // Load the file
            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
            messages.put(lang, langConfig);
            messageFiles.put(lang, langFile);
            plugin.getLogger().info("Regenerated " + fileName);
        }
    }

    public void reloadConfigs() {
        plugin.getLogger().info("Reloading configuration files...");

        // Reload config.yml
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        // Update language setting
        currentLanguage = config.getString("language", "en");

        // Check if messages folder exists, if not, recreate everything
        messagesFolder = new File(plugin.getDataFolder(), "messages");
        if (!messagesFolder.exists()) {
            plugin.getLogger().warning("Messages folder was deleted! Regenerating all language files...");
            messagesFolder.mkdirs();
            regenerateAllLanguageFiles();
        } else {
            // Check if folder is empty
            File[] files = messagesFolder.listFiles((dir, name) -> name.startsWith("messages_") && name.endsWith(".yml"));
            if (files == null || files.length == 0) {
                plugin.getLogger().warning("Messages folder is empty! Regenerating all language files...");
                regenerateAllLanguageFiles();
            } else {
                // Reload all message files
                reloadMessageFiles();
            }
        }

        // Reload jumppads.yml
        File jumpPadsFile = new File(plugin.getDataFolder(), "jumppads.yml");
        if (!jumpPadsFile.exists()) {
            try {
                jumpPadsFile.createNewFile();
                FileConfiguration empty = YamlConfiguration.loadConfiguration(jumpPadsFile);
                empty.set("jumppads", null);
                empty.save(jumpPadsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        jumpPads = YamlConfiguration.loadConfiguration(jumpPadsFile);

        plugin.getLogger().info("Configuration files reloaded successfully.");
    }

    private void reloadMessageFiles() {
        messages.clear();
        messageFiles.clear();

        for (String lang : supportedLanguages) {
            loadLanguageFile(lang);
        }

        // Ensure we have at least English
        if (!messages.containsKey("en")) {
            plugin.getLogger().warning("English language file missing after reload! Regenerating...");
            regenerateLanguageFile("en");
        }

        plugin.getLogger().info("Reloaded " + messages.size() + " language files.");
    }

    public void saveJumpPads() {
        File jumpPadsFile = new File(plugin.getDataFolder(), "jumppads.yml");
        try {
            jumpPads.save(jumpPadsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save jumppads.yml!");
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return getMessages(currentLanguage);
    }

    public FileConfiguration getMessages(String lang) {
        FileConfiguration langConfig = messages.get(lang.toLowerCase());
        if (langConfig == null) {
            // Fallback to English
            langConfig = messages.get("en");
            if (langConfig == null) {
                // Emergency fallback - create in-memory config
                plugin.getLogger().severe("CRITICAL: No language files available! Creating emergency fallback...");
                langConfig = new YamlConfiguration();
                langConfig.set("Prefix", "<gradient:#ff1a12:#734845:#ff1a12><b>ONYXPADS</b></gradient> &8▹");
                langConfig.set("Messages.PlayerOnly", "%prefix% &fThis command can only be executed by a player!");
            }
        }
        return langConfig;
    }

    public FileConfiguration getMessagesEn() {
        return getMessages("en");
    }

    public FileConfiguration getMessagesEs() {
        return getMessages("es");
    }

    public FileConfiguration getJumpPads() {
        return jumpPads;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public Map<String, FileConfiguration> getAllMessages() {
        return messages;
    }

    /**
     * Forza la regeneración de todos los archivos de idioma
     */
    public void forceRegenerateAllLanguages() {
        plugin.getLogger().info("Force regenerating all language files...");
        regenerateAllLanguageFiles();
        plugin.getLogger().info("All language files regenerated successfully.");
    }

    /**
     * Verifica si un idioma específico está disponible
     */
    public boolean isLanguageAvailable(String lang) {
        return messages.containsKey(lang.toLowerCase());
    }
}