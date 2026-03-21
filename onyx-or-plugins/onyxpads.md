---
description: 📚 Welcome to the official documentation for OnyxPads, the advance
---

# OnyxPads

### 📖 Table of Contents

* Introduction
* Installation
* Configuration
* Commands
* Permissions
* Language Support
* FAQ
* Support

***

### 🚀 Introduction

**OnyxPads** is a fully customizable jump pads plugin for Minecraft Paper 1.21+ servers. Create launch pads with:

* ⚡ **Adjustable power** (1-10)
* 📐 **Launch angle** (0-360°)
* ✨ **Particle effects** (fully configurable)
* 🔊 **Sound effects** (customizable)
* 🔒 **Per-pad permissions** for VIP zones
* 🛡️ **Protection system** (anti-break, anti-explosion, anti-place)
* ⚔️ **Optional combat system**
* 🌍 **Multilingual support** (10 languages)

***

### 📥 Installation

#### Requirements

| Requirement      | Specification                |
| ---------------- | ---------------------------- |
| **Server**       | Paper 1.21+ / Purpur / Folia |
| **Java**         | Java 21 or higher            |
| **Dependencies** | None                         |

#### Installation Steps

1. **Download** `OnyxPads.jar`
2. **Place** the file in your server's `plugins/` folder
3. **Restart** or reload your server
4. **Configure** files in `plugins/OnyxPads/`
5. **Run** `/onyxpads reload` to apply changes

***

### ⚙️ Configuration

#### File Structure&#x20;

```
plugins/OnyxPads/
├── config.yml           # Main configuration
├── jumppads.yml         # Jump pads data (do not edit manually)
└── messages/
    ├── messages_en.yml  # English
    ├── messages_es.yml  # Spanish
    ├── messages_pt.yml  # Portuguese
    ├── messages_fr.yml  # French
    ├── messages_de.yml  # German
    ├── messages_it.yml  # Italian
    ├── messages_ru.yml  # Russian
    ├── messages_zh.yml  # Chinese
    ├── messages_ja.yml  # Japanese
    └── messages_ko.yml  # Korean
```

#### Main Configuration (`config.yml`)

yaml

```
# Plugin language
language: en

# Maximum power limit (1-10)
max-power: 5

# Default values for new jump pads
default-power: 2
default-angle: 0

# Protection settings
protection:
  prevent-breaking: true          # Prevent breaking jump pads
  prevent-block-place: true       # Prevent placing blocks on pads
  prevent-interact: true          # Prevent interactions
  protect-from-explosions: true   # Protect from explosions

# Combat system
combat:
  enabled: false                  # Enable/disable
  cooldown: 5                     # Cooldown in seconds

# Particle effects
particles:
  enabled: true
  type: "FLAME"                   # Particle type
  count: 20                       # Number of particles
  speed: 0.1                      # Particle speed

# Sound effects
sounds:
  enabled: true
  use: "ENTITY_ENDER_DRAGON_SHOOT"
  volume: 0.5
  pitch: 1.0

# Allowed blocks for creating jump pads
allowed-blocks:
  - STONE_PRESSURE_PLATE
  - OAK_PRESSURE_PLATE
  - SLIME_BLOCK
  - HONEY_BLOCK
  # Add more blocks as needed

# Permission settings
permissions:
  default-permission: "onyxpads.use"
  allow-custom-permissions: true
  predefined-permissions:
    - "onyxpads.use"
    - "onyxpads.vip"
    - "onyxpads.staff"
```

***

### 📝 Commands

#### Main Commands

| Command                             | Description              | Permission        |
| ----------------------------------- | ------------------------ | ----------------- |
| `/onyxpads help`                    | Shows help menu          | `onyxpads.use`    |
| `/onyxpads create <power> [angle]`  | Creates a jump pad       | `onyxpads.create` |
| `/onyxpads delete`                  | Deletes the targeted pad | `onyxpads.delete` |
| `/onyxpads set <attribute> [value]` | Modifies pad attributes  | `onyxpads.set`    |
| `/onyxpads info`                    | Shows pad information    | `onyxpads.info`   |
| `/onyxpads list [page]`             | Lists all jump pads      | `onyxpads.list`   |
| `/onyxpads reload`                  | Reloads configuration    | `onyxpads.reload` |

#### Aliases

The plugin also responds to these aliases:

* `/jumppads`
* `/jp`
* `/opads`
* `/pads`

#### Set Command Attributes

| Attribute    | Description              | Values                       |
| ------------ | ------------------------ | ---------------------------- |
| `power`      | Launch power             | 1-10                         |
| `angle`      | Launch angle             | 0-360                        |
| `particles`  | Enable/disable particles | true / false                 |
| `sound`      | Enable/disable sound     | true / false                 |
| `permission` | Required permission      | any string, "none" to remove |

