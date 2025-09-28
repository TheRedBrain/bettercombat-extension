package com.github.theredbrain.bettercombatextension.mixin.bettercombat.logic;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerAttackHelper.class)
public class PlayerAttackHelperMixin {

	@WrapOperation(
			method = "getStaticRange",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeBaseValue(Lnet/minecraft/registry/entry/RegistryEntry;)D")
	)
	private static double bettercombatextension$getStaticRange(PlayerEntity instance, RegistryEntry registryEntry, Operation<Double> original) {
		if (BetterCombatExtension.SERVER_CONFIG.enable_attack_range_attribute_integration.get()) {
			return BetterCombatExtension.getAttackRangeBaseValue(instance);
		} else {
			return original.call(instance, registryEntry);
		}
	}

	@WrapOperation(
			method = "getRangeForItem",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D")
	)
	private static double bettercombatextension$getRangeForItem(PlayerEntity instance, RegistryEntry registryEntry, Operation<Double> original) {
		if (BetterCombatExtension.SERVER_CONFIG.enable_attack_range_attribute_integration.get()) {
			return BetterCombatExtension.getAttackRange(instance);
		} else {
			return original.call(instance, registryEntry);
		}
	}

}
