package com.newenchants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.LivingEntity;

/**
 * Keeps entities frozen using the SAME mechanism as powder snow: the entity's "ticks frozen" counter.
 * That means everything comes straight from vanilla:
 *  - the blue frost overlay on a frozen player's screen
 *  - the shivering animation on a fully frozen entity
 *  - the movement slowdown (the powder-snow speed modifier, -0.05 = 50% of a player's base speed)
 *  - vanilla freeze damage, and vanilla freeze immunity (strays, snow golems, leather armor, ...)
 */
public final class FreezeManager {
    private FreezeManager() {}

    /** entity -> ticks of freezing remaining */
    private static final Map<LivingEntity, Integer> FROZEN = new HashMap<>();

    /** Freeze an entity for the given number of ticks (re-hitting refreshes to whichever is longer). */
    public static void freeze(LivingEntity entity, int ticks) {
        if (ticks <= 0 || !entity.canFreeze()) {
            return; // respects vanilla powder-snow immunity
        }
        FROZEN.merge(entity, ticks, Math::max);
    }

    public static void register() {
        // START of the server tick, so the counter is already maxed when each entity's own tick runs.
        ServerTickEvents.START_SERVER_TICK.register(server -> tick());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> FROZEN.clear());
    }

    private static void tick() {
        if (FROZEN.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<LivingEntity, Integer>> it = FROZEN.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<LivingEntity, Integer> entry = it.next();
            LivingEntity entity = entry.getKey();
            int remaining = entry.getValue();

            if (entity.isRemoved() || !entity.isAlive()) {
                it.remove();
                continue;
            }
            if (remaining <= 0 || !entity.canFreeze()) {
                entity.setTicksFrozen(0); // thaw immediately when the 8 seconds are up
                it.remove();
                continue;
            }

            // Vanilla lowers the counter by 2 during the entity's tick; starting 2 above the threshold
            // means it is exactly "fully frozen" while vanilla evaluates slowdown/shaking/damage.
            entity.setTicksFrozen(entity.getTicksRequiredToFreeze() + 2);
            entry.setValue(remaining - 1);
        }
    }
}
