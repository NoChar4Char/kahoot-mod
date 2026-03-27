package com.flashcardmod.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.ResourceLocation;

@Mod.EventBusSubscriber(modid = com.flashcardmod.FlashcardMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EnergyHUD {
    
    public static int clientEnergy = 100;
    public static int clientMaxEnergy = 1000;
    public static int questionsAnswered = 0;
    public static int questionsCorrect = 0;

    @SubscribeEvent
    public static void registerOverlays(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().add(
            ForgeLayeredDraw.VANILLA_ROOT,
            ResourceLocation.fromNamespaceAndPath(com.flashcardmod.FlashcardMod.MODID, "energy_hud"),
            (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options.hideGui || mc.player == null || mc.player.isSpectator()) return;

                int screenWidth = mc.getWindow().getGuiScaledWidth();
                int screenHeight = mc.getWindow().getGuiScaledHeight();

                int totalIcons = 10;
                float ratio = (float) clientEnergy / clientMaxEnergy;
                float filledIcons = (float) Math.ceil(ratio * totalIcons * 2.0f) / 2.0f;
                int startX = screenWidth / 2 + 10;
                int baseY = screenHeight - 60;
                
                boolean isShaking = clientEnergy <= clientMaxEnergy * 0.1f && clientEnergy > 0;
                
                for (int i = 0; i < totalIcons; i++) {
                    int iconX = startX + (i * 7);
                    int iconY = baseY + 10;
                    
                    if (isShaking && mc.level != null) {
                        iconY += mc.level.random.nextInt(3) - 1;
                    }
                    
                    guiGraphics.fill(iconX, iconY, iconX + 6, iconY + 6, 0xFF444444);
                    
                    if (i < Math.floor(filledIcons)) {
                        guiGraphics.fill(iconX, iconY, iconX + 6, iconY + 6, 0xFFFFAA00);
                    } else if (i == Math.floor(filledIcons) && (filledIcons - i) >= 0.5f) {
                        guiGraphics.fill(iconX, iconY, iconX + 3, iconY + 6, 0xFFFFAA00);
                    }
                }
                
                guiGraphics.drawString(mc.font, "Energy: " + (clientEnergy / 10.0f), startX, baseY - 2, 0xFFFF00, true);
                
                if (questionsAnswered > 0) {
                    int percentage = (int) (((float)questionsCorrect / questionsAnswered) * 100);
                    guiGraphics.drawString(mc.font, "Questions: " + questionsCorrect + "/" + questionsAnswered + " (" + percentage + "%)", startX, baseY - 12, 0xFFFFFF, true);
                }
                
                if (isShaking) {
                    guiGraphics.drawCenteredString(mc.font, "Low Energy!", screenWidth / 2, screenHeight / 2 + 30, 0xFF0000);
                }
            }
        );
    }
}
