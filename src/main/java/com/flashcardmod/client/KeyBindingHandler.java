package com.flashcardmod.client;

import com.flashcardmod.network.AnswerPacket;
import com.flashcardmod.network.NetworkHandler;
import com.flashcardmod.QuestionManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class KeyBindingHandler {
    public static final KeyMapping MCQ_KEY = new KeyMapping(
            "key.flashcardmod.open_mcq",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.flashcardmod.keys"
    );

    @Mod.EventBusSubscriber(modid = com.flashcardmod.FlashcardMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
            event.register(MCQ_KEY);
        }
    }

    @Mod.EventBusSubscriber(modid = com.flashcardmod.FlashcardMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeClientEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                while (MCQ_KEY.consumeClick()) {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.screen == null && mc.player != null) {
                        QuestionManager.Question q = QuestionManager.getRandomQuestion();
                        if (q != null) {
                            mc.setScreen(new MCQScreen(q.text, q.answers, q.correctIndex));
                        }
                    }
                }
            }
        }
    }
}
