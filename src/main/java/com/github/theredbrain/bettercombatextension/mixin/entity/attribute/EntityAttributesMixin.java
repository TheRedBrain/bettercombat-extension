package com.github.theredbrain.bettercombatextension.mixin.entity.attribute;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityAttributes.class)
public class EntityAttributesMixin {
	static {
		BetterCombatExtension.ATTACK_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, BetterCombatExtension.identifier("generic.attack_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.attack_stamina_cost", 1.0, 0.0, 1024.0).setTracked(true));
	}
}
