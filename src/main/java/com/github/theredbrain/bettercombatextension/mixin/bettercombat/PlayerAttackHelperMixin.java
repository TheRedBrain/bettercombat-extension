package com.github.theredbrain.bettercombatextension.mixin.bettercombat;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(PlayerAttackHelper.class)
@SuppressWarnings("UnreachableCode")
public abstract class PlayerAttackHelperMixin {

	@Shadow
	private static void setAttributesForOffHandAttack(PlayerEntity player, boolean useOffHand) {
		throw new AssertionError();
	}

	@ModifyReturnValue(method = "isDualWielding(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("RETURN"))
	private static boolean bettercombatextension$isDualWielding(boolean original, PlayerEntity player) {
		return original && !(!player.getMainHandStack().isIn(BetterCombatExtension.EMPTY_HAND_WEAPONS) && player.getOffHandStack().isIn(BetterCombatExtension.EMPTY_HAND_WEAPONS));
	}

	/**
	 * @author TheRedBrain
	 * @reason account for alternative two-handing condition
	 */
	@Overwrite
	public static boolean isTwoHandedWielding(PlayerEntity player) {
		WeaponAttributes mainAttributes = WeaponRegistry.getAttributes(player.getMainHandStack());
		return (mainAttributes != null && mainAttributes.isTwoHanded()) || (player.getOffHandStack().isEmpty() && BetterCombatExtension.SERVER_CONFIG.empty_offhand_equals_two_handing_mainhand.get());
	}

	/**
	 * @author TheRedBrain
	 * @reason experimental fix for incompatibility between RPG Inventory and Better Combats offhand attacking
	 */
	@Overwrite
	public static void swapHandAttributes(PlayerEntity player, boolean useOffHand, Runnable runnable) {
		if (!useOffHand) {
			runnable.run();
		} else {
			synchronized (player) {
				PlayerInventory inventory = player.getInventory();
				ItemStack mainHandStack = player.getMainHandStack();
				ItemStack offHandStack = player.getOffHandStack();
				setAttributesForOffHandAttack(player, true);
				if (BetterCombatExtension.shouldAlternativeHandSwapAlgorithmBeEnabled()) {
					player.equipStack(EquipmentSlot.MAINHAND, offHandStack);
					player.equipStack(EquipmentSlot.OFFHAND, offHandStack);
				} else {
					inventory.main.set(inventory.selectedSlot, offHandStack);
					inventory.offHand.set(0, offHandStack);
				}
				runnable.run();
				if (BetterCombatExtension.shouldAlternativeHandSwapAlgorithmBeEnabled()) {
					player.equipStack(EquipmentSlot.MAINHAND, mainHandStack);
					player.equipStack(EquipmentSlot.OFFHAND, offHandStack);
				} else {
					inventory.main.set(inventory.selectedSlot, mainHandStack);
					inventory.offHand.set(0, offHandStack);
				}
				setAttributesForOffHandAttack(player, false);
			}
		}
	}
}
