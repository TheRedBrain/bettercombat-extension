package com.github.theredbrain.bettercombatextension.bettercombat;

public interface DuckWeaponAttributesAttackMixin {

	float bettercombatextension$getStaminaCostMultiplier();

	void bettercombatextension$setStaminaCostMultiplier(float stamina_cost_multiplier);

	float bettercombatextension$getAttackSpeedMultiplier();

	void bettercombatextension$setAttackSpeedMultiplier(float attack_speed_multiplier);

	String bettercombatextension$getDamageType();

	void bettercombatextension$setDamageType(String damage_type);
}
