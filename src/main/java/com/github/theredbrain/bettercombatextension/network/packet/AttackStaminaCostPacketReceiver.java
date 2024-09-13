package com.github.theredbrain.bettercombatextension.network.packet;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class AttackStaminaCostPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<AttackStaminaCostPacket> {

	@Override
	public void receive(AttackStaminaCostPacket payload, ServerPlayNetworking.Context context) {

		float staminaCost = payload.staminaCost();
		ServerPlayerEntity player = context.player();

		if (BetterCombatExtension.isStaminaAttributesLoaded) {
			if (BetterCombatExtension.getCurrentStamina(player) <= 0 && !player.isCreative()) {
				ServerPlayNetworking.send(player, new CancelAttackPacket(player.getId()));
			} else {
				BetterCombatExtension.addStamina(player, -staminaCost);
			}
		}
	}
}
