package com.github.theredbrain.bettercombatextension.mixin.client.network;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import com.github.theredbrain.bettercombatextension.client.DuckMinecraftClientMixin;
import com.github.theredbrain.bettercombatextension.compatability.ShoulderSurfingCompat;
import com.mojang.authlib.GameProfile;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.config.ServerConfig;
import net.bettercombat.utils.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
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
		ServerConfig config = BetterCombat.config;
		double multiplier = Math.min(Math.max((double) config.movement_speed_while_attacking, 0.0), 1.0);
		boolean isMovementPenaltyIgnored = this.getStackInHand(((DuckMinecraftClientMixin) this.client).bettercombatextension$getCurrentAttackHand()).isIn(BetterCombatExtension.IGNORES_ATTACK_MOVEMENT_PENALTY) && isWeaponSwingInProgress;
		if (multiplier != 1.0 && !isMovementPenaltyIgnored) {
			ClientPlayerEntity clientPlayer = (ClientPlayerEntity) (Object) this;
			if (!clientPlayer.hasVehicle() || config.movement_speed_effected_while_mounting) {
				MinecraftClient_BetterCombat client = (MinecraftClient_BetterCombat) MinecraftClient.getInstance();
				float swingProgress = client.getSwingProgress();
				if ((double) swingProgress < 1/*0.98*/) {
					if (config.movement_speed_applied_smoothly) {
						double p2 = 0.0;
						if ((double) swingProgress <= 0.5) {
							p2 = MathHelper.easeOutCubic((double) (swingProgress * 2.0F));
						} else {
							p2 = MathHelper.easeOutCubic(1.0 - ((double) swingProgress - 0.5) * 2.0);
						}

						multiplier = (double) ((float) (1.0 - (1.0 - multiplier) * p2));
					}

					Input var10000 = clientPlayer.input;
					var10000.movementForward = (float) ((double) var10000.movementForward * multiplier);
					var10000 = clientPlayer.input;
					var10000.movementSideways = (float) ((double) var10000.movementSideways * multiplier);

					if (ShoulderSurfingCompat.isShoulderSurfingCameraDecoupled() && BetterCombatExtension.serverConfig.disable_player_yaw_changes_during_attacks) {
						var10000 = clientPlayer.input;
						var10000.movementSideways = 0.0F;
					}
				}

			}
		}
	}
}
