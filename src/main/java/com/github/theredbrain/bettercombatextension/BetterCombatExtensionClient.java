package com.github.theredbrain.bettercombatextension;

import com.github.theredbrain.bettercombatextension.network.packet.CancelAttackPacket;
import com.github.theredbrain.bettercombatextension.network.packet.CancelAttackPacketReceiver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class BetterCombatExtensionClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		// Packets
		ClientPlayNetworking.registerGlobalReceiver(CancelAttackPacket.TYPE, new CancelAttackPacketReceiver());

		ClientPlayNetworking.registerGlobalReceiver(BetterCombatExtension.ServerConfigSync.ID, (client, handler, buf, responseSender) -> {
			BetterCombatExtension.serverConfig = BetterCombatExtension.ServerConfigSync.read(buf);
		});
	}
}
