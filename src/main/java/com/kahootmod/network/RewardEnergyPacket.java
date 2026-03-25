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
                int rewardAmount = com.kahootmod.config.ModConfig.REWARD_ENERGY.get();
                int maxEnergy = com.kahootmod.config.ModConfig.MAX_ENERGY.get();
                energy.addEnergy(rewardAmount, maxEnergy);
                NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), maxEnergy), PacketDistributor.PLAYER.with((net.minecraft.server.level.ServerPlayer) player));
            });
        }
    }
}
