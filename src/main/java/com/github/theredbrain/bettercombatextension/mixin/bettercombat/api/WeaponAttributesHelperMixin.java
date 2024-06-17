package com.github.theredbrain.bettercombatextension.mixin.bettercombat.api;

import com.github.theredbrain.bettercombatextension.bettercombat.DuckWeaponAttributesAttackMixin;
import com.github.theredbrain.bettercombatextension.bettercombat.DuckWeaponAttributesMixin;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributesHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;

@Mixin(WeaponAttributesHelper.class)
public class WeaponAttributesHelperMixin {

    /**
     * @author TheRedBrain
     * @reason integrate two_handed_pose, stamina_cost and damage_type
     */
    @Overwrite(remap=false)
    public static WeaponAttributes override(WeaponAttributes a, WeaponAttributes b) {
        double attackRange = b.attackRange() > 0.0 ? b.attackRange() : a.attackRange();
        String pose = b.pose() != null ? b.pose() : a.pose();
        String off_hand_pose = b.offHandPose() != null ? b.offHandPose() : a.offHandPose();

        String b_two_handed_pose = ((DuckWeaponAttributesMixin) (Object) b).bettercombatextension$getTwoHandedPose();
        String two_handed_pose = b_two_handed_pose != null ? b_two_handed_pose : ((DuckWeaponAttributesMixin) (Object) a).bettercombatextension$getTwoHandedPose();

        Boolean isTwoHanded = b.two_handed() != null ? b.two_handed() : a.two_handed();
        String category = b.category() != null ? b.category() : a.category();
        WeaponAttributes.Attack[] attacks = a.attacks();
        if (b.attacks() != null && b.attacks().length > 0) {
            ArrayList<WeaponAttributes.Attack> overrideAttacks = new ArrayList();

            WeaponAttributes.Attack base;
            for(int i = 0; i < b.attacks().length; ++i) { // = new WeaponAttributes.Attack((WeaponAttributes.Condition[])null, (WeaponAttributes.HitBoxShape)null, 0.0, 0.0, 0.0, (String)null, (WeaponAttributes.Sound)null, (WeaponAttributes.Sound)null);
                if (a.attacks() != null && a.attacks().length > i) {
                    base = a.attacks()[i];
                } else {
                    base = new WeaponAttributes.Attack((WeaponAttributes.Condition[])null, (WeaponAttributes.HitBoxShape)null, 0.0, 0.0, 0.0, (String)null, (WeaponAttributes.Sound)null, (WeaponAttributes.Sound)null);
                    ((DuckWeaponAttributesAttackMixin) (Object) base).bettercombatextension$setDamageType("");
                    ((DuckWeaponAttributesAttackMixin) (Object) base).bettercombatextension$setStaminaCost(0.0F);
                }
                WeaponAttributes.Attack override = b.attacks()[i];
                WeaponAttributes.Attack attack = new WeaponAttributes.Attack(override.conditions() != null ? override.conditions() : base.conditions(), override.hitbox() != null ? override.hitbox() : base.hitbox(), override.damageMultiplier() != 0.0 ? override.damageMultiplier() : base.damageMultiplier(), override.angle() != 0.0 ? override.angle() : base.angle(), override.upswing() != 0.0 ? override.upswing() : base.upswing(), override.animation() != null ? override.animation() : base.animation(), override.swingSound() != null ? override.swingSound() : base.swingSound(), override.impactSound() != null ? override.impactSound() : base.impactSound());

                String override_damageType = ((DuckWeaponAttributesAttackMixin) (Object) override).bettercombatextension$getDamageType();
                if (!override_damageType.isEmpty()) {
                    ((DuckWeaponAttributesAttackMixin) (Object) attack).bettercombatextension$setDamageType(override_damageType);
                }

                float override_staminaCost = ((DuckWeaponAttributesAttackMixin) (Object) override).bettercombatextension$getStaminaCost();
                if (override_staminaCost != 0.0F) {
                    ((DuckWeaponAttributesAttackMixin) (Object) attack).bettercombatextension$setStaminaCost(override_staminaCost);
                }

                overrideAttacks.add(attack);
            }

            attacks = (WeaponAttributes.Attack[])overrideAttacks.toArray(new WeaponAttributes.Attack[0]);
        }

        WeaponAttributes newWeaponAttributes = new WeaponAttributes(attackRange, pose, off_hand_pose, isTwoHanded, category, attacks);
        ((DuckWeaponAttributesMixin) (Object) newWeaponAttributes).bettercombatextension$setTwoHandedPose(two_handed_pose);
        return newWeaponAttributes;
    }
}
