package com.flashcardmod.network;

import com.flashcardmod.data.EnergyCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class RewardEnergyPacket {
    public final boolean correct;

    public RewardEnergyPacket(boolean correct) {
        this.correct = correct;
    }

    public RewardEnergyPacket(FriendlyByteBuf buf) {
        this.correct = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(correct);
    }

    public void handle(CustomPayloadEvent.Context context) {
        Player player = context.getSender();
        if (player != null) {
            player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                energy.addQuestionAnswered();
                int maxEnergy = com.flashcardmod.config.ModConfig.MAX_ENERGY.get();
                if (this.correct) {
                    energy.addQuestionCorrect();
                    int rewardAmount = com.flashcardmod.config.ModConfig.REWARD_ENERGY.get();
                    energy.addEnergy(rewardAmount, maxEnergy);
                }
                NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), maxEnergy, energy.getQuestionsAnswered(), energy.getQuestionsCorrect()), PacketDistributor.PLAYER.with((net.minecraft.server.level.ServerPlayer) player));
            });
        }
        context.setPacketHandled(true);
    }
}
