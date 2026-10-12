package com.newenchants;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * Freezes the entity it is applied to. Configured from the enchantment JSON:
 * "duration" is in SECONDS and can scale with the enchantment level.
 */
public record FreezeEnchantmentEffect(LevelBasedValue seconds) implements EnchantmentEntityEffect {

    public static final MapCodec<FreezeEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    LevelBasedValue.CODEC.fieldOf("duration").forGetter(FreezeEnchantmentEffect::seconds)
            ).apply(instance, FreezeEnchantmentEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity target, Vec3 position) {
        if (target instanceof LivingEntity living) {
            int ticks = Math.round(this.seconds.calculate(enchantmentLevel) * 20.0F);
            FreezeManager.freeze(living, ticks);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
