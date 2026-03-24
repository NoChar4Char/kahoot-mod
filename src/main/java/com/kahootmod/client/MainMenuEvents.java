package com.kahootmod.client;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.kahootmod.KahootMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MainMenuEvents {
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof TitleScreen screen) {
            // Adds the button to the top-left corner of the Main Menu Screen
            event.addListener(Button.builder(Component.literal("Kahoot Packs"), btn -> {
                screen.getMinecraft().setScreen(new KahootPacksScreen());
            }).bounds(10, 10, 130, 20).build());
        }
    }
}
