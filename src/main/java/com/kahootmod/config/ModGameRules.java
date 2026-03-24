package com.kahootmod.config;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.IntegerValue;

public class ModGameRules {
    public static GameRules.Key<IntegerValue> INITIAL_ENERGY;
    public static GameRules.Key<IntegerValue> MAX_ENERGY;
    public static GameRules.Key<IntegerValue> REWARD_ENERGY;
    public static GameRules.Key<IntegerValue> DRAIN_SPRINT_SWIM;
    public static GameRules.Key<IntegerValue> DRAIN_MOVE_OTHER;
    public static GameRules.Key<IntegerValue> DRAIN_BLOCK;
    public static GameRules.Key<IntegerValue> DRAIN_EAT;
    public static GameRules.Key<IntegerValue> DRAIN_HEAL;
    public static GameRules.Key<IntegerValue> DRAIN_CROUCH;
    public static GameRules.Key<IntegerValue> DRAIN_ATTACK;
    public static GameRules.Key<IntegerValue> DRAIN_BOW;

    public static void init() {
        INITIAL_ENERGY = GameRules.register("kahootInitialEnergy", Category.PLAYER, IntegerValue.create(1000));
        MAX_ENERGY = GameRules.register("kahootMaxEnergy", Category.PLAYER, IntegerValue.create(1000));
        REWARD_ENERGY = GameRules.register("kahootRewardEnergy", Category.PLAYER, IntegerValue.create(200));
        
        DRAIN_SPRINT_SWIM = GameRules.register("kahootDrainSprintSwim", Category.PLAYER, IntegerValue.create(10));
        DRAIN_MOVE_OTHER = GameRules.register("kahootDrainMoveOther", Category.PLAYER, IntegerValue.create(10));
        DRAIN_BLOCK = GameRules.register("kahootDrainBlock", Category.PLAYER, IntegerValue.create(1));
        DRAIN_EAT = GameRules.register("kahootDrainEat", Category.PLAYER, IntegerValue.create(5));
        DRAIN_HEAL = GameRules.register("kahootDrainHeal", Category.PLAYER, IntegerValue.create(10));
        DRAIN_CROUCH = GameRules.register("kahootDrainCrouch", Category.PLAYER, IntegerValue.create(100)); // Default drains 10.0 visual energy
        DRAIN_ATTACK = GameRules.register("kahootDrainAttack", Category.PLAYER, IntegerValue.create(30));
        DRAIN_BOW = GameRules.register("kahootDrainBow", Category.PLAYER, IntegerValue.create(20));
    }
}
