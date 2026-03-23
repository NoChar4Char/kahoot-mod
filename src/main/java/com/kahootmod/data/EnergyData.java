package com.kahootmod.data;

public class EnergyData {
    public static final int MAX_ENERGY = 100;
    private int energy = MAX_ENERGY;

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(MAX_ENERGY, energy));
    }

    public void consumeEnergy(int amount) {
        setEnergy(this.energy - amount);
    }

    public void addEnergy(int amount) {
        setEnergy(this.energy + amount);
    }
}
