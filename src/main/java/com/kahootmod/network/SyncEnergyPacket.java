package com.kahootmod.network;

import com.kahootmod.client.EnergyHUD;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SyncEnergyPacket {
    public final int energy;

    public SyncEnergyPacket(int energy) {
        this.energy = energy;
    }

    public SyncEnergyPacket(FriendlyByteBuf buf) {
        this.energy = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(energy);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            EnergyHUD.clientEnergy = this.energy;
            if (this.energy <= 0) {
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
