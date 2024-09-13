package com.github.theredbrain.bettercombatextension;

import com.github.theredbrain.bettercombatextension.network.packet.CancelAttackPacket;
import com.github.theredbrain.bettercombatextension.network.packet.CancelAttackPacketReceiver;
import com.github.theredbrain.bettercombatextension.network.packet.ServerConfigSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class BetterCombatExtensionClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		// Packets
		ClientPlayNetworking.registerGlobalReceiver(CancelAttackPacket.PACKET_ID, new CancelAttackPacketReceiver());

		ClientPlayNetworking.registerGlobalReceiver(ServerConfigSyncPacket.PACKET_ID, (payload, context) -> {
			BetterCombatExtension.serverConfig = payload.serverConfig();
		});
	}
}
