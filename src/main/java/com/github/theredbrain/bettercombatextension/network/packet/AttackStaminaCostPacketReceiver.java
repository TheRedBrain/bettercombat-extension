package com.github.theredbrain.bettercombatextension.network.packet;

import com.github.theredbrain.staminaattributes.entity.StaminaUsingEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class AttackStaminaCostPacketReceiver implements ServerPlayNetworking.PlayPacketHandler<AttackStaminaCostPacket> {

    @Override
    public void receive(AttackStaminaCostPacket packet, ServerPlayerEntity player, PacketSender responseSender) {

        int staminaCost = packet.staminaCost;

        if (((StaminaUsingEntity) player).staminaattributes$getStamina() <= 0 && !player.isCreative()) {
            ServerPlayNetworking.send(player, new CancelAttackPacket(player.getId()));
        } else {
            ((StaminaUsingEntity) player).staminaattributes$addStamina(-(staminaCost));
        }
    }
}
