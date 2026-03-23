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
        EnergyHUD.clientEnergy = this.energy;
    }
}
