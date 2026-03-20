# 🚀 OnyxPads - Advanced Jump Pads Plugin

[![Version](https://img.shields.io/badge/version-1.0.2-blue.svg)](https://github.com/LichiDev/OnyxPads)
[![API Version](https://img.shields.io/badge/API-1.21-green.svg)](https://papermc.io)
[![License](https://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

A powerful and customizable jump pad plugin for Minecraft servers, supporting multiple languages and advanced features.

## ✨ Features

- 🎮 **Easy to use** - Simple commands to create and manage jump pads
- 🌍 **Multi-language support** - 10+ languages including English, Spanish, French, German, Japanese, and more
- ⚙️ **Highly customizable** - Adjust power, angle, particles, sounds, and permissions per jump pad
- 🛡️ **Protection system** - Prevent breaking, block placement, and explosions
- ⚔️ **Combat integration** - Optional combat cooldown for PvP servers
- 🎨 **Modern formatting** - Support for HEX colors, gradients, and MiniMessage
- 🔧 **Permission-based** - Granular control over who can use, create, and modify jump pads

## 📋 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/onyxpads help` | Shows help message | `onyxpads.use` |
| `/onyxpads create <power> [angle]` | Creates a jump pad | `onyxpads.create` |
| `/onyxpads delete` | Deletes the targeted jump pad | `onyxpads.delete` |
| `/onyxpads set <attribute> [value]` | Modifies jump pad attributes | `onyxpads.set` |
| `/onyxpads info` | Shows jump pad information | `onyxpads.info` |
| `/onyxpads list [page]` | Lists all jump pads | `onyxpads.list` |
| `/onyxpads reload` | Reloads the configuration | `onyxpads.reload` |

### Attributes for `/set` command:
- `power` - Set jump power (1-10)
- `angle` - Set launch angle (0-360°)
- `particles` - Enable/disable particles
- `sound` - Enable/disable sounds
- `permission` - Set required permission

## 🔧 Installation

1. Download the latest `OnyxPads.jar` from [Releases](https://github.com/LichiDev/OnyxPads/releases)
2. Place it in your server's `plugins` folder
3. Restart your server (or run `/reload confirm`)
4. Configure the plugin in `plugins/OnyxPads/config.yml`
5. Enjoy! 🎉

## 📝 Configuration

### Basic Settings
```yaml
# Language selection
language: en

# Maximum power limit
max-power: 5

# Default values for new jump pads
default-power: 2
default-angle: 0