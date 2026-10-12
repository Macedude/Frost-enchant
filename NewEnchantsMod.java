package com.newenchants;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewEnchantsMod implements ModInitializer {
    public static final String MOD_ID = "new_enchants";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ResourceKey<Enchantment> FROST_ASPECT = enchantment("frost_aspect");
    public static final ResourceKey<Enchantment> HEROBRINE = enchantment("herobrine");

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private static ResourceKey<Enchantment> enchantment(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, id(path));
    }

    @Override
    public void onInitialize() {
        ModEnchantmentEffects.register();
        FreezeManager.register();
        HerobrineManager.register();
        SnowVillageLoot.register();
        LOGGER.info("New Enchantments loaded: Frost Aspect, Herobrine");
    }
}
