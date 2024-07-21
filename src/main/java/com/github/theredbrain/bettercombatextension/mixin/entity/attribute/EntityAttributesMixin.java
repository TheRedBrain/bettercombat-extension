package com.github.theredbrain.bettercombatextension.mixin.entity.attribute;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityAttributes.class)
public class EntityAttributesMixin {
	@Shadow
	private static EntityAttribute register(String id, EntityAttribute attribute) {
		throw new AssertionError();
	}

	static {
		BetterCombatExtension.ATTACK_STAMINA_COST = register(BetterCombatExtension.MOD_ID + ":generic.attack_stamina_cost", new ClampedEntityAttribute("attribute.name.generic.attack_stamina_cost", 1.0, 0.0, 1024.0).setTracked(true));
	}
}
