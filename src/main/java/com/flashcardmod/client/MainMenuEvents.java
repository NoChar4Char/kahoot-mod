package com.flashcardmod.client;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.flashcardmod.FlashcardMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MainMenuEvents {
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof TitleScreen screen) {
            // Adds the button to the top-left corner of the Main Menu Screen
            event.addListener(Button.builder(Component.literal("Flashcard Packs"), btn -> {
                screen.getMinecraft().setScreen(new FlashcardPacksScreen());
            }).bounds(10, 10, 130, 20).build());
        }
    }

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (com.flashcardmod.QuestionManager.getQuestionCount() == 0) {
            if (event.getPlayer() != null && event.getPlayer().connection != null) {
                event.getPlayer().connection.getConnection().disconnect(Component.literal("§c[Flashcard Mod]§r\nYou must load a Flashcard Pack before joining a world!\nClick 'Flashcard Packs' on the main menu."));
            }
        }
    }

    @SubscribeEvent
    public static void onMainMenuRender(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof net.minecraft.client.gui.screens.TitleScreen) {
            if (com.flashcardmod.QuestionManager.getQuestionCount() == 0) {
                event.getGuiGraphics().drawString(net.minecraft.client.Minecraft.getInstance().font, "WARNING: No Flashcard Pack Loaded!", 10, 35, 0xFF0000, true);
            }
        }
    }
}
