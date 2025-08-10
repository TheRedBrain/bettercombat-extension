package com.github.theredbrain.bettercombatextension.compat;

import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerInventoryMixin;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class RPGInventoryCompat {
	public static boolean isHandSlotOverhaulActive() {
		return RPGInventory.SERVER_CONFIG.handSlotOverhaul.enable_hand_slot_overhaul.get();
	}

	public static void setMainHandSlot(PlayerInventory playerInventory, ItemStack itemStack) {
		((DuckPlayerInventoryMixin) playerInventory).rpginventory$setHand(itemStack);
	}
}
