package com.github.theredbrain.bettercombatextension.mixin.bettercombat.client.collision;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import com.github.theredbrain.bettercombatextension.config.ServerConfig;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.client.AttackRangeExtensions;
import net.bettercombat.client.collision.OrientedBoundingBox;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.client.collision.WeaponHitBoxes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(TargetFinder.class)
public abstract class TargetFinderMixin {

	@Shadow
	private static double applyAttackRangeModifiers(PlayerEntity player, double attackRange) {
		throw new AssertionError();
	}

	/**
	 * @author TheRedBrain
	 * @reason integrate "restricted_attack_pitch" option
	 */
	@Overwrite
	public static TargetFinder.TargetResult findAttackTargetResult(PlayerEntity player, Entity cursorTarget, WeaponAttributes.Attack attack, double attackRange) {
		Vec3d origin = TargetFinder.getInitialTracingPoint(player);
		List<Entity> entities = TargetFinder.getInitialTargets(player, cursorTarget, attackRange);
		if (!AttackRangeExtensions.sources().isEmpty()) {
			attackRange = applyAttackRangeModifiers(player, attackRange);
		}

		boolean isSpinAttack = attack.angle() > 180.0;
		Vec3d size = WeaponHitBoxes.createHitbox(attack.hitbox(), attackRange, isSpinAttack);

		// restrict attack pitch
		ServerConfig serverConfig = BetterCombatExtension.SERVER_CONFIG;
		float attackPitch = serverConfig.restrict_attack_pitch.get() ? MathHelper.clamp(player.getPitch(), -serverConfig.attack_pitch_range.get(), serverConfig.attack_pitch_range.get()) : player.getPitch();

		OrientedBoundingBox obb = new OrientedBoundingBox(origin, size, attackPitch, player.getYaw());
		if (!isSpinAttack) {
			obb = obb.offsetAlongAxisZ(size.z / 2.0);
		}
		obb.updateVertex();

		TargetFinder.CollisionFilter collisionFilter = new TargetFinder.CollisionFilter(obb);
		entities = collisionFilter.filter(entities);
		TargetFinder.RadialFilter radialFilter = new TargetFinder.RadialFilter(origin, obb.axisZ, attackRange, attack.angle());
		entities = radialFilter.filter(entities);
		return new TargetFinder.TargetResult(cursorTarget, entities, obb);
	}

}
