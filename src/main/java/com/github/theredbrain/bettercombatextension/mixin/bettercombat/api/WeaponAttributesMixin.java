package com.github.theredbrain.bettercombatextension.mixin.bettercombat.api;

import com.github.theredbrain.bettercombatextension.bettercombat.DuckWeaponAttributesMixin;
import net.bettercombat.api.WeaponAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WeaponAttributes.class)
public class WeaponAttributesMixin implements DuckWeaponAttributesMixin {
	@Unique
	private String two_handed_pose = null;

	@Override
	public String bettercombatextension$getTwoHandedPose() {
		return this.two_handed_pose;
	}

	@Override
	public void bettercombatextension$setTwoHandedPose(String two_handed_pose) {
		this.two_handed_pose = two_handed_pose;
	}

}
