package com.github.theredbrain.bettercombatextension.network.packet;

import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class CancelAttackPacketReceiver implements ClientPlayNetworking.PlayPayloadHandler<CancelAttackPacket> {

	@Override
	public void receive(CancelAttackPacket payload, ClientPlayNetworking.Context context) {

		int entityId = payload.entityId();
		ClientPlayerEntity player = context.player();

		if (player != null && player.getWorld().getEntityById(entityId) != null) {
			PlayerEntity player2 = (PlayerEntity) player.getWorld().getEntityById(entityId);
			if (player2 != null && player == player2) {
				((MinecraftClient_BetterCombat) MinecraftClient.getInstance()).cancelUpswing();
			}
		}
	}
}
