package dev.lichi.onyxPads.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Convierte un string con formato MiniMessage o legacy a Component
     */
    public static Component format(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        // Verificar si el mensaje usa principalmente MiniMessage o legacy
        boolean hasMiniMessage = message.contains("<") && message.contains(">") &&
                (message.contains("</") || message.contains("/>") ||
                        message.contains("<gradient") || message.contains("<rainbow") ||
                        message.contains("<color") || message.contains("<#"));
        boolean hasLegacy = message.contains("&") || message.contains("§");

        try {
            if (hasMiniMessage && !hasLegacy) {
                // Solo MiniMessage
                return MINI_MESSAGE.deserialize(message);
            } else if (hasLegacy && !hasMiniMessage) {
                // Solo legacy
                return LEGACY_SERIALIZER.deserialize(message);
            } else {
                // Mezcla - convertir legacy a MiniMessage
                String converted = convertLegacyToMiniMessage(message);
                return MINI_MESSAGE.deserialize(converted);
            }
        } catch (Exception e) {
            // Si falla MiniMessage, intentar con legacy
            try {
                return LEGACY_SERIALIZER.deserialize(message);
            } catch (Exception ex) {
                // Si todo falla, devolver texto plano
                return Component.text(message);
            }
        }
    }

    /**
     * Versión que retorna String (para compatibilidad)
     */
    public static String formatString(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        return translateColorCodes(message);
    }

    /**
     * Convierte códigos legacy (&) a formato MiniMessage
     */
    private static String convertLegacyToMiniMessage(String message) {
        StringBuilder result = new StringBuilder();
        int length = message.length();
        boolean inTag = false;

        for (int i = 0; i < length; i++) {
            char c = message.charAt(i);

            if (c == '<') {
                inTag = true;
                result.append(c);
            } else if (c == '>') {
                inTag = false;
                result.append(c);
            } else if (c == '&' && i + 1 < length && !inTag) {
                char code = message.charAt(i + 1);
                String replacement = getMiniMessageReplacement(code);

                if (replacement != null) {
                    result.append(replacement);
                    i++; // Saltar el código
                } else {
                    // Si no es un código válido, mantener el & literal
                    result.append('&');
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private static String getMiniMessageReplacement(char code) {
        switch (code) {
            // Colores básicos
            case '0': return "<black>";
            case '1': return "<dark_blue>";
            case '2': return "<dark_green>";
            case '3': return "<dark_aqua>";
            case '4': return "<dark_red>";
            case '5': return "<dark_purple>";
            case '6': return "<gold>";
            case '7': return "<gray>";
            case '8': return "<dark_gray>";
            case '9': return "<blue>";
            case 'a': return "<green>";
            case 'b': return "<aqua>";
            case 'c': return "<red>";
            case 'd': return "<light_purple>";
            case 'e': return "<yellow>";
            case 'f': return "<white>";

            // Formatos
            case 'l': return "<bold>";
            case 'o': return "<italic>";
            case 'n': return "<underlined>";
            case 'm': return "<strikethrough>";
            case 'k': return "<obfuscated>";
            case 'r': return "<reset>";

            default: return null;
        }
    }

    /**
     * Traduce códigos de color (&) a § (para compatibilidad)
     */
    public static String translateColorCodes(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        java.util.regex.Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);

        return buffer.toString().replace('&', '§');
    }

    /**
     * Convierte una lista de strings a una lista de Components
     */
    public static Component[] formatList(String... messages) {
        Component[] components = new Component[messages.length];
        for (int i = 0; i < messages.length; i++) {
            components[i] = format(messages[i]);
        }
        return components;
    }

    /**
     * Limpia todos los códigos de color de un string
     */
    public static String stripColor(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        return PlainTextComponentSerializer.plainText().serialize(format(message));
    }
}