package com.github.theredbrain.bettercombatextension;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class BetterCombatExtensionMixinCanceller implements MixinCanceller {
	@Override
	public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
		return switch (mixinClassName) {
			case "net.bettercombat.mixin.client.AbstractClientPlayerEntityMixin",
				 "net.bettercombat.mixin.client.ClientPlayerEntityMixin",
				 "net.bettercombat.mixin.player.PlayerEntityMixin",
				 "net.bettercombat.mixin.client.ItemStackTooltipMixin",
				 "net.bettercombat.mixin.client.MinecraftClientInject" -> true;
			default -> false;
		};
	}
}
