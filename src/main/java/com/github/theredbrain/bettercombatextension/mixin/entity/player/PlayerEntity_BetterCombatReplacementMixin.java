package com.github.theredbrain.bettercombatextension.mixin.entity.player;

import com.github.theredbrain.bettercombatextension.bettercombat.DuckWeaponAttributesAttackMixin;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.mixin.PlayerEntityAccessor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin({PlayerEntity.class})
public abstract class PlayerEntity_BetterCombatReplacementMixin implements PlayerAttackProperties, EntityPlayer_BetterCombat {
	private int comboCount = 0;
	private Multimap<EntityAttribute, EntityAttributeModifier> dualWieldingAttributeMap;
	private Multimap<EntityAttribute, EntityAttributeModifier> currentAttackAttributeMap;
	private static UUID dualWieldingSpeedModifierId = UUID.fromString("6b364332-0dc4-11ed-861d-0242ac120002");
	private static UUID currentAttackSpeedModifierId = UUID.fromString("2e7e0032-3cd7-4e62-b14a-6c770e6c65ca");
	private AttackHand lastAttack;

	public PlayerEntity_BetterCombatReplacementMixin() {
	}

	public int getComboCount() {
		return this.comboCount;
	}

	public void setComboCount(int comboCount) {
		this.comboCount = comboCount;
	}

	@Inject(
			method = {"tick"},
			at = {@At("TAIL")}
	)
	public void post_Tick(CallbackInfo ci) {
		Object instance = this;
		if (((PlayerEntity)instance).getWorld().isClient()) {
			((PlayerAttackAnimatable)this).updateAnimationsOnTick();
		}

		this.updateDualWieldingSpeedBoost();
		this.updateAttackSpecificSpeedBoost();
	}

	@ModifyVariable(
			method = {"attack"},
			at = @At("STORE"),
			ordinal = 3
	)
	private boolean disableSweeping(boolean value) {
		if (BetterCombat.config.allow_vanilla_sweeping) {
			return value;
		} else {
			PlayerEntity player = (PlayerEntity) (Object) this;
			AttackHand currentHand = PlayerAttackHelper.getCurrentAttack(player, this.comboCount);
			return currentHand != null ? false : value;
		}
	}

	@Inject(
			method = {"getEquippedStack"},
			at = {@At("HEAD")},
			cancellable = true
	)
	public void getEquippedStack_Pre(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
		boolean mainHandHasTwoHanded = false;
		ItemStack mainHandStack = ((PlayerEntityAccessor)this).getInventory().getMainHandStack();
		WeaponAttributes mainHandAttributes = WeaponRegistry.getAttributes(mainHandStack);
		if (mainHandAttributes != null && mainHandAttributes.isTwoHanded()) {
			mainHandHasTwoHanded = true;
		}

		boolean offHandHasTwoHanded = false;
		ItemStack offHandStack = (ItemStack)((PlayerEntityAccessor)this).getInventory().offHand.get(0);
		WeaponAttributes offHandAttributes = WeaponRegistry.getAttributes(offHandStack);
		if (offHandAttributes != null && offHandAttributes.isTwoHanded()) {
			offHandHasTwoHanded = true;
		}

		if (slot == EquipmentSlot.OFFHAND && (mainHandHasTwoHanded || offHandHasTwoHanded)) {
			cir.setReturnValue(ItemStack.EMPTY);
			cir.cancel();
		}
	}

