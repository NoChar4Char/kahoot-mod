package com.flashcardmod.network;

import com.flashcardmod.client.EnergyHUD;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SyncEnergyPacket {
    public final int energy;
    public final int maxEnergy;
    public final int qAnswered;
    public final int qCorrect;

    public SyncEnergyPacket(int energy, int maxEnergy, int qAnswered, int qCorrect) {
        this.energy = energy;
        this.maxEnergy = maxEnergy;
        this.qAnswered = qAnswered;
        this.qCorrect = qCorrect;
    }

    public SyncEnergyPacket(FriendlyByteBuf buf) {
        this.energy = buf.readInt();
        this.maxEnergy = buf.readInt();
        this.qAnswered = buf.readInt();
        this.qCorrect = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(energy);
        buf.writeInt(maxEnergy);
        buf.writeInt(qAnswered);
        buf.writeInt(qCorrect);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            EnergyHUD.clientEnergy = this.energy;
            EnergyHUD.clientMaxEnergy = this.maxEnergy;
            EnergyHUD.questionsAnswered = this.qAnswered;
            EnergyHUD.questionsCorrect = this.qCorrect;
            if (this.energy <= 0 && net.minecraft.client.Minecraft.getInstance().player != null && net.minecraft.client.Minecraft.getInstance().player.isAlive()) {
                if (!(net.minecraft.client.Minecraft.getInstance().screen instanceof com.flashcardmod.client.MCQScreen)) {
                    com.flashcardmod.QuestionManager.Question q = com.flashcardmod.QuestionManager.getRandomQuestion();
                    if (q != null) {
                        net.minecraft.client.Minecraft.getInstance().setScreen(new com.flashcardmod.client.MCQScreen(q.text, q.answers, q.correctIndex));
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
