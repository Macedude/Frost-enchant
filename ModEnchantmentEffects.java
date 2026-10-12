package com.newenchants;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;

/** Registers the custom effect types that the enchantment JSON files can reference. */
public final class ModEnchantmentEffects {
    private ModEnchantmentEffects() {}

    /** Used in JSON as "type": "new_enchants:freeze". */
    public static final MapCodec<FreezeEnchantmentEffect> FREEZE =
            register("freeze", FreezeEnchantmentEffect.CODEC);

    private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String name, MapCodec<T> codec) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, NewEnchantsMod.id(name), codec);
    }

    /** Calling this forces the static fields above to initialise. */
    public static void register() {}
}
