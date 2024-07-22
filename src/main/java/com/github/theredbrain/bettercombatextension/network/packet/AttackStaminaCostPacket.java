package com.github.theredbrain.bettercombatextension.network.packet;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public class AttackStaminaCostPacket implements FabricPacket {
	public static final PacketType<AttackStaminaCostPacket> TYPE = PacketType.create(
			BetterCombatExtension.identifier("attack_stamina_cost"),
			AttackStaminaCostPacket::new
	);

	public final float staminaCost;

	public AttackStaminaCostPacket(float staminaCost) {
		this.staminaCost = staminaCost;
	}

	public AttackStaminaCostPacket(PacketByteBuf buf) {
		this(buf.readFloat());
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.staminaCost);
	}
}
