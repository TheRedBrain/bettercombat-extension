package com.github.theredbrain.bettercombatextension.compatability;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.theredbrain.bettercombatextension.BetterCombatExtension;

public class ShoulderSurfingCompat {

	public static boolean isShoulderSurfingCameraDecoupled() {
		return BetterCombatExtension.isShoulderSurfingLoaded && ShoulderSurfing.getInstance().isShoulderSurfing() && Config.CLIENT.isCameraDecoupled();
	}

}