	@Unique
	private void updateDualWieldingSpeedBoost() {
		PlayerEntity player = (PlayerEntity) (Object) this;
		boolean newState = PlayerAttackHelper.isDualWielding(player);
		boolean currentState = this.dualWieldingAttributeMap != null;
		if (newState != currentState) {
			if (newState) {
				this.dualWieldingAttributeMap = HashMultimap.create();
				double multiplier = (double)(BetterCombat.config.dual_wielding_attack_speed_multiplier - 1.0F);
				this.dualWieldingAttributeMap.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(dualWieldingSpeedModifierId, "Dual wielding attack speed boost", multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE));
				player.getAttributes().addTemporaryModifiers(this.dualWieldingAttributeMap);
			} else if (this.dualWieldingAttributeMap != null) {
				player.getAttributes().removeModifiers(this.dualWieldingAttributeMap);
				this.dualWieldingAttributeMap = null;
			}
		}

	}

	@Unique
	private void updateAttackSpecificSpeedBoost() {
		PlayerEntity player = (PlayerEntity) (Object) this;
		WeaponAttributes.Attack newAttack = null;
		AttackHand attackHand = PlayerAttackHelper.getCurrentAttack(player, this.getComboCount());
		if (attackHand != null) {
			newAttack = attackHand.attack();
		}
		WeaponAttributes.Attack currentAttack = null;
		if (this.lastAttack != null) {
			currentAttack = this.lastAttack.attack();
		}

		boolean bl = false;
		if (newAttack != null && newAttack != currentAttack) {
			float newAttackSpeedModifier = ((DuckWeaponAttributesAttackMixin) (Object) newAttack).bettercombatextension$getAttackSpeedMultiplier();
			if (newAttackSpeedModifier != 1.0F) {
				this.currentAttackAttributeMap = HashMultimap.create();
				double multiplier = (double)(newAttackSpeedModifier - 1.0F);
				this.currentAttackAttributeMap.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(currentAttackSpeedModifierId, "Current attack speed boost", multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE));
				player.getAttributes().addTemporaryModifiers(this.currentAttackAttributeMap);
			} else if (this.currentAttackAttributeMap != null) {
				bl = true;
			}
		} else if (newAttack == null && this.currentAttackAttributeMap != null) {
			bl = true;
		}

		if (bl) {
			player.getAttributes().removeModifiers(this.currentAttackAttributeMap);
			this.currentAttackAttributeMap = null;
		}
	}

	@ModifyArg(
			method = {"attack"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"
			),
			index = 0
	)
	public Hand getHand(Hand hand) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		AttackHand currentHand = PlayerAttackHelper.getCurrentAttack(player, this.comboCount);
		if (currentHand != null) {
			return currentHand.isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
		} else {
			return Hand.MAIN_HAND;
		}
	}

	@Redirect(
			method = {"attack"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"
			)
	)
	public ItemStack getMainHandStack_Redirect(PlayerEntity instance) {
		if (this.comboCount < 0) {
			return instance.getMainHandStack();
		} else {
			AttackHand hand = PlayerAttackHelper.getCurrentAttack(instance, this.comboCount);
			if (hand == null) {
				boolean isOffHand = PlayerAttackHelper.shouldAttackWithOffHand(instance, this.comboCount);
				return isOffHand ? ItemStack.EMPTY : instance.getMainHandStack();
			} else {
				this.lastAttack = hand;
				return hand.itemStack();
			}
		}
	}

	@Redirect(
			method = {"attack"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"
			)
	)
	public void setStackInHand_Redirect(PlayerEntity instance, Hand handArg, ItemStack itemStack) {
		if (this.comboCount < 0) {
			instance.setStackInHand(handArg, itemStack);
		}

		AttackHand hand = this.lastAttack;
		if (hand == null) {
			hand = PlayerAttackHelper.getCurrentAttack(instance, this.comboCount);
		}

		if (hand == null) {
			instance.setStackInHand(handArg, itemStack);
		} else {
			Hand redirectedHand = hand.isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
			instance.setStackInHand(redirectedHand, itemStack);
		}
	}

	public @Nullable AttackHand getCurrentAttack() {
		if (this.comboCount < 0) {
			return null;
		} else {
			PlayerEntity player = (PlayerEntity) (Object) this;
			return PlayerAttackHelper.getCurrentAttack(player, this.comboCount);
		}
	}
}
