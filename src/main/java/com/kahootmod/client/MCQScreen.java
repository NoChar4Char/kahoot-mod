package com.kahootmod.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MCQScreen extends Screen {
    private final String question;
    private final List<String> answers;
    private final int correctIndex;

    public MCQScreen(String question, List<String> answers, int correctIndex) {
        super(Component.literal("MCQ Screen"));
        this.question = question;
        this.answers = answers;
        this.correctIndex = correctIndex;
    }

    @Override
    protected void init() {
        super.init();
        int btnWidth = 150;
        int btnHeight = 20;
        
        for (int i = 0; i < answers.size(); i++) {
            int x = (this.width / 2) - (btnWidth / 2) + (i % 2 == 0 ? -80 : 80);
            int y = (this.height / 2) + (i / 2 == 0 ? -10 : 20);
            
            final int index = i;
            this.addRenderableWidget(Button.builder(Component.literal(answers.get(i)), btn -> {
                if (index == this.correctIndex) {
                    com.kahootmod.network.NetworkHandler.CHANNEL.send(new com.kahootmod.network.RewardEnergyPacket(20), net.minecraftforge.network.PacketDistributor.SERVER.noArg());
                }
                this.minecraft.setScreen(null);
            }).bounds(x, y, btnWidth, btnHeight).build());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.question, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
