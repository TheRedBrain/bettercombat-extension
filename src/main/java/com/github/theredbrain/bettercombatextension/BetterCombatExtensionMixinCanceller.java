package com.github.theredbrain.bettercombatextension;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class BetterCombatExtensionMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (mixinClassName.equals("net.bettercombat.mixin.client.AbstractClientPlayerEntityMixin")) {
            return true;
        }
        if (mixinClassName.equals("net.bettercombat.mixin.client.MinecraftClientInject")) {
            return true;
        }
        if (mixinClassName.equals("net.bettercombat.mixin.client.ClientPlayerEntityMixin")) {
            return true;
        }
        return false;
    }
}
