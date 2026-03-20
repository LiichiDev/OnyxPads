package dev.lichi.onyxPads;

import dev.lichi.onyxPads.commands.JumpPadsCommand;
import dev.lichi.onyxPads.listeners.JumpPadListener;
import dev.lichi.onyxPads.managers.ConfigManager;
import dev.lichi.onyxPads.managers.JumpPadManager;
import dev.lichi.onyxPads.managers.MessageManager;
import dev.lichi.onyxPads.models.JumpPad;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class OnyxPads extends JavaPlugin {

    private static OnyxPads instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private JumpPadManager jumpPadManager;

    @Override
    public void onEnable() {
        instance = this;

        // Mostrar mensaje de inicio
        sendStartupMessage();

        // Registrar clases serializables
        ConfigurationSerialization.registerClass(JumpPad.class);

        // Inicializa el administrador de configuración
        this.configManager = new ConfigManager(this);

        // Carga las configuraciones
        configManager.loadConfigs();

        // Inicializa el administrador de mensajes
        this.messageManager = new MessageManager(this);

        // Inicializa el administrador de JumpPads
        this.jumpPadManager = new JumpPadManager(this);

        // Registra comandos
        getCommand("onyxpads").setExecutor(new JumpPadsCommand(this));

        // Registra listeners
        getServer().getPluginManager().registerEvents(new JumpPadListener(this), this);
    }

    @Override
    public void onDisable() {
        // Mostrar mensaje de cierre
        sendShutdownMessage();

        // Guarda los JumpPads
        if (jumpPadManager != null) {
            jumpPadManager.saveAll();
        }
    }

    private String getServerPlatform() {
        String serverName = Bukkit.getServer().getName();
        String mcVersion = Bukkit.getMinecraftVersion();

        return serverName + " " + mcVersion;
    }

    private void sendStartupMessage() {
        String version = getDescription().getVersion();
        String platform = getServerPlatform();

        Bukkit.getConsoleSender().sendMessage(new String[] {
                "§8§m|==================================================================================|",
                "§8",
                "§8    §4▄████▄ ███  ██ ██  ██ ██  ██   §c█████▄ ▄████▄ ████▄  ▄█████ ",
                "§8    §4██  ██ ██ ▀▄██  ▀██▀   ████    §c██▄▄█▀ ██▄▄██ ██  ██ ▀▀▀▄▄▄ ",
                "§8    §4▀████▀ ██   ██   ██   ██  ██   §c██     ██  ██ ████▀  █████▀ ",
                "§8",
                "§8    §f• Plugin: §cOnyxPads §8| §fVersion: §a" + version + " §8| §fAuthor: §eLichiDev",
                "§8    §f• Platform: §6" + platform,
                "§8    §f• Loading configuration...",
                "§8§m|==================================================================================|",
        });
    }

    private void sendShutdownMessage() {
        Bukkit.getConsoleSender().sendMessage(new String[] {
                "§8§m|==========================================================|",
                "§8",
                "§8    §c ▄████  ▄████▄ ▄████▄ ████▄  █████▄ ██  ██ ██████ ",
                "§8    §c██  ▄▄▄ ██  ██ ██  ██ ██  ██ ██▄▄██  ▀██▀  ██▄▄  ",
                "§8    §c ▀███▀  ▀████▀ ▀████▀ ████▀  ██▄▄█▀   ██   ██▄▄▄▄ ",
                "§8",
                "§8    §2✓ §aOnyxPads has been successfully disabled",
                "§8    §7• Thank you for using OnyxPads!",
                "§8    §7• Developed by: §eLichiDev",
                "§8    §7• GitHub: §9https://github.com/LichiDev/OnyxPads",
                "§8    §7• See you next time! §b☺",
                "§8§m|==========================================================|",
        });
    }

    public static OnyxPads getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public JumpPadManager getJumpPadManager() {
        return jumpPadManager;
    }
}