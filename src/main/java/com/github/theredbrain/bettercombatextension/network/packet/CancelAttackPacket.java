package com.github.theredbrain.bettercombatextension.network.packet;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public class CancelAttackPacket implements FabricPacket {
    public static final PacketType<CancelAttackPacket> TYPE = PacketType.create(
            BetterCombatExtension.identifier("cancel_attack"),
            CancelAttackPacket::new
    );

    public final int entityId;

    public CancelAttackPacket(int entityId) {
        this.entityId = entityId;
    }

    public CancelAttackPacket(PacketByteBuf buf) {
        this(buf.readInt());
    }
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.entityId);
    }
}
