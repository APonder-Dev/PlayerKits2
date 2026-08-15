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
  cloning, disconnecting mid-edit) is blocked. A player's real held item is also handed
  back to their own inventory before the editor opens, so it can never end up trapped
  inside the virtual GUI.
- Clicking **Save** flashes the button (glowing, renamed, with a sound) to confirm the
  arrangement currently on screen is what's now persisted - it isn't just a chat message.

Configurable via the `kit_layout` section in `config.yml` (button appearance, GUI
title, reset/back/save buttons, the save-confirmation flash text) and the `kitLayout*`
messages in `messages.yml`.

## Building

```
mvn clean package
```

The compiled jar will be in `target/`.

## Releases

Version numbers follow `major.feature.fix`:

- `x.#.#` - full release. Not automated; only bumped intentionally.
- `#.x.#` - a feature was added.
- `#.#.x` - a bug fix.

Every push to `main` that changes `pom.xml` or `src/**` runs the `Release` GitHub
Actions workflow. If the `<version>` in `pom.xml` doesn't already have a matching
GitHub Release, it builds the plugin and publishes a new release for that version with
the jar attached - so cutting a release is just a matter of bumping the version in
`pom.xml`. A separate `CI` workflow builds every push/PR to catch build breakage early.

## License

See [LICENSE](LICENSE).
