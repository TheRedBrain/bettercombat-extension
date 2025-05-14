package com.github.theredbrain.bettercombatextension.compat;

import com.github.theredbrain.rpginventory.RPGInventory;

public class RPGInventoryCompat {
	public static boolean isHandSlotOverhaulActive() {
		return RPGInventory.SERVER_CONFIG.handSlotOverhaul.enable_hand_slot_overhaul.get();
	}
}
