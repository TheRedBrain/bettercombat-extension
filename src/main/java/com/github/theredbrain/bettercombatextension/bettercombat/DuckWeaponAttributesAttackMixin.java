package com.github.theredbrain.bettercombatextension.bettercombat;

public interface DuckWeaponAttributesAttackMixin {
	float bettercombatextension$getStaminaCostMultiplier();

	void bettercombatextension$setStaminaCostMultiplier(float staminaCostMultiplier);

	String bettercombatextension$getDamageType();

	void bettercombatextension$setDamageType(String damage_type);

	float bettercombatextension$getMovementSpeedMultiplier();

	void bettercombatextension$setMovementSpeedMultiplier(float movement_speed_multiplier);

	float bettercombatextension$getAttackSpeedMultiplier();

	void bettercombatextension$setAttackSpeedMultiplier(float attack_speed_multiplier);
}
