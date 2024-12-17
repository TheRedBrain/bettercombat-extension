package com.github.theredbrain.bettercombatextension;

import com.github.theredbrain.bettercombatextension.config.ClientConfig;
import com.github.theredbrain.bettercombatextension.network.packet.CancelAttackPacket;
import com.github.theredbrain.bettercombatextension.network.packet.CancelAttackPacketReceiver;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class BetterCombatExtensionClient implements ClientModInitializer {
	public static ClientConfig CLIENT_CONFIG = ConfigApiJava.registerAndLoadConfig(ClientConfig::new, RegisterType.CLIENT);

	@Override
	public void onInitializeClient() {

		// Packets
		ClientPlayNetworking.registerGlobalReceiver(CancelAttackPacket.TYPE, new CancelAttackPacketReceiver());

	}
}
