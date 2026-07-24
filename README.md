# PlayerKits2

A fully configurable Minecraft kits plugin for Spigot/Paper (1.8 - 1.21+), based on
[PlayerKits2 by Ajneb97](https://www.spigotmc.org/resources/playerkits-2-fully-configurable-kits-1-8-1-20.112616/),
maintained here by APonder.

## Features

- Fully configurable kits: items, cooldowns, permissions, one-time claims, purchase requirements (Vault), claim/error actions, PlaceholderAPI support.
- Configurable GUIs for browsing, previewing, and buying kits.
- MySQL or flat-file (YAML) player data storage.
- In-game kit creation and an admin GUI for editing kits without touching config files.

### Per-player kit layout editor

From a kit's preview screen, players can click **Arrange Your Kit** to open a virtual
inventory editor and drag the kit's items into their own preferred hotbar/inventory
slots. The arrangement is:

- Saved per-player, per-kit.
- Auto-saved when the menu is closed or the player goes back to the previous menu.
- Applied automatically the next time the kit is claimed — items go into their saved
  slots, with armor auto-equipped and off-hand items placed correctly. Items without a
  saved position (new items, deleted/changed kit contents, occupied/full inventory)
  fall back to the plugin's normal item-giving behavior.
- Fully virtual: nothing in the editor is a real item, and every duplication/extraction
  vector (shift-click, number-key swap, dragging, double-click, dropping, creative
  cloning, disconnecting mid-edit) is blocked.

Configurable via the `kit_layout` section in `config.yml` (button appearance, GUI
title, reset/back buttons) and the `kitLayout*` messages in `messages.yml`.

## Building

```
mvn clean package
```

The compiled jar will be in `target/`.

## License

See [LICENSE](LICENSE).
