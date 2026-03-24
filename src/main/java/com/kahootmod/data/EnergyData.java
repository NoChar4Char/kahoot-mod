package com.kahootmod.data;

import net.minecraft.nbt.CompoundTag;

public class EnergyData {
    private int energy = -1; // -1 indicates uninitialized

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, energy);
    }

    public void consumeEnergy(int amount) {
        this.energy = Math.max(0, this.energy - amount);
    }

    public void addEnergy(int amount, int maxEnergy) {
        this.energy = Math.min(maxEnergy, this.energy + amount);
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("Energy", energy);
    }

    public void loadNBTData(CompoundTag nbt) {
        energy = nbt.getInt("Energy");
    }
}
