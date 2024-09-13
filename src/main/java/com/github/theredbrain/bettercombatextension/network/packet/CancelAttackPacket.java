package com.github.theredbrain.bettercombatextension.network.packet;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record CancelAttackPacket(int entityId) implements CustomPayload {
	public static final CustomPayload.Id<CancelAttackPacket> PACKET_ID = new CustomPayload.Id<>(BetterCombatExtension.identifier("cancel_attack"));
	public static final PacketCodec<RegistryByteBuf, CancelAttackPacket> PACKET_CODEC = PacketCodec.of(CancelAttackPacket::write, CancelAttackPacket::new);

	public CancelAttackPacket(RegistryByteBuf registryByteBuf) {
		this(registryByteBuf.readInt());
	}

	private void write(RegistryByteBuf registryByteBuf) {
		registryByteBuf.writeInt(entityId);
	}

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}
}
