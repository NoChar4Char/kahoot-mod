package com.kahootmod.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.ResourceLocation;

@Mod.EventBusSubscriber(modid = com.kahootmod.KahootMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EnergyHUD {
    
    public static int clientEnergy = 100;

    @SubscribeEvent
    public static void registerOverlays(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().add(
            ForgeLayeredDraw.VANILLA_ROOT,
            ResourceLocation.fromNamespaceAndPath(com.kahootmod.KahootMod.MODID, "energy_hud"),
            (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options.hideGui || mc.player == null || mc.player.isSpectator()) return;

                int screenWidth = mc.getWindow().getGuiScaledWidth();
                int screenHeight = mc.getWindow().getGuiScaledHeight();

                int x = screenWidth / 2 + 10;
                int y = screenHeight - 49;
                
                guiGraphics.drawString(mc.font, "Energy: " + (clientEnergy / 10.0f), x, y, 0xFFFF00, true);
            }
        );
    }
}
