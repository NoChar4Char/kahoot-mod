package com.flashcardmod.data;

import net.minecraft.nbt.CompoundTag;

public class EnergyData {
    private int energy = -1; // -1 indicates uninitialized
    private int questionsAnswered = 0;
    private int questionsCorrect = 0;
    private boolean hasSeenWelcome = false;

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

    public int getQuestionsAnswered() { return questionsAnswered; }
    public int getQuestionsCorrect() { return questionsCorrect; }
    public void addQuestionAnswered() { this.questionsAnswered++; }
    public void addQuestionCorrect() { this.questionsCorrect++; }
    public boolean hasSeenWelcome() { return hasSeenWelcome; }
    public void setHasSeenWelcome(boolean hasSeenWelcome) { this.hasSeenWelcome = hasSeenWelcome; }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("Energy", energy);
        nbt.putInt("questionsAnswered", questionsAnswered);
        nbt.putInt("questionsCorrect", questionsCorrect);
        nbt.putBoolean("hasSeenWelcome", hasSeenWelcome);
    }

    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("Energy")) energy = nbt.getInt("Energy");
        if (nbt.contains("questionsAnswered")) questionsAnswered = nbt.getInt("questionsAnswered");
        if (nbt.contains("questionsCorrect")) questionsCorrect = nbt.getInt("questionsCorrect");
        if (nbt.contains("hasSeenWelcome")) hasSeenWelcome = nbt.getBoolean("hasSeenWelcome");
    }
}
