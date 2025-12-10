package com.github.theredbrain.bettercombatextension.mixin.client.network;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import com.github.theredbrain.bettercombatextension.BetterCombatExtensionClient;
import com.github.theredbrain.bettercombatextension.bettercombat.DuckWeaponAttributesMixin;
import com.github.theredbrain.bettercombatextension.config.ServerConfig;
import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.animation.AttackAnimationSubStack;
import net.bettercombat.client.animation.CustomAnimationPlayer;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.client.animation.PoseSubStack;
import net.bettercombat.client.animation.StateCollectionHelper;
import net.bettercombat.client.animation.modifier.HarshAdjustmentModifier;
import net.bettercombat.client.animation.modifier.TransmissionSpeedModifier;
import net.bettercombat.client.compat.FirstPersonAnimationCompatibility;
import net.bettercombat.client.particle.SlashParticleUtil;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.mixin.player.LivingEntityAccessor;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin({AbstractClientPlayerEntity.class})
public abstract class AbstractClientPlayerEntity_BetterCombatReplacementMixin extends PlayerEntity implements PlayerAttackAnimatable {
	@Unique
	private final AttackAnimationSubStack attackAnimation = new AttackAnimationSubStack(this.createAttackAdjustment());
	@Unique
	private final PoseSubStack mainHandBodyPose = new PoseSubStack(this.createPoseAdjustment(), true, true);
	@Unique
	private final PoseSubStack mainHandItemPose = new PoseSubStack((AbstractModifier) null, false, true);
	@Unique
	private final PoseSubStack offHandBodyPose = new PoseSubStack((AbstractModifier) null, true, false);
	@Unique
	private final PoseSubStack offHandItemPose = new PoseSubStack((AbstractModifier) null, false, true);

	public AbstractClientPlayerEntity_BetterCombatReplacementMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(
			method = {"<init>"},
			at = {@At("TAIL")}
	)
	private void postInit(ClientWorld world, GameProfile profile, CallbackInfo ci) {
		AnimationStack stack = ((IAnimatedPlayer) this).getAnimationStack();
		stack.addAnimLayer(1, this.offHandItemPose.base);
		stack.addAnimLayer(2, this.offHandBodyPose.base);
		stack.addAnimLayer(3, this.mainHandItemPose.base);
		stack.addAnimLayer(4, this.mainHandBodyPose.base);
		stack.addAnimLayer(2000, this.attackAnimation.base);
		this.mainHandBodyPose.configure = this::updateAnimationByCurrentActivity;
		this.offHandBodyPose.configure = this::updateAnimationByCurrentActivity;
	}

