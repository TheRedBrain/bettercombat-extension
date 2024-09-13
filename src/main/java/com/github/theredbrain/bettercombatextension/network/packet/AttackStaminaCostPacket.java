package com.github.theredbrain.bettercombatextension.network.packet;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record AttackStaminaCostPacket(float staminaCost) implements CustomPayload {
	public static final CustomPayload.Id<AttackStaminaCostPacket> PACKET_ID = new CustomPayload.Id<>(BetterCombatExtension.identifier("attack_stamina_cost"));
	public static final PacketCodec<RegistryByteBuf, AttackStaminaCostPacket> PACKET_CODEC = PacketCodec.of(AttackStaminaCostPacket::write, AttackStaminaCostPacket::new);

	public AttackStaminaCostPacket(RegistryByteBuf registryByteBuf) {
		this(registryByteBuf.readFloat());
	}

	private void write(RegistryByteBuf registryByteBuf) {
		registryByteBuf.writeFloat(staminaCost);
	}

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}
}
