package com.github.theredbrain.bettercombatextension.mixin.client;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import com.github.theredbrain.bettercombatextension.bettercombat.DuckWeaponAttributesAttackMixin;
import com.github.theredbrain.bettercombatextension.client.DuckMinecraftClientMixin;
import com.github.theredbrain.bettercombatextension.config.ServerConfig;
import com.github.theredbrain.bettercombatextension.entity.DuckLivingEntityMixin;
import com.github.theredbrain.bettercombatextension.network.packet.AttackStaminaCostPacket;
import me.shedaniel.autoconfig.AutoConfig;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.PlatformClient;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.Keybindings;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.client.particle.SlashParticleUtil;
import net.bettercombat.config.ClientConfigWrapper;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.logic.WeaponSwing;
import net.bettercombat.mixin.client.MinecraftClientAccessor;
import net.bettercombat.network.Packets;
import net.bettercombat.utils.PatternMatching;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({MinecraftClient.class})
public abstract class MinecraftClient_BetterCombatReplacementMixin implements MinecraftClient_BetterCombat, DuckMinecraftClientMixin {
	@Shadow
	public ClientWorld world;
	@Shadow
	public @Nullable ClientPlayerEntity player;
	@Shadow
	private int itemUseCooldown;
	@Shadow
	public int attackCooldown;
	@Shadow
	@Final
	public InGameHud inGameHud;
	@Shadow
	public @Nullable HitResult crosshairTarget;
	@Unique
	private boolean isHoldingAttackInput = false;
	@Unique
	private boolean isHarvesting = false;
	@Unique
	private ItemStack upswingStack;
	@Unique
	private ItemStack lastAttacedWithItemStack;
	//	@Unique
//	private int upswingTicks = 0;
	private WeaponSwing ongoingSwing;
	@Unique
	private int lastAttacked = 1000;
	@Unique
	private float lastSwingDuration = 0.0F;
	@Unique
	private int comboReset = 0;
	@Unique
	private List<Entity> targetsInReach = null;

	public MinecraftClient_BetterCombatReplacementMixin() {
	}

	@Unique
	private MinecraftClient thisClient() {
		return (MinecraftClient) (Object) this;
	}

	@Inject(
			method = {"disconnect(Lnet/minecraft/client/gui/screen/Screen;)V"},
			at = {@At("TAIL")}
	)
	private void disconnect_TAIL(Screen screen, CallbackInfo ci) {
		BetterCombatClientMod.ENABLED = false;
	}

