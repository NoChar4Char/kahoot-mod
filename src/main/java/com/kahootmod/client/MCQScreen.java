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
    private boolean answeredCorrectly = false;
    private int answerStatus = 0;

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
        
        int leaveAndQuitY = this.height / 2 + 65;
        if (com.kahootmod.client.EnergyHUD.clientEnergy > 0) {
            this.addRenderableWidget(Button.builder(Component.literal("Leave Menu (ESC)"), btn -> {
                this.minecraft.setScreen(null);
            }).bounds(this.width / 2 - 75, leaveAndQuitY, 150, 20).build());
        } else {
            this.addRenderableWidget(Button.builder(Component.literal("Save and Quit to Title"), btn -> {
                if (this.minecraft.level != null) {
                    this.minecraft.level.disconnect();
                }
                this.minecraft.disconnect(new net.minecraft.client.gui.screens.TitleScreen());
            }).bounds(this.width / 2 - 75, leaveAndQuitY, 150, 20).build());
        }

        for (int i = 0; i < answers.size(); i++) {
            int x = (this.width / 2) - (btnWidth / 2) + (i % 2 == 0 ? -80 : 80);
            int y = (this.height / 2) + (i / 2 == 0 ? -15 : 15);
            
            final int index = i;
            this.addRenderableWidget(Button.builder(Component.literal(answers.get(i)), btn -> {
                if (index == this.correctIndex) {
                    com.kahootmod.network.NetworkHandler.CHANNEL.send(new com.kahootmod.network.RewardEnergyPacket(20), net.minecraftforge.network.PacketDistributor.SERVER.noArg());
                    this.answeredCorrectly = true;
                    this.answerStatus = 1;
                } else {
                    this.answerStatus = 2;
                }
                this.clearWidgets();
                this.buildPostAnswerWidgets();
            }).bounds(x, y, btnWidth, btnHeight).build());
        }
    }

    private void buildPostAnswerWidgets() {
        int centerX = this.width / 2;
        int y = this.height / 2 + 10;
        
        this.addRenderableWidget(Button.builder(Component.literal("Next Question (Space)"), btn -> {
            com.kahootmod.QuestionManager.Question q = com.kahootmod.QuestionManager.getRandomQuestion();
            if (q != null) {
                this.minecraft.setScreen(new MCQScreen(q.text, q.answers, q.correctIndex));
            }
        }).bounds(centerX - 100, y, 200, 20).build());
        
        y += 25;
        if (this.answeredCorrectly || com.kahootmod.client.EnergyHUD.clientEnergy > 0) {
            Button leaveBtn = Button.builder(Component.literal("Leave (ESC)"), btn -> {
                this.minecraft.setScreen(null);
            }).bounds(centerX - 100, y, 200, 20).build();
            this.addRenderableWidget(leaveBtn);
        } else {
            this.addRenderableWidget(Button.builder(Component.literal("Save and Quit to Title"), btn -> {
                if (this.minecraft.level != null) {
                    this.minecraft.level.disconnect();
                }
                this.minecraft.disconnect(new net.minecraft.client.gui.screens.TitleScreen());
            }).bounds(centerX - 100, y, 200, 20).build());
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return (this.answerStatus != 0 && this.answeredCorrectly) || com.kahootmod.client.EnergyHUD.clientEnergy > 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.answerStatus != 0) {
            if (keyCode == com.mojang.blaze3d.platform.InputConstants.KEY_SPACE) {
                com.kahootmod.QuestionManager.Question q = com.kahootmod.QuestionManager.getRandomQuestion();
                if (q != null) {
                    this.minecraft.setScreen(new MCQScreen(q.text, q.answers, q.correctIndex));
                }
                return true;
            }
            if (keyCode == com.mojang.blaze3d.platform.InputConstants.KEY_ESCAPE) {
                if (this.answeredCorrectly || com.kahootmod.client.EnergyHUD.clientEnergy > 0) {
                    this.minecraft.setScreen(null);
                }
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        guiGraphics.drawCenteredString(this.font, this.question, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        
        if (this.answerStatus == 1) {
            guiGraphics.fill(0, this.height / 2 - 80, this.width, this.height / 2, 0x5500FF00);
            guiGraphics.drawCenteredString(this.font, "CORRECT!", this.width / 2, this.height / 2 - 70, 0x00FF00);
        } else if (this.answerStatus == 2) {
            guiGraphics.fill(0, this.height / 2 - 80, this.width, this.height / 2, 0x55FF0000);
            guiGraphics.drawCenteredString(this.font, "INCORRECT!", this.width / 2, this.height / 2 - 70, 0xFF0000);
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.minecraft.player != null && !this.minecraft.player.isAlive()) {
            this.minecraft.setScreen(null);
        }
    }
}
