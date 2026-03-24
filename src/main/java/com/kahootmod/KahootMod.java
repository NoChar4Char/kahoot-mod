package com.kahootmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.kahootmod.config.ModGameRules;

@Mod(KahootMod.MODID)
public class KahootMod {
    public static final String MODID = "kahootmod";

    public KahootMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModGameRules.init();

        // Register ourselves for game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
}
