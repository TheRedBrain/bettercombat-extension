package com.github.theredbrain.bettercombatextension.mixin.bettercombat.api;

import com.github.theredbrain.bettercombatextension.bettercombat.DuckWeaponAttributesAttackMixin;
import net.bettercombat.api.WeaponAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WeaponAttributes.Attack.class)
public class WeaponAttributesAttackMixin implements DuckWeaponAttributesAttackMixin {

	@Unique
	private float attack_range_multiplier = 1.0F;
	@Unique
	private float stamina_cost_multiplier = 1.0F;
	@Unique
	private float movement_speed_multiplier = 1.0F;
	@Unique
	private float attack_speed_multiplier = 1.0F;
	@Unique
	private String damage_type = "";

	@Override
	public float bettercombatextension$getAttackRangeMultiplier() {
		return this.attack_range_multiplier;
	}

	@Override
	public void bettercombatextension$setAttackRangeMultiplier(float attack_range_multiplier) {
		this.attack_range_multiplier = attack_range_multiplier;
	}

	@Override
	public float bettercombatextension$getStaminaCostMultiplier() {
		return this.stamina_cost_multiplier;
	}

	@Override
	public void bettercombatextension$setStaminaCostMultiplier(float stamina_cost_multiplier) {
		this.stamina_cost_multiplier = stamina_cost_multiplier;
	}

	@Override
	public float bettercombatextension$getMovementSpeedMultiplier() {
		return this.movement_speed_multiplier;
	}

	@Override
	public void bettercombatextension$setMovementSpeedMultiplier(float movement_speed_multiplier) {
		this.movement_speed_multiplier = movement_speed_multiplier;
	}

	@Override
	public float bettercombatextension$getAttackSpeedMultiplier() {
		return this.attack_speed_multiplier;
	}

	@Override
	public void bettercombatextension$setAttackSpeedMultiplier(float attack_speed_multiplier) {
		this.attack_speed_multiplier = attack_speed_multiplier;
	}

	@Override
	public String bettercombatextension$getDamageType() {
		return this.damage_type;
	}

	@Override
	public void bettercombatextension$setDamageType(String damage_type) {
		this.damage_type = damage_type;
	}
}
