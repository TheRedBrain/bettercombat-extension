package com.github.theredbrain.bettercombatextension.compat;

import com.github.theredbrain.attackrangeattribute.AttackRangeAttribute;
import com.github.theredbrain.attackrangeattribute.entity.AttackRangeUsingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;

public class AttackRangeAttributeCompat {

	public static double getAttackRange(PlayerEntity playerEntity) {
		return ((AttackRangeUsingEntity) playerEntity).attackrangeattribute$getAttackRange();
	}

	public static double getAttackRangeBaseValue(PlayerEntity playerEntity) {
		return playerEntity.getAttributeBaseValue(AttackRangeAttribute.ATTACK_RANGE);
	}

	public static RegistryEntry<EntityAttribute> getAttackRangeAttribute() {
		return AttackRangeAttribute.ATTACK_RANGE;
	}

}