	@Inject(
			method = {"doAttack"},
			at = {@At("HEAD")},
			cancellable = true
	)
	private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
		if (BetterCombatClientMod.ENABLED) {
			MinecraftClient client = this.thisClient();
			WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
			if (attributes != null && attributes.attacks() != null) {
				if (this.isTargetingMineableBlock() || this.isHarvesting) {
					this.isHarvesting = true;
					return;
				}

				this.startUpswing(attributes);
				info.setReturnValue(false);
				info.cancel();
			}

		}
	}

	@Inject(
			method = {"handleBlockBreaking"},
			at = {@At("HEAD")},
			cancellable = true
	)
	private void pre_handleBlockBreaking(boolean bl, CallbackInfo ci) {
		if (BetterCombatClientMod.ENABLED) {
			MinecraftClient client = this.thisClient();
			WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
			if (attributes != null && attributes.attacks() != null) {
				boolean isPressed = client.options.attackKey.isPressed();
				if (isPressed && !this.isHoldingAttackInput) {
					if (this.isTargetingMineableBlock() || this.isHarvesting) {
						this.isHarvesting = true;
						return;
					}

					ci.cancel();
				}

				if (BetterCombatClientMod.config.isHoldToAttackEnabled && !BetterCombatExtension.SERVER_CONFIG.disable_better_combat_hold_to_attack.get() && isPressed) {
					this.isHoldingAttackInput = true;
					this.startUpswing(attributes);
					ci.cancel();
				} else {
					this.isHarvesting = false;
					this.isHoldingAttackInput = false;
				}
			}

		}
	}

	@Inject(
			method = {"doItemUse"},
			at = {@At("HEAD")},
			cancellable = true
	)
	private void pre_doItemUse(CallbackInfo ci) {
		if (BetterCombatClientMod.ENABLED) {
			AttackHand hand = this.getCurrentHand();
			if (hand != null) {
				double upswingRate = hand.upswingRate();
				if (currentUpswingTicks() > 0 || (double) this.player.getAttackCooldownProgress(0.0F) < 1.0 - upswingRate) {
					ci.cancel();
				}

			}
		}
	}

	@Unique
	private boolean isTargetingMineableBlock() {
		if (!BetterCombatClientMod.config.isMiningWithWeaponsEnabled) {
			return false;
		} else {
			String regex = BetterCombatClientMod.config.mineWithWeaponBlacklist;
			if (regex != null && !regex.isEmpty()) {
				ItemStack itemStack = this.player.getMainHandStack();
				String id = Registries.ITEM.getId(itemStack.getItem()).toString();
				if (PatternMatching.matches(id, regex)) {
					return false;
				}
			}

			if (BetterCombatClientMod.config.isAttackInsteadOfMineWhenEnemiesCloseEnabled && this.hasTargetsInReach()) {
				return false;
			} else {
				MinecraftClient client = this.thisClient();
				HitResult crosshairTarget = client.crosshairTarget;
				if (crosshairTarget != null && crosshairTarget.getType() == HitResult.Type.BLOCK) {
					BlockHitResult blockHitResult = (BlockHitResult) crosshairTarget;
					BlockPos pos = blockHitResult.getBlockPos();
					BlockState clicked = this.world.getBlockState(pos);
					if (!this.shouldSwingThruGrass()) {
						return true;
					}

					if (!clicked.getCollisionShape(this.world, pos).isEmpty() || clicked.getHardness(this.world, pos) != 0.0F) {
						return true;
					}
				}

				return false;
			}
		}
	}

	@Unique
	private boolean shouldSwingThruGrass() {
		if (!BetterCombatClientMod.config.isSwingThruGrassEnabled) {
			return false;
		} else if (BetterCombatClientMod.config.isSwingThruGrassSmart && !this.hasTargetsInReach()) {
			return false;
		} else {
			String regex = BetterCombatClientMod.config.swingThruGrassBlacklist;
			if (regex != null && !regex.isEmpty()) {
				ItemStack itemStack = this.player.getMainHandStack();
				String id = Registries.ITEM.getId(itemStack.getItem()).toString();
				return !PatternMatching.matches(id, regex);
			} else {
				return true;
			}
		}
	}

	@Unique
	private int currentTime() {
		if (player == null) {
			return 0;
		}
		return player.age;
	}

	@Unique
	private int currentUpswingTicks() {
		if (ongoingSwing == null) {
			return 0;
		}
		return ongoingSwing.upswingTicksLeft(currentTime());
	}

	@Unique
	private void startUpswing(WeaponAttributes attributes) {
		if (this.player.isRiding()) {
			// isRiding is `isHandsBusy()` according to official mappings
			// Support for revival mod
			return;
		}

		AttackHand attackHand = this.getCurrentHand();
		if (attackHand == null) {
			return;
		}

		float upswingRate = (float) attackHand.upswingRate();
		if (currentUpswingTicks() > 0
				|| attackCooldown > 0
				|| player.isUsingItem()
				|| player.getAttackCooldownProgress(0) < (1.0 - upswingRate)) {
			return;
		}

		if (BetterCombatExtension.isStaminaAttributesLoaded && ((DuckWeaponAttributesAttackMixin) (Object) attackHand.attack()).bettercombatextension$getStaminaCostMultiplier() > 0.0F && ((DuckLivingEntityMixin) player).bettercombatextension$getAttackStaminaCost() > 0.0F && BetterCombatExtension.getCurrentStamina(this.player)/* * BetterCombatExtension.serverConfig.global_attack_stamina_cost_multiplier*/ <= 0.0F && !this.player.isCreative()) {
			this.player.sendMessage(Text.translatable("hud.message.staminaTooLow"), true);
			return;
		}

		// Starting upswing
		this.player.stopUsingItem();

		this.lastAttacked = 0;
		this.upswingStack = this.player.getMainHandStack();
		float attackCooldownTicksFloat = PlayerAttackHelper.getAttackCooldownTicksCapped(this.player);
		int attackCooldownTicks = Math.round(attackCooldownTicksFloat);
		this.comboReset = Math.round(attackCooldownTicksFloat * BetterCombatMod.config.combo_reset_rate);
		int upswingTicks = Math.max(Math.round(attackCooldownTicksFloat * upswingRate), 1); // At least 1 upswing ticks
		this.ongoingSwing = new WeaponSwing(attackHand, currentTime(), upswingTicks, attackCooldownTicksFloat);
		this.lastSwingDuration = attackCooldownTicksFloat;
		this.itemUseCooldown = attackCooldownTicks;
		this.setMiningCooldown(attackCooldownTicks);
		String animationName = attackHand.attack().animation();
		boolean isOffHand = attackHand.isOffHand();
		AnimatedHand animatedHand = AnimatedHand.from(isOffHand, attributes.isTwoHanded());
		((PlayerAttackAnimatable) this.player).playAttackAnimation(animationName, animatedHand, attackCooldownTicksFloat, upswingRate);

		var particles = SlashParticleUtil.trailParticlesFromAttack(attackHand);
		var appearance = SlashParticleUtil.appearanceFromItemStack(attackHand.itemStack());
		var packet = new Packets.AttackAnimation(
				player.getId(), animatedHand, animationName, attackCooldownTicksFloat, upswingRate,
				(float) PlayerAttackHelper.getStaticRange(player, attackHand.itemStack()),
				upswingTicks,
				new Packets.SwingParticles(particles, appearance)
		);
		Platform.networkC2S_Send(packet);
		BetterCombatClientEvents.ATTACK_START.invoke((handler) -> {
			handler.onPlayerAttackStart(this.player, attackHand);
		});
	}

	@Unique
	private void cancelSwingIfNeeded() {
		if (this.upswingStack != null && !areItemStackEqual(this.player.getMainHandStack(), this.upswingStack)) {
			this.cancelWeaponSwing();
		}
	}

	@Unique
	private void attackFromUpswingIfNeeded() {
		if (ongoingSwing != null && currentUpswingTicks() == 0) {
			this.performAttack();
			this.upswingStack = null;
		}
	}

	@Unique
	private void resetComboIfNeeded() {
		if (this.lastAttacked > this.comboReset && this.getComboCount() > 0) {
			this.setComboCount(0);
		}

		if (!PlayerAttackHelper.shouldAttackWithOffHand(this.player, this.getComboCount()) && (this.player.getMainHandStack() == null || this.lastAttacedWithItemStack != null && !this.lastAttacedWithItemStack.getItem().equals(this.player.getMainHandStack().getItem()))) {
			this.setComboCount(0);
		}

	}

	@Unique
	private boolean shouldUpdateTargetsInReach() {
		if (!BetterCombatClientMod.config.isHighlightCrosshairEnabled && !BetterCombatClientMod.config.isAttackInsteadOfMineWhenEnemiesCloseEnabled) {
			return false;
		} else {
			return this.targetsInReach == null;
		}
	}

	@Unique
	private void updateTargetsInReach(List<Entity> targets) {
		this.targetsInReach = targets;
	}

	@Unique
	private void updateTargetsIfNeeded() {
		if (this.shouldUpdateTargetsInReach()) {
			List<Entity> targets = List.of();
			AttackHand hand = PlayerAttackHelper.getCurrentAttack(this.player, this.getComboCount());
			if (hand != null) {
				WeaponAttributes attributes = WeaponRegistry.getAttributes(hand.itemStack());
				double range = PlayerAttackHelper.getRangeForItem(this.player, hand.itemStack());
				if (attributes != null && attributes.attacks() != null) {
					targets = TargetFinder.findAttackTargets(this.player, this.getCursorTarget(), hand.attack(), range);
				}
			}

			this.updateTargetsInReach(targets);
		}

	}

	@Inject(
			method = {"tick"},
			at = {@At("HEAD")}
	)
	private void pre_Tick(CallbackInfo ci) {
		if (this.player == null) {
			return;
		}
		this.targetsInReach = null;
		this.lastAttacked += 1;

		if (ongoingSwing != null) {
			if (ongoingSwing.ticksLeft(currentTime()) <= 0) {
				ongoingSwing = null;
			}
		}
		this.cancelSwingIfNeeded();
		this.attackFromUpswingIfNeeded();
		this.updateTargetsIfNeeded();
		this.resetComboIfNeeded();
	}

	@Inject(
			method = {"tick"},
			at = {@At("TAIL")}
	)
	private void post_Tick(CallbackInfo ci) {
		if (this.player == null) {
			return;
		}
		if (Keybindings.toggleMineKeyBinding.wasPressed()) {
			BetterCombatClientMod.config.isMiningWithWeaponsEnabled = !BetterCombatClientMod.config.isMiningWithWeaponsEnabled;
			AutoConfig.getConfigHolder(ClientConfigWrapper.class).save();
			String message = I18n.translate(BetterCombatClientMod.config.isMiningWithWeaponsEnabled ? "hud.bettercombat.mine_with_weapons_on" : "hud.bettercombat.mine_with_weapons_off", new Object[0]);
			this.inGameHud.setOverlayMessage(Text.literal(message), false);
		}
	}

	@Unique
	private void performAttack() {
		ServerConfig serverConfig = BetterCombatExtension.SERVER_CONFIG;
		if (this.player == null) {
			return;
		}
		if (Keybindings.feintKeyBinding.isPressed()) {
			this.player.resetLastAttackedTicks();
			this.cancelWeaponSwing();
			AttackHand hand = this.getCurrentHand();
			if (hand != null && BetterCombatExtension.isStaminaAttributesLoaded) {
				ClientPlayNetworking.send(new AttackStaminaCostPacket(((DuckLivingEntityMixin) this.player).bettercombatextension$getAttackStaminaCost() * ((DuckWeaponAttributesAttackMixin) (Object) hand.attack()).bettercombatextension$getStaminaCostMultiplier() * serverConfig.global_feint_stamina_cost_multiplier.get()));
			}
			// feinting an attack increases combo count
			if (serverConfig.feinting_increases_combo_count.get()) {
				this.setComboCount(this.getComboCount() + 1);
			}
			return;
		}
		AttackHand hand = this.getCurrentHand();
		if (hand == null) {
			return;
		}
		WeaponAttributes.Attack attack = hand.attack();
		if (BetterCombatExtension.isStaminaAttributesLoaded) {
			ClientPlayNetworking.send(new AttackStaminaCostPacket(((DuckLivingEntityMixin) this.player).bettercombatextension$getAttackStaminaCost() * ((DuckWeaponAttributesAttackMixin) (Object) attack).bettercombatextension$getStaminaCostMultiplier() * serverConfig.global_attack_stamina_cost_multiplier.get()));
		}
		double upswingRate = hand.upswingRate();
		if (((double) this.player.getAttackCooldownProgress(0.0F) < 1.0 - upswingRate)) {
			return;
		}
		Entity cursorTarget = this.getCursorTarget();
		double range = PlayerAttackHelper.getRangeForItem(this.player, hand.itemStack());
		range *= hand.attack().rangeMultiplier();
		List<Entity> targets = TargetFinder.findAttackTargets(
				this.player,
				cursorTarget,
				attack,
				range
		);
		this.updateTargetsInReach(targets);
		if (targets.size() == 0) {
			PlatformClient.onEmptyLeftClick(this.player);
			if (this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;
				BlockPos pos = blockHitResult.getBlockPos();
				Packets.C2S_BlockHit packet = new Packets.C2S_BlockHit(pos);
				Platform.networkC2S_Send(packet);
			}
		}

		Packets.C2S_AttackRequest packet = new Packets.C2S_AttackRequest(this.getComboCount(), this.player.isSneaking(), this.player.getInventory().selectedSlot, cursorTarget, targets);
		Platform.networkC2S_Send(packet);
		for (var target : targets) {
			player.attack(target);
		}

		this.player.resetLastAttackedTicks();
		BetterCombatClientEvents.ATTACK_HIT.invoke((handler) -> {
			handler.onPlayerAttackStart(this.player, hand, targets, cursorTarget);
		});

		var particles = SlashParticleUtil.trailParticlesFromAttack(hand);
		var appearance = SlashParticleUtil.appearanceFromItemStack(hand.itemStack());
		SlashParticleUtil.spawnParticles(player, hand.isOffHand(), (float) PlayerAttackHelper.getStaticRange(player, hand.itemStack()), particles, appearance);

		this.setComboCount(this.getComboCount() + 1);
		if (!hand.isOffHand()) {
			this.lastAttacedWithItemStack = hand.itemStack();
		}
	}

	@Unique
	private AttackHand getCurrentHand() {
		return PlayerAttackHelper.getCurrentAttack(this.player, this.getComboCount());
	}

	@Override
	public Hand bettercombatextension$getCurrentAttackHand() {
		return getCurrentHand() != null && getCurrentHand().isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
	}

	@Unique
	private void setComboCount(int comboCount) {
		((PlayerAttackProperties) this.player).setComboCount(comboCount);
	}

	@Unique
	private static boolean areItemStackEqual(ItemStack left, ItemStack right) {
		if (left == null && right == null) {
			return true;
		} else {
			return left != null && right != null ? ItemStack.areEqual(left, right) : false;
		}
	}

	@Unique
	private void setMiningCooldown(int ticks) {
		MinecraftClient client = this.thisClient();
		((MinecraftClientAccessor) client).setAttackCooldown(ticks);
	}

	@Unique
	private void cancelWeaponSwing() {
		int downWind = (int) Math.round((double) PlayerAttackHelper.getAttackCooldownTicksCapped(this.player) * (1.0 - 0.5 * (double) BetterCombatMod.config.upswing_multiplier));
		((PlayerAttackAnimatable) this.player).stopAttackAnimation((float) downWind);
		Packets.AttackAnimation packet = Packets.AttackAnimation.stop(this.player.getId(), downWind);
		Platform.networkC2S_Send(packet);
		this.upswingStack = null;
		this.itemUseCooldown = 0;
		this.setMiningCooldown(0);
	}

	public int getComboCount() {
		return ((PlayerAttackProperties) this.player).getComboCount();
	}

	public boolean hasTargetsInReach() {
		return this.targetsInReach != null && !this.targetsInReach.isEmpty();
	}

	public float getSwingProgress() {
		return !((float) this.lastAttacked > this.lastSwingDuration) && !(this.lastSwingDuration <= 0.0F) ? (float) this.lastAttacked / this.lastSwingDuration : 1.0F;
	}

	public int getUpswingTicks() {
		return currentUpswingTicks();
	}

	public void cancelUpswing() {
		if (currentUpswingTicks() > 0) {
			this.cancelWeaponSwing();
		}
	}

	@Override
	public AttackHand getCurrentAttackHand() {
		if (this.ongoingSwing != null) {
			return this.ongoingSwing.attackHand();
		}
		return null;
	}
}
