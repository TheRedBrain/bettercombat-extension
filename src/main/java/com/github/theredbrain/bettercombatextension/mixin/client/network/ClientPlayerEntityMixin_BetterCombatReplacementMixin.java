package com.github.theredbrain.bettercombatextension.mixin.client.network;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import com.github.theredbrain.bettercombatextension.client.DuckMinecraftClientMixin;
import com.mojang.authlib.GameProfile;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.config.ServerConfig;
import net.bettercombat.utils.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
@SuppressWarnings("UnreachableCode")
public abstract class ClientPlayerEntityMixin_BetterCombatReplacementMixin extends AbstractClientPlayerEntity {

	@Shadow
	@Final
	protected MinecraftClient client;

	@Shadow
	public abstract boolean isUsingItem();

	@Shadow
	public abstract float getPitch(float tickDelta);

	@Shadow
	public abstract void sendMessage(Text message, boolean overlay);

	public ClientPlayerEntityMixin_BetterCombatReplacementMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(
			method = "tickMovement",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/input/Input;tick(ZF)V",
					shift = At.Shift.AFTER
			)
	)
	public void bettercombatextension$tickMovement(CallbackInfo ci) {
		boolean isWeaponSwingInProgress = ((MinecraftClient_BetterCombat) this.client).isWeaponSwingInProgress();
		ServerConfig betterCombatServerConfig = BetterCombatMod.config;
		com.github.theredbrain.bettercombatextension.config.ServerConfig betterCombatExtensionServerConfig = BetterCombatExtension.SERVER_CONFIG;

		MinecraftClient_BetterCombat client = (MinecraftClient_BetterCombat) MinecraftClient.getInstance();

		float multiplier = Math.min(
				Math.max(
						betterCombatServerConfig.movement_speed_while_attacking,
						betterCombatExtensionServerConfig.minimum_global_attack_movement_speed_multiplier.get()
				),
				betterCombatExtensionServerConfig.maximum_global_attack_movement_speed_multiplier.get()
		);
		WeaponAttributes.Attack attack = client.getCurrentAttack();
		ItemStack activeItemStack = this.getStackInHand(((DuckMinecraftClientMixin) this.client).bettercombatextension$getCurrentAttackHand());
		boolean isMovementPenaltyIgnored = activeItemStack.isIn(BetterCombatExtension.IGNORES_ATTACK_MOVEMENT_PENALTY) && isWeaponSwingInProgress;
		ClientPlayerEntity clientPlayer = (ClientPlayerEntity) (Object) this;

		if (attack != null) {
			multiplier *= attack.movementSpeedMultiplier();
		}

		if ((multiplier != 1.0) && !isMovementPenaltyIgnored) {
			if (!clientPlayer.hasVehicle() || betterCombatServerConfig.movement_speed_effected_while_mounting) {
				float swingProgress = client.getSwingProgress();

				if ((double) swingProgress < /*1*/0.98) {
					if (betterCombatServerConfig.movement_speed_applied_smoothly) {
						double p2 = 0.0;
						if ((double) swingProgress <= 0.5) {
							p2 = MathHelper.easeOutCubic((double) (swingProgress * 2.0F));
						} else {
							p2 = MathHelper.easeOutCubic(1.0 - ((double) swingProgress - 0.5) * 2.0);
						}

						if (multiplier != 1.0) {
							multiplier = ((float) (1.0 - (1.0 - multiplier) * p2));
						}
					}

					clientPlayer.input.movementForward *= multiplier;
					clientPlayer.input.movementSideways *= multiplier;
				}
			}
		}
		boolean isMovementLockingDisabled = activeItemStack.isIn(BetterCombatExtension.DISABLES_MOVEMENT_LOCKING_DURING_ATTACK);
		if (betterCombatExtensionServerConfig.enable_movement_locking_attacks.get() && !isMovementLockingDisabled && isWeaponSwingInProgress) {
			boolean isVehicleDisablingMovementLocking = clientPlayer.getVehicle() != null && clientPlayer.getVehicle().getType().isIn(BetterCombatExtension.DISABLES_MOVEMENT_LOCKING_WHEN_RIDDEN);
			if (!clientPlayer.hasVehicle() || !isVehicleDisablingMovementLocking) {
				clientPlayer.input.movementForward = 0.0F;
				clientPlayer.input.movementSideways = 0.0F;
			}
		}
		boolean isJumpRestrictionDisabled = activeItemStack.isIn(BetterCombatExtension.DISABLES_JUMP_RESTRICTION_DURING_ATTACK);
		if (betterCombatExtensionServerConfig.enable_jump_restriction_during_attacks.get() && !isJumpRestrictionDisabled && isWeaponSwingInProgress) {
			boolean isVehicleDisablingJumpRestriction = clientPlayer.getVehicle() != null && clientPlayer.getVehicle().getType().isIn(BetterCombatExtension.DISABLES_JUMP_RESTRICTION_WHEN_RIDDEN);
			if (!clientPlayer.hasVehicle() || !isVehicleDisablingJumpRestriction) {
				clientPlayer.input.jumping = false;
			}
		}
	}
}
