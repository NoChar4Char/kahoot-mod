package com.kahootmod.network;

import com.kahootmod.data.EnergyCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class RewardEnergyPacket {
    public final int amount;

    public RewardEnergyPacket(int amount) {
        this.amount = amount;
    }

    public RewardEnergyPacket(FriendlyByteBuf buf) {
        this.amount = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(amount);
    }

    public void handle(CustomPayloadEvent.Context context) {
        Player player = context.getSender();
        if (player != null) {
            player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                energy.addEnergy(this.amount);
                NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy()), PacketDistributor.PLAYER.with((net.minecraft.server.level.ServerPlayer) player));
            });
        }
    }
}
