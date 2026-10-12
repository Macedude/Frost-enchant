# New Enchantments (Fabric, Minecraft 1.21.8)

Two enchantments: **Frost Aspect** and **Herobrine**. Needs Fabric Loader 0.17.2+, Fabric API, Java 21.
The mod must be installed on **both the server and every client** (the enchantments use a custom effect type).

## Building
I couldn't produce the binary Gradle wrapper files, so do ONE of these first:
- Generate any 1.21.8 template at https://fabricmc.net/develop/template/ and copy its `gradlew`, `gradlew.bat`
  and `gradle/wrapper/gradle-wrapper.jar` into this folder, **or**
- If you have Gradle installed: `gradle wrapper --gradle-version 8.14.3`

Then run `./gradlew build` (Windows: `gradlew build`). The mod is `build/libs/new_enchants-1.0.0.jar`
(not the `-sources` jar). Drop it in `mods/` together with Fabric API `0.136.1+1.21.8`.

## Frost Aspect (I, II; III creative-only)
- Swords and axes only. Hit something (direct melee hit) and it is frozen for **8 seconds**.
- Uses the powder-snow freezing system, so you get the real thing: blue frost screen overlay, the shivering
  animation, the powder-snow slowdown (a flat -0.05 movement speed = 50% for a player), and vanilla freeze damage.
  Vanilla freeze immunity applies too (strays, snow golems, polar bears, anyone in leather armor).
- Survival: **only** from chests in snowy villages (snowy houses + shepherd houses, ~17% per chest, a quarter of
  those are level II). It is in no enchanting-table, trade, fishing or random-loot tag.
- Level III: only via the creative inventory or commands. The anvil is blocked from *creating* level III
  (II + II, or II sword + II book), but a III you already have can still be applied.
- Tuning: duration per level is in `data/new_enchants/enchantment/frost_aspect.json` ("base" / "per_level_above_first", in
  seconds). Chest odds are the three weights at the top of `SnowVillageLoot.java`.

## Herobrine (max level I)
- While **holding** the item (either hand): creative-style flight + Speed II. Let go and both end within ~0.4 s.
- Not obtainable in survival (it's in no loot/trade/table tag) - creative inventory or commands only.
- Applies to swords, axes, pickaxes, shovels, hoes, mace, trident, bow, crossbow. To widen it, edit
  `data/new_enchants/tags/item/enchantable/herobrine.json`.
- Flight is never granted over or taken from creative/spectator players, or flight from other sources.
- Heads-up: letting go while flying drops you, so you take normal fall damage.

## Commands
    /give @s enchanted_book[stored_enchantments={"new_enchants:frost_aspect":3}]
    /give @s diamond_sword[enchantments={"new_enchants:frost_aspect":2}]
    /give @s diamond_sword[enchantments={"new_enchants:herobrine":1}]
    /give @s enchanted_book[stored_enchantments={"new_enchants:herobrine":1}]
