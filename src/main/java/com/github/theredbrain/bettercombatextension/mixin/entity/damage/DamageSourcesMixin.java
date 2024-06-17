package com.github.theredbrain.bettercombatextension.mixin.entity.damage;

import com.github.theredbrain.bettercombatextension.bettercombat.DuckWeaponAttributesAttackMixin;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(DamageSources.class)
public abstract class DamageSourcesMixin {

    @Inject(method = "playerAttack", at = @At("RETURN"), cancellable = true)
    public void bettercombatextension$playerAttack(PlayerEntity attacker, CallbackInfoReturnable<DamageSource> cir) {
        AttackHand attackHand = ((EntityPlayer_BetterCombat) attacker).getCurrentAttack();
        if (attackHand != null) {
            String damageTypeString = ((DuckWeaponAttributesAttackMixin) (Object) attackHand.attack()).bettercombatextension$getDamageType();
            if (damageTypeString != null && !damageTypeString.isEmpty() && Identifier.isValid(damageTypeString)) {
                Identifier damageTypeId = Identifier.tryParse(damageTypeString);
                if (damageTypeId != null) {
                    RegistryKey<DamageType> key = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, damageTypeId);
                    Registry<DamageType> registry = attacker.getDamageSources().registry;
                    Optional<RegistryEntry.Reference<DamageType>> optional = registry.getEntry(key);
                    optional.ifPresent(damageTypeReference -> cir.setReturnValue(new DamageSource(damageTypeReference, attacker)));
                }
            }
        }
    }
}
