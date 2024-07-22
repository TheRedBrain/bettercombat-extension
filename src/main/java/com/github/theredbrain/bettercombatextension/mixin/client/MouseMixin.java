package com.github.theredbrain.bettercombatextension.mixin.client;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import com.github.theredbrain.bettercombatextension.compatability.ShoulderSurfingCompat;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Mouse.class)
public abstract class MouseMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	// TODO needs a rework
	@Inject(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V", ordinal = 0), cancellable = true)
	private void bettercombatextension$updateMouse(CallbackInfo ci) {
		if (BetterCombatExtension.serverConfig.disable_player_yaw_changes_during_attacks && ((MinecraftClient_BetterCombat) this.client).isWeaponSwingInProgress() && !ShoulderSurfingCompat.isShoulderSurfingCameraDecoupled()) {
			ci.cancel();
		}
	}
}