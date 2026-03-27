package com.flashcardmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue INITIAL_ENERGY;
    public static final ForgeConfigSpec.IntValue MAX_ENERGY;
    public static final ForgeConfigSpec.IntValue REWARD_ENERGY;

    public static final ForgeConfigSpec.IntValue DRAIN_AFK;
    public static final ForgeConfigSpec.IntValue DRAIN_MOVE;
    public static final ForgeConfigSpec.IntValue DRAIN_SPRINT;
    public static final ForgeConfigSpec.IntValue DRAIN_CROUCH;
    public static final ForgeConfigSpec.IntValue DRAIN_EAT;
    public static final ForgeConfigSpec.IntValue DRAIN_HEAL_HALF_HEART;
    public static final ForgeConfigSpec.IntValue DRAIN_ATTACK;
    public static final ForgeConfigSpec.IntValue DRAIN_BOW;
    public static final ForgeConfigSpec.IntValue DRAIN_BLOCK_INTERACT;
    public static final ForgeConfigSpec.IntValue DRAIN_BLOCK_BREAK_PLACE;

    static {
        BUILDER.push("Energy Limits (Divided by 10 for UI)");
        INITIAL_ENERGY = BUILDER.comment("Initial Energy on spawn (1000 = 100 on UI)").defineInRange("initialEnergy", 1000, 1, 10000);
        MAX_ENERGY = BUILDER.comment("Maximum Energy limit (1000 = 100 on UI)").defineInRange("maxEnergy", 1000, 1, 10000);
        REWARD_ENERGY = BUILDER.comment("Energy rewarded per correct question").defineInRange("rewardEnergy", 100, 0, 10000);
        BUILDER.pop();

        BUILDER.push("Energy Drain Rates (Divided by 10 for UI)");
        DRAIN_AFK = BUILDER.comment("Energy drained passively per second while standing still").defineInRange("drainAfk", 1, 0, 1000);
        DRAIN_MOVE = BUILDER.comment("Energy drained passively per second while walking/flying").defineInRange("drainMove", 5, 0, 1000);
        DRAIN_SPRINT = BUILDER.comment("Energy drained per second while sprinting/swimming").defineInRange("drainSprint", 15, 0, 1000);
        DRAIN_CROUCH = BUILDER.comment("Energy drained per second while crouching").defineInRange("drainCrouch", 2, 0, 1000);
        
        DRAIN_EAT = BUILDER.comment("Energy drained per food item eaten").defineInRange("drainEat", 10, 0, 1000);
        DRAIN_HEAL_HALF_HEART = BUILDER.comment("Energy drained per half-heart of healing").defineInRange("drainHeal", 5, 0, 1000);
        DRAIN_ATTACK = BUILDER.comment("Energy drained per attack swing/hit").defineInRange("drainAttack", 20, 0, 1000);
        DRAIN_BOW = BUILDER.comment("Energy drained per arrow shot").defineInRange("drainBow", 15, 0, 1000);
        DRAIN_BLOCK_INTERACT = BUILDER.comment("Energy drained when right-clicking interactive blocks (chests/doors)").defineInRange("drainBlockInteract", 1, 0, 1000);
        DRAIN_BLOCK_BREAK_PLACE = BUILDER.comment("Energy drained when placing or breaking any block").defineInRange("drainBlockBreakPlace", 1, 0, 1000);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