**Examples:**

```
/onyxpads set power 5
/onyxpads set angle 45
/onyxpads set particles false
/onyxpads set permission onyxpads.vip
/onyxpads set permission none
```

***

### 🔐 Permissions

#### Permission Nodes

| Permission               | Description                     | Default |
| ------------------------ | ------------------------------- | ------- |
| `onyxpads.use`           | Base permission to use commands | OP      |
| `onyxpads.create`        | Create jump pads                | OP      |
| `onyxpads.delete`        | Delete jump pads                | OP      |
| `onyxpads.set`           | Modify jump pads                | OP      |
| `onyxpads.info`          | View pad information            | OP      |
| `onyxpads.list`          | List all jump pads              | OP      |
| `onyxpads.reload`        | Reload configuration            | OP      |
| `onyxpads.bypass.combat` | Bypass combat cooldown          | false   |

#### Power Permissions

Control which players can use specific power levels:

| Permission         | Description              |
| ------------------ | ------------------------ |
| `onyxpads.power.1` | Can use power level 1    |
| `onyxpads.power.2` | Can use power level 2    |
| `onyxpads.power.3` | Can use power level 3    |
| `onyxpads.power.4` | Can use power level 4    |
| `onyxpads.power.5` | Can use power level 5    |
| `onyxpads.power.*` | Can use all power levels |

#### Per-Pad Permissions

You can set a custom permission for each jump pad using:

bash

```
/onyxpads set permission <permission>
```

Only players with that permission can use the pad.

***

### 🌍 Language Support

OnyxPads includes complete message files for 10 languages:

| Language   | Code | File              |
| ---------- | ---- | ----------------- |
| English    | `en` | `messages_en.yml` |
| Spanish    | `es` | `messages_es.yml` |
| Portuguese | `pt` | `messages_pt.yml` |
| French     | `fr` | `messages_fr.yml` |
| German     | `de` | `messages_de.yml` |
| Italian    | `it` | `messages_it.yml` |
| Russian    | `ru` | `messages_ru.yml` |
| Chinese    | `zh` | `messages_zh.yml` |
| Japanese   | `ja` | `messages_ja.yml` |
| Korean     | `ko` | `messages_ko.yml` |

#### Changing Language

1. Open `config.yml`
2.  Change the `language` value:

    yaml

    ```
    language: es  # For Spanish
    ```
3. Run `/onyxpads reload`

#### Customizing Messages

All messages can be customized in the respective language file. Colors and formatting are supported:

* **Legacy colors:** `&a`, `&b`, `&c`, etc.
* **HEX colors:** `&#RRGGBB`
* **MiniMessage:** `<gradient:#FF6B6B:#DC143C>`, `<bold>`, etc.

***

### ❓ FAQ

#### Which blocks can be used as jump pads?

By default, pressure plates, slime blocks, and honey blocks are allowed. You can add more in `config.yml` under `allowed-blocks`.

#### How do I remove a jump pad?

Look at the pad and run `/onyxpads delete`.

#### Can players break jump pads?

No, jump pads are protected by default. You can change this in `config.yml` under `protection.prevent-breaking`.

#### How do I create a horizontal jump?

Use an angle of 90 degrees: `/onyxpads create 5 90`

#### Can I set different permissions for different pads?

Yes! Use `/onyxpads set permission <permission>` to set a custom permission for any pad.

#### Does it work with Folia?

Yes, OnyxPads is fully compatible with Folia.

#### What server software is supported?

Paper, Purpur, and Folia. Spigot may work but is not officially supported.

***

### 🆘 Support

#### Resources

| Resource          | Link                                                                                                      |
| ----------------- | --------------------------------------------------------------------------------------------------------- |
| **Source Code**   | [GitHub](https://github.com/LichiDev/OnyxPads)                                                            |
| **Issue Tracker** | [GitHub Issues](https://github.com/LichiDev/OnyxPads/issues)                                              |
| **Documentation** | [GitBook](https://app.gitbook.com/o/SeJilJp5rghYwoY1bLHm/s/kSipoiURxmaBKJotnkHQ/onyx-or-plugins/onyxpads) |

#### Reporting Issues

When reporting a bug, please include:

* Server software and version
* Plugin version
* Steps to reproduce
* Any relevant console errors

***

### 📄 License

This plugin is distributed under the **MIT License**.

***

**Created by:** [@LichiDev](https://github.com/LichiDev)\
**Last Updated:** March 2026