	public void updateAnimationsOnTick() {
		PlayerEntity player = (PlayerEntity) this;
		boolean isLeftHanded = this.isLeftHanded();
		boolean hasActiveAttackAnimation = this.attackAnimation.base.getAnimation() != null && this.attackAnimation.base.getAnimation().isActive();
		ItemStack mainHandStack = player.getMainHandStack();
		ItemStack offHandStack = player.getOffHandStack();

		if (scheduledParticles != null && scheduledParticles.time() == player.age) {
			SlashParticleUtil.spawnParticles(scheduledParticles.args());
			scheduledParticles = null;
		}

		if (!player.handSwinging && !player.isSwimming() && !player.isUsingItem() && (BetterCombatExtensionClient.CLIENT_CONFIG.enable_poses_while_sprinting.get() || !player.isSprinting()) && (BetterCombatExtensionClient.CLIENT_CONFIG.enable_poses_while_mounted.get() || player.getVehicle() == null) && !player.isClimbing() && !player.isFallFlying() && !Platform.isCastingSpell(player) && !CrossbowItem.isCharged(mainHandStack)) {
			if (hasActiveAttackAnimation) {
				((LivingEntityAccessor) player).invokeTurnHead(player.getHeadYaw(), 0.0F);
			}

			KeyframeAnimation newMainHandPose = null;
			KeyframeAnimation newOffHandPose = null;
			WeaponAttributes mainHandAttributes = WeaponRegistry.getAttributes(mainHandStack);
			WeaponAttributes offHandAttributes = WeaponRegistry.getAttributes(player.getOffHandStack());

			boolean isWeaponTwoHanded = mainHandAttributes != null && mainHandAttributes.isTwoHanded();
			boolean isAlternativeTwoHandedWieldingActive = mainHandAttributes != null && !mainHandAttributes.isTwoHanded() && offHandStack.isEmpty() && BetterCombatExtension.SERVER_CONFIG.empty_offhand_equals_two_handing_mainhand.get();

			if (isWeaponTwoHanded) {
				if (mainHandAttributes.pose() != null) {
					newMainHandPose = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Identifier.of(mainHandAttributes.pose()));
				}
			} else if (isAlternativeTwoHandedWieldingActive) {
				String two_handed_pose = ((DuckWeaponAttributesMixin) (Object) mainHandAttributes).bettercombatextension$getTwoHandedPose();
				if (two_handed_pose != null) {
					newMainHandPose = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Identifier.of(two_handed_pose));
				}
			} else {
				if (mainHandAttributes != null && mainHandAttributes.pose() != null) {
					newMainHandPose = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Identifier.of(mainHandAttributes.pose()));
				}
				if (offHandAttributes != null && offHandAttributes.offHandPose() != null) {
					newOffHandPose = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Identifier.of(offHandAttributes.offHandPose()));
				}
			}

			this.mainHandItemPose.setPose(newMainHandPose, isLeftHanded);
			this.offHandItemPose.setPose(newOffHandPose, isLeftHanded);
			this.mainHandBodyPose.setPose(newMainHandPose, isLeftHanded);
			this.offHandBodyPose.setPose(newOffHandPose, isLeftHanded);
		} else {
			this.mainHandBodyPose.setPose((KeyframeAnimation) null, isLeftHanded);
			this.mainHandItemPose.setPose((KeyframeAnimation) null, isLeftHanded);
			this.offHandBodyPose.setPose((KeyframeAnimation) null, isLeftHanded);
			this.offHandItemPose.setPose((KeyframeAnimation) null, isLeftHanded);
		}
	}

	public void playAttackAnimation(String name, AnimatedHand animatedHand, float length, float upswing) {
		try {
			KeyframeAnimation animation = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Identifier.of(name));
			KeyframeAnimation.AnimationBuilder copy = animation.mutableCopy();
			this.updateAnimationByCurrentActivity(copy);
			copy.torso.fullyEnablePart(true);
			copy.head.pitch.setEnabled(false);
			float speed = (float) animation.endTick / length;
			boolean mirror = animatedHand.isOffHand();
			if (this.isLeftHanded()) {
				mirror = !mirror;
			}

			int fadeIn = copy.beginTick;
			float upswingSpeed = speed / BetterCombatMod.config.getUpswingMultiplier();
			float downwindSpeed = (float) ((double) speed * MathHelper.lerp(Math.max((double) BetterCombatMod.config.getUpswingMultiplier() - 0.5, 0.0) / 0.5, (double) (1.0F - upswing), (double) (upswing / (1.0F - upswing))));
			this.attackAnimation.speed.set(upswingSpeed, List.of(new TransmissionSpeedModifier.Gear(length * upswing, downwindSpeed), new TransmissionSpeedModifier.Gear(length, speed)));
			this.attackAnimation.mirror.setEnabled(mirror);
			CustomAnimationPlayer player = new CustomAnimationPlayer(copy.build(), 0);
			player.setFirstPersonMode(FirstPersonAnimationCompatibility.firstPersonMode());
			player.setFirstPersonConfiguration(this.firstPersonConfig(animatedHand));
			this.attackAnimation.base.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fadeIn, Ease.INOUTSINE), player);
		} catch (Exception var13) {
			var13.printStackTrace();
		}
	}

	@Nullable
	private SlashParticleUtil.ScheduledSpawnArgs scheduledParticles = null;

	@Override
	public void playAttackParticles(boolean isOffHand, float weaponRange, int delay, List<ParticlePlacement> particles, TrailAppearance appearance) {
		var player = (AbstractClientPlayerEntity)(Object)this;
		var spawn = new SlashParticleUtil.SpawnArgs(
				player,
				isOffHand,
				weaponRange,
				particles,
				appearance
		);
		scheduledParticles = new SlashParticleUtil.ScheduledSpawnArgs(
				spawn,
				player.age + delay
		);
	}

	@Unique
	private AdjustmentModifier createAttackAdjustment() {
		return new AdjustmentModifier((partName) -> {
			float rotationX = 0.0F;
			float rotationY = 0.0F;
			float rotationZ = 0.0F;
			float offsetX = 0.0F;
			float offsetY = 0.0F;
			float offsetZ = 0.0F;
			float pitch;
			ServerConfig serverConfig = BetterCombatExtension.SERVER_CONFIG;
			pitch = serverConfig.restrict_attack_pitch.get() ? MathHelper.clamp(this.getPitch(), -serverConfig.attack_pitch_range.get(), serverConfig.attack_pitch_range.get()) : this.getPitch();
			pitch = (float) Math.toRadians((double) pitch);
			if (FirstPersonMode.isFirstPersonPass()) {
				switch (partName) {
					case "body":
						rotationX -= pitch;
						if (pitch < 0.0F) {
							double offset = Math.abs(Math.sin((double) pitch));
							offsetY = (float) ((double) offsetY + offset * 0.5);
							offsetZ = (float) ((double) offsetZ - offset);
						}
						break;
					default:
						return Optional.empty();
				}
			} else {
				switch (partName) {
					case "body":
						rotationX -= pitch * 0.75F;
						break;
					case "rightArm":
					case "leftArm":
						rotationX += pitch * 0.25F;
						break;
					case "rightLeg":
					case "leftLeg":
						rotationX = (float) ((double) rotationX - (double) pitch * 0.75);
						break;
					default:
						return Optional.empty();
				}
			}

			return Optional.of(new AdjustmentModifier.PartModifier(new Vec3f(rotationX, rotationY, rotationZ), new Vec3f(offsetX, offsetY, offsetZ)));
		});
	}

	@Unique
	private AdjustmentModifier createPoseAdjustment() {
		PlayerEntity player = this;
		return new HarshAdjustmentModifier((partName) -> {
			float rotationX = 0.0F;
			float rotationY = 0.0F;
			float rotationZ = 0.0F;
			float offsetX = 0.0F;
			float offsetY = 0.0F;
			float offsetZ = 0.0F;
			if (!FirstPersonMode.isFirstPersonPass()) {
				switch (partName) {
					case "rightArm":
					case "leftArm":
						if (!this.mainHandItemPose.lastAnimationUsesBodyChannel && player.isInSneakingPose()) {
							offsetY += 3.0F;
						}
						break;
					default:
						return Optional.empty();
				}
			}

			return Optional.of(new AdjustmentModifier.PartModifier(new Vec3f(rotationX, rotationY, rotationZ), new Vec3f(offsetX, offsetY, offsetZ)));
		});
	}

	@Unique
	private void updateAnimationByCurrentActivity(KeyframeAnimation.AnimationBuilder animation) {
		EntityPose pose = this.getPose();
		switch (pose) {
			case SWIMMING:
				StateCollectionHelper.configure(animation.rightLeg, false, false);
				StateCollectionHelper.configure(animation.leftLeg, false, false);
			case STANDING:
			case FALL_FLYING:
			case SLEEPING:
			case SPIN_ATTACK:
			case CROUCHING:
			case LONG_JUMPING:
			case DYING:
			default:
				if (this.isMounting()) {
					StateCollectionHelper.configure(animation.rightLeg, false, false);
					StateCollectionHelper.configure(animation.leftLeg, false, false);
				}

		}
	}

	@Unique
	private boolean isWalking() {
		return !this.isDead() && (this.isSwimming() || this.getVelocity().horizontalLength() > 0.03);
	}

	@Unique
	private boolean isMounting() {
		return this.getVehicle() != null;
	}

	@Unique
	public boolean isLeftHanded() {
		return this.getMainArm() == Arm.LEFT;
	}

	public void stopAttackAnimation(float length) {
		IAnimation currentAnimation = this.attackAnimation.base.getAnimation();
		scheduledParticles = null;
		if (currentAnimation != null && currentAnimation instanceof KeyframeAnimationPlayer) {
			int fadeOut = Math.round(length);
			this.attackAnimation.adjustmentModifier.fadeOut(fadeOut);
			this.attackAnimation.base.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fadeOut, Ease.INOUTSINE), (IAnimation) null);
		}

	}

	@Unique
	private FirstPersonConfiguration firstPersonConfig(AnimatedHand animatedHand) {
		boolean showRightItem = true;
		boolean showLeftItem = BetterCombatClientMod.config.isShowingOtherHandFirstPerson || animatedHand == AnimatedHand.TWO_HANDED;
		boolean showRightArm = showRightItem && BetterCombatClientMod.config.isShowingArmsInFirstPerson;
		boolean showLeftArm = showLeftItem && BetterCombatClientMod.config.isShowingArmsInFirstPerson;
		FirstPersonConfiguration config = new FirstPersonConfiguration(showRightArm, showLeftArm, showRightItem, showLeftItem);
		return config;
	}
}
