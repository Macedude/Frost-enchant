package com.newenchants;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class EnchantUtil {
    private EnchantUtil() {}

    /** Level of an enchantment that is actually APPLIED to the item (an enchanted book's stored enchants don't count). */
    public static int appliedLevel(ItemStack stack, ResourceKey<Enchantment> key) {
        if (stack.isEmpty()) {
            return 0;
        }
        return levelIn(stack.get(DataComponents.ENCHANTMENTS), key);
    }

    /** Highest level of an enchantment on the item, counting both applied AND stored (book) enchantments. */
    public static int anyLevel(ItemStack stack, ResourceKey<Enchantment> key) {
        if (stack.isEmpty()) {
            return 0;
        }
        return Math.max(
                levelIn(stack.get(DataComponents.ENCHANTMENTS), key),
                levelIn(stack.get(DataComponents.STORED_ENCHANTMENTS), key));
    }

    private static int levelIn(ItemEnchantments enchantments, ResourceKey<Enchantment> key) {
        if (enchantments == null) {
            return 0;
        }
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }
}
