package com.newenchants.mixin;

import com.newenchants.EnchantUtil;
import com.newenchants.NewEnchantsMod;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Frost Aspect III is creative-only. Vanilla anvils would otherwise let players merge two level II books
 * (or a II sword + II book) into III, so we block any anvil result that is level III unless one of the
 * inputs ALREADY had level III (which can only come from creative mode / commands).
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Inject(method = "createResult", at = @At("RETURN"))
    private void newEnchants$blockFrostAspectThree(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;

        Slot resultSlot = menu.getSlot(AnvilMenu.RESULT_SLOT);
        ItemStack result = resultSlot.getItem();
        if (result.isEmpty() || newEnchants$frostLevel(result) < 3) {
            return;
        }

        boolean inputAlreadyHadIt =
                newEnchants$frostLevel(menu.getSlot(AnvilMenu.INPUT_SLOT).getItem()) >= 3
                || newEnchants$frostLevel(menu.getSlot(AnvilMenu.ADDITIONAL_SLOT).getItem()) >= 3;

        if (!inputAlreadyHadIt) {
            resultSlot.set(ItemStack.EMPTY);
        }
    }

    private static int newEnchants$frostLevel(ItemStack stack) {
        return EnchantUtil.anyLevel(stack, NewEnchantsMod.FROST_ASPECT);
    }
}
