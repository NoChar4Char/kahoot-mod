package com.kahootmod.network;

import com.kahootmod.client.EnergyHUD;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SyncEnergyPacket {
    public final int energy;
    public final int maxEnergy;

    public SyncEnergyPacket(int energy, int maxEnergy) {
        this.energy = energy;
        this.maxEnergy = maxEnergy;
    }

    public SyncEnergyPacket(FriendlyByteBuf buf) {
        this.energy = buf.readInt();
        this.maxEnergy = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(energy);
        buf.writeInt(maxEnergy);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            EnergyHUD.clientEnergy = this.energy;
            EnergyHUD.clientMaxEnergy = this.maxEnergy;
            if (this.energy <= 0 && net.minecraft.client.Minecraft.getInstance().player != null && net.minecraft.client.Minecraft.getInstance().player.isAlive()) {
                if (!(net.minecraft.client.Minecraft.getInstance().screen instanceof com.kahootmod.client.MCQScreen)) {
                    com.kahootmod.QuestionManager.Question q = com.kahootmod.QuestionManager.getRandomQuestion();
                    if (q != null) {
                        net.minecraft.client.Minecraft.getInstance().setScreen(new com.kahootmod.client.MCQScreen(q.text, q.answers, q.correctIndex));
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
