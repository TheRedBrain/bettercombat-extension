package com.github.theredbrain.bettercombatextension.mixin.bettercombat.api;

import com.github.theredbrain.bettercombatextension.bettercombat.DuckWeaponAttributesAttackMixin;
import net.bettercombat.api.WeaponAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WeaponAttributes.Attack.class)
public class WeaponAttributesAttackMixin implements DuckWeaponAttributesAttackMixin {

    @Unique
    private float stamina_cost = 0.0F;
    @Unique
    private String damage_type = "";

    @Override
    public float bettercombatextension$getStaminaCost() {
        return this.stamina_cost;
    }

    @Override
    public void bettercombatextension$setStaminaCost(float staminaCost) {
        this.stamina_cost = staminaCost;
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
