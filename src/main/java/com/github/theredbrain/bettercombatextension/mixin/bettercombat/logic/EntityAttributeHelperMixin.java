package com.github.theredbrain.bettercombatextension.mixin.bettercombat.logic;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import net.bettercombat.logic.EntityAttributeHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityAttributeHelper.class)
public class EntityAttributeHelperMixin {

	/**
	 * @author TheRedBrain
	 * @reason integrate Attack Range Attribute
	 */
	@Overwrite
	public static boolean itemHasRangeAttribute(ItemStack stack) {
		var attributeModifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
		if (attributeModifiers != null) {
			for (var modifier : attributeModifiers.modifiers()) {
				if (modifier.attribute().value().equals(BetterCombatExtension.getAttackRangeAttribute().value())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @author TheRedBrain
	 * @reason integrate Attack Range Attribute
	 */
	@Overwrite
	public static int rangeModifierCount(ItemStack stack) {
		var attributeModifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
		if (attributeModifiers != null) {
			int count = 0;
			for (var modifier : attributeModifiers.modifiers()) {
				if (modifier.attribute().value().equals(BetterCombatExtension.getAttackRangeAttribute().value())) {
					count += 1;
				}
			}
			return count;
		}
		return 0;
	}
}
