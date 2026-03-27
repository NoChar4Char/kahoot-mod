package com.flashcardmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

@Mod(FlashcardMod.MODID)
public class FlashcardMod {
    public static final String MODID = "flashcardmod";

    public FlashcardMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, com.flashcardmod.config.ModConfig.SPEC, "flashcardmod-common.toml");

        // Register ourselves for game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
}
