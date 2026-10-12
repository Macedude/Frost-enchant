package com.newenchants;

import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * The ONLY way to get Frost Aspect in survival: chests in snowy villages.
 * It is added to chest tables that only generate in snowy villages, so it can't show up anywhere else,
 * and it is not in the enchanting-table / trade / random-loot tags.
 */
public final class SnowVillageLoot {
    private SnowVillageLoot() {}

    // ---- tuning: relative weights inside the extra loot pool (one roll per chest) ----
    private static final int WEIGHT_NOTHING = 20; // bigger = rarer
    private static final int WEIGHT_LEVEL_1 = 3;
    private static final int WEIGHT_LEVEL_2 = 1;
    // With these numbers each snowy-village chest has a ~17% chance to hold a Frost Aspect book (a quarter of those are level II).

    private static final List<ResourceKey<LootTable>> SNOWY_VILLAGE_CHESTS = List.of(
            table("chests/village/village_snowy_house"),
            table("chests/village/village_shepherd") // shepherd-house chests only exist in snowy villages
    );

    private static ResourceKey<LootTable> table(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("minecraft", path));
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin() || !SNOWY_VILLAGE_CHESTS.contains(key)) {
                return;
            }

            Optional<Holder.Reference<Enchantment>> frostAspect =
                    registries.lookupOrThrow(Registries.ENCHANTMENT).get(NewEnchantsMod.FROST_ASPECT);
            if (frostAspect.isEmpty()) {
                NewEnchantsMod.LOGGER.warn("Frost Aspect enchantment not found; skipping snowy village loot for {}", key.location());
                return;
            }
            Holder<Enchantment> enchantment = frostAspect.get();

            LootPool.Builder pool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(EmptyLootItem.emptyItem().setWeight(WEIGHT_NOTHING))
                    .add(book(enchantment, 1, WEIGHT_LEVEL_1))
                    .add(book(enchantment, 2, WEIGHT_LEVEL_2));
            tableBuilder.withPool(pool);
        });
    }

    private static LootPoolEntryContainer.Builder<?> book(Holder<Enchantment> enchantment, int level, int weight) {
        return LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                .setWeight(weight)
                .apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchantment, ConstantValue.exactly((float) level)));
    }
}
