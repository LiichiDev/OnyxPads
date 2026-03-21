---
description: >-
  Welcome to the ultimate guide for installing, customizing, and optimizing your
  fullpvp experience. Here, you'll find everything you need to set up your world
  perfectly.
---

# FullPvP - (1.8x - 1.21x)

<figure><img src="../.gitbook/assets/fullpvp setup.jpeg" alt=""><figcaption></figcaption></figure>

***

## Setup video:  [Clik Here](https://www.youtube.com/watch?v=lwJO2SLFypY)

## Buy setup: [Clik Here](https://builtbybit.com/resources/premium-fullpvp-setup-onyx-studios.60040/)

***

### How To Install The Setup <a href="#how-to-install-the-setup" id="how-to-install-the-setup"></a>

* Upload all the files from the folder named "FullPvP (Setup)" to your own server host.

***

### Get the Right Server JAR

For the best performance, we recommend using **Paper** or **Purpur** with our setups. If you're unsure which one to pick, **Paper** is the safest choice.

{% hint style="warning" %}
**Download Paper version (1.8.8):**  [PaperMC](https://api.papermc.io/v2/projects/paper/versions/1.8.8/builds/445/downloads/paper-1.8.8-445.jar)
{% endhint %}

If you're using a public Minecraft host, you can install **Paper** automatically through your hosting panel. Be sure to select the version mentioned at the top of this page.

***

### Adding Necessary Plugins

Because of BuiltByBit’s distribution restrictions, certain plugins aren’t included in the setup. You’ll need to download and install them yourself.

{% hint style="success" %}
Download all necessary plugins and install them in your **plugins** folder
{% endhint %}

> [FullPvP Core](https://builtbybit.com/resources/neron-fullpvp-core.25305/) _(Paid but important)_
>
> [UltimateKoths](https://polymart.org/resource/ultimate-koth-v2.3066) _(Paid but optional)_\
> \
> \
> [AlonsoLeaderboards](https://www.spigotmc.org/resources/%E2%9C%85-alonsoleaderboards-1-8-1-16-%E2%80%A2-signs-heads-armorstands-npc-placeholders-developerapi.84267/) _(Free)_\
> [Tab](https://www.spigotmc.org/resources/tab-1-5-1-21-4.57806/) _(Free)_

***

### Installation completed <a href="#id-3.-installation-completed" id="id-3.-installation-completed"></a>

You have new fully installed the setup, and everything should be ready to go. Now you will be able to start the server and join through your host IP.

### Start your server <a href="#id-4.-start-your-server" id="id-4.-start-your-server"></a>

#### You can now start your server, and everything should be working.

If you are the server owner, you should OP yourself by using this command in console:

```
op %player%
```

```
lp user %player% parent add owner
```

***

### Usage Guide <a href="#usage-guide" id="usage-guide"></a>

This is a guide on how to use the setup, and describes commands you can use to run the server.

{% tabs %}
{% tab title="Money" %}
The setup's economy system relies on the Essentials plugin. Use these commands to add or remove money from players:

**`/eco give [player] [amount] --- /eco take [player] [amount]`**
{% endtab %}

{% tab title="Crate Key" %}
The plugin used for the Crates is SpecializedCrates. Below are some key commands:

**`/crates`**` ``givekey [cratename] [player] [amount]`

Crates: Vote, Money, Keyall, Soul, Koth, Celestial, Mystery, Magic, Sangrienta
{% endtab %}

{% tab title="Rank" %}
You can add ranks to any user with `/lp user [player] parent add [rank]`

You can remove ranks to any user with `/lp user [player] parent remove [rank]`
{% endtab %}

{% tab title="RPGItems" %}
Special items system.\
\
Give an item: `/rpgitem give [item] [player] [amount]`
{% endtab %}
{% endtabs %}
