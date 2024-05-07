package com.github.theredbrain.bettercombatextension.mixin.bettercombat;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerAttackHelper.class)
public class PlayerAttackHelperMixin {

    @Inject(method = "isDualWielding", at = @At("RETURN"), cancellable = true)
    private static void bam$isDualWielding(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() && !(!player.getMainHandStack().isIn(BetterCombatExtension.EMPTY_HAND_WEAPONS) && player.getOffHandStack().isIn(BetterCombatExtension.EMPTY_HAND_WEAPONS)));
        cir.cancel();
    }

    /**
     * @author TheRedBrain
     * @reason account for alternative two-handing condition
     */
    @Overwrite
    public static boolean isTwoHandedWielding(PlayerEntity player) {
        WeaponAttributes mainAttributes = WeaponRegistry.getAttributes(player.getMainHandStack());
        return (mainAttributes != null && mainAttributes.isTwoHanded()) || (player.getOffHandStack().isEmpty() && BetterCombatExtension.serverConfig.empty_offhand_equals_two_handing_mainhand);
    }
}
