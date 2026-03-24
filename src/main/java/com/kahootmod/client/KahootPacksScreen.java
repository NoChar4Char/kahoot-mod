package com.kahootmod.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.kahootmod.QuestionManager;
import net.minecraft.Util;

@OnlyIn(Dist.CLIENT)
public class KahootPacksScreen extends Screen {
    private String statusMessage = "";

    public KahootPacksScreen() {
        super(Component.literal("Kahoot Packs"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.addRenderableWidget(Button.builder(Component.literal("Open Packs Folder"), btn -> {
            Util.getPlatform().openFile(QuestionManager.PACKS_DIR);
        }).bounds(centerX - 100, centerY - 20, 200, 20).build());
        
        this.addRenderableWidget(Button.builder(Component.literal("Reload Packs"), btn -> {
            QuestionManager.loadQuestions();
            this.statusMessage = "Loaded " + QuestionManager.getQuestionCount() + " questions!";
        }).bounds(centerX - 100, centerY + 10, 200, 20).build());
        
        this.addRenderableWidget(Button.builder(Component.literal("Done"), btn -> {
            this.minecraft.setScreen(null);
        }).bounds(centerX - 100, centerY + 40, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        String header = "Place your .json question kits in the open folder!";
        guiGraphics.drawCenteredString(this.font, header, this.width / 2, this.height / 2 - 60, 0xFFFFFF);
        if (!this.statusMessage.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, this.statusMessage, this.width / 2, this.height / 2 + 80, 0x00FF00);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
