package com.newenchants;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.ItemStack;

/**
 * While a player HOLDS (main hand or off hand) an item enchanted with Herobrine they get
 * creative-style flight and Speed II. Both are removed as soon as they stop holding it.
 */
public final class HerobrineManager {
    private HerobrineManager() {}

    /** Speed II = amplifier 1. */
    private static final int SPEED_AMPLIFIER = 1;
    /** The effect is kept very short and refreshed every few ticks, so it vanishes right after you let go. */
    private static final int EFFECT_DURATION_TICKS = 8;
    private static final int REFRESH_BELOW_TICKS = 4;

    /** Marks players whose flight WE granted, so we never take away flight from creative mode or other sources. Saved with the player. */
    private static final String FLIGHT_TAG = "new_enchants_herobrine_flight";

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(HerobrineManager::tick);
        // Clean up if the server crashed while someone was flying (the flight flag is saved with the player).
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (!isHoldingHerobrine(player)) {
                releaseFlight(player);
            }
        });
        // Don't save a flying survival player to disk.
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> releaseFlight(handler.getPlayer()));
    }

    private static void tick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            update(player);
        }
    }

    private static void update(ServerPlayer player) {
        if (isHoldingHerobrine(player)) {
            boolean exempt = player.isCreative() || player.isSpectator(); // they can already fly
            Abilities abilities = player.getAbilities();
            if (!exempt && !abilities.mayfly) {
                abilities.mayfly = true;
                player.addTag(FLIGHT_TAG);
                player.onUpdateAbilities();
            }
            applySpeed(player);
        } else {
            releaseFlight(player);
        }
    }

    private static void applySpeed(ServerPlayer player) {
        MobEffectInstance current = player.getEffect(MobEffects.SPEED);
        boolean infinite = current != null && current.getDuration() == -1; // e.g. from a command
        boolean strongEnough = current != null && current.getAmplifier() >= SPEED_AMPLIFIER;
        boolean aboutToExpire = current != null && !infinite && current.getDuration() < REFRESH_BELOW_TICKS;

        if (current == null || !strongEnough || aboutToExpire) {
            // ambient = true (soft icon), showParticles = false, showIcon = true
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, EFFECT_DURATION_TICKS, SPEED_AMPLIFIER, true, false, true));
        }
    }

    private static void releaseFlight(ServerPlayer player) {
        if (!player.getTags().contains(FLIGHT_TAG)) {
            return;
        }
        player.removeTag(FLIGHT_TAG);
        if (player.isCreative() || player.isSpectator()) {
            return; // never take flight away from creative/spectator
        }
        Abilities abilities = player.getAbilities();
        if (abilities.mayfly) {
            abilities.mayfly = false;
            abilities.flying = false;
            player.onUpdateAbilities();
        }
    }

    private static boolean isHoldingHerobrine(ServerPlayer player) {
        return hasHerobrine(player.getMainHandItem()) || hasHerobrine(player.getOffhandItem());
    }

    private static boolean hasHerobrine(ItemStack stack) {
        return EnchantUtil.appliedLevel(stack, NewEnchantsMod.HEROBRINE) > 0;
    }
}
