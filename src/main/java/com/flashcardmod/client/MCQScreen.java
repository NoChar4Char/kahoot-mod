package com.flashcardmod.client;

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
    private long answerTime = 0;
    private Button nextButton;

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
        int calculatedHeight = 24;
        for (int i = 0; i < answers.size(); i++) {
            String label = answers.get(i);
            calculatedHeight = Math.max(calculatedHeight, this.font.wordWrapHeight(net.minecraft.network.chat.FormattedText.of(label), btnWidth - 10) + 8);
        }
        int btnHeight = calculatedHeight;
        
        int leaveAndQuitY = this.height / 2 + btnHeight + (btnHeight / 2) + 20;
        if (com.flashcardmod.client.EnergyHUD.clientEnergy > 0) {
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
            int y = (this.height / 2) + (i / 2 == 0 ? -btnHeight/2 - 5 : btnHeight/2 + 5);
            
            final int index = i;
            this.addRenderableWidget(Button.builder(Component.empty(), btn -> {
                selectAnswer(index);
            }).bounds(x, y, btnWidth, btnHeight).build());
        }
    }

    private void selectAnswer(int index) {
        if (this.answerStatus != 0) return;
        this.answerTime = net.minecraft.Util.getMillis();
        if (index == this.correctIndex) {
            com.flashcardmod.network.NetworkHandler.CHANNEL.send(new com.flashcardmod.network.RewardEnergyPacket(true), net.minecraftforge.network.PacketDistributor.SERVER.noArg());
            this.answeredCorrectly = true;
            this.answerStatus = 1;
        } else {
            com.flashcardmod.network.NetworkHandler.CHANNEL.send(new com.flashcardmod.network.RewardEnergyPacket(false), net.minecraftforge.network.PacketDistributor.SERVER.noArg());
            this.answerStatus = 2;
        }
        this.clearWidgets();
        this.buildPostAnswerWidgets();
    }

    private void buildPostAnswerWidgets() {
        int btnWidth = 150;
        int calculatedHeight = 24;
        for (int i = 0; i < answers.size(); i++) {
            String label = answers.get(i);
            calculatedHeight = Math.max(calculatedHeight, this.font.wordWrapHeight(net.minecraft.network.chat.FormattedText.of(label), btnWidth - 10) + 8);
        }
        int btnHeight = calculatedHeight;
        
        int centerX = this.width / 2;
        int y = this.height / 2 + btnHeight + (btnHeight / 2) + 15;
        
        this.nextButton = this.addRenderableWidget(Button.builder(Component.literal("Next Question (Space)"), btn -> {
            if (net.minecraft.Util.getMillis() - this.answerTime < 1000) return;
            com.flashcardmod.QuestionManager.Question q = com.flashcardmod.QuestionManager.getRandomQuestion();
            if (q != null) {
                this.minecraft.setScreen(new MCQScreen(q.text, q.answers, q.correctIndex));
            }
        }).bounds(centerX - 100, y, 200, 20).build());
        this.nextButton.active = false;
        
        y += 25;
        if (this.answeredCorrectly || com.flashcardmod.client.EnergyHUD.clientEnergy > 0) {
            Button leaveBtn = Button.builder(Component.literal("Leave (ESC)"), btn -> {
                if (net.minecraft.Util.getMillis() - this.answerTime < 1000) return;
                this.minecraft.setScreen(null);
            }).bounds(centerX - 100, y, 200, 20).build();
            leaveBtn.active = false;
            this.addRenderableWidget(leaveBtn);
        } else {
            Button quitBtn = Button.builder(Component.literal("Save and Quit to Title"), btn -> {
                if (net.minecraft.Util.getMillis() - this.answerTime < 1000) return;
                if (this.minecraft.level != null) {
                    this.minecraft.level.disconnect();
                }
                this.minecraft.disconnect(new net.minecraft.client.gui.screens.TitleScreen());
            }).bounds(centerX - 100, y, 200, 20).build();
            quitBtn.active = false;
            this.addRenderableWidget(quitBtn);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return (this.answerStatus != 0 && this.answeredCorrectly) || com.flashcardmod.client.EnergyHUD.clientEnergy > 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        
        if (this.answerStatus == 0) {
            if (keyCode >= com.mojang.blaze3d.platform.InputConstants.KEY_1 && keyCode <= com.mojang.blaze3d.platform.InputConstants.KEY_4) {
                int index = keyCode - com.mojang.blaze3d.platform.InputConstants.KEY_1;
                if (index < answers.size()) {
                    selectAnswer(index);
                    return true;
                }
            }
            if (keyCode >= com.mojang.blaze3d.platform.InputConstants.KEY_NUMPAD1 && keyCode <= com.mojang.blaze3d.platform.InputConstants.KEY_NUMPAD4) {
                int index = keyCode - com.mojang.blaze3d.platform.InputConstants.KEY_NUMPAD1;
                if (index < answers.size()) {
                    selectAnswer(index);
                    return true;
                }
            }
        } else {
            if (keyCode == com.mojang.blaze3d.platform.InputConstants.KEY_SPACE) {
                if (net.minecraft.Util.getMillis() - this.answerTime < 1000) return true;
                com.flashcardmod.QuestionManager.Question q = com.flashcardmod.QuestionManager.getRandomQuestion();
                if (q != null) {
                    this.minecraft.setScreen(new MCQScreen(q.text, q.answers, q.correctIndex));
                }
                return true;
            }
            if (keyCode == com.mojang.blaze3d.platform.InputConstants.KEY_ESCAPE) {
                if (net.minecraft.Util.getMillis() - this.answerTime < 1000) return true;
                if (this.answeredCorrectly || com.flashcardmod.client.EnergyHUD.clientEnergy > 0) {
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
        
        int btnWidth = 150;
        int calculatedHeight = 24;
        for (int i = 0; i < answers.size(); i++) {
            String label = answers.get(i);
            calculatedHeight = Math.max(calculatedHeight, this.font.wordWrapHeight(net.minecraft.network.chat.FormattedText.of(label), btnWidth - 10) + 8);
        }
        int btnHeight = calculatedHeight;
        
        List<net.minecraft.util.FormattedCharSequence> qLines = this.font.split(net.minecraft.network.chat.FormattedText.of(this.question), 300);
        int qHeight = qLines.size() * 10;
        int qY = (this.height / 2) - btnHeight/2 - 15 - qHeight;
        
        for (net.minecraft.util.FormattedCharSequence line : qLines) {
            guiGraphics.drawCenteredString(this.font, line, this.width / 2, qY, 0xFFFFFF);
            qY += 10;
        }
        
        if (this.answerStatus == 0) {
            for (int i = 0; i < answers.size(); i++) {
                int x = (this.width / 2) - (btnWidth / 2) + (i % 2 == 0 ? -80 : 80);
                int y = (this.height / 2) + (i / 2 == 0 ? -btnHeight/2 - 5 : btnHeight/2 + 5);
                String label = answers.get(i);
                
                List<net.minecraft.util.FormattedCharSequence> ansLines = this.font.split(net.minecraft.network.chat.FormattedText.of(label), btnWidth - 10);
                int textHeight = ansLines.size() * 10;
                int textY = y + (btnHeight - textHeight) / 2 + 1;
                
                for (net.minecraft.util.FormattedCharSequence line : ansLines) {
                    guiGraphics.drawCenteredString(this.font, line, x + (btnWidth / 2), textY, 0xFFFFFF);
                    textY += 10;
                }
            }
        }
        
        int statusY = (this.height / 2) - btnHeight/2 - 15 - qHeight - 25;
        if (this.answerStatus == 1) {
            guiGraphics.fill(0, statusY - 5, this.width, statusY + 20, 0x5500FF00);
            guiGraphics.drawCenteredString(this.font, "CORRECT!", this.width / 2, statusY + 5, 0x00FF00);
        } else if (this.answerStatus == 2) {
            guiGraphics.fill(0, statusY - 5, this.width, statusY + 20, 0x55FF0000);
            guiGraphics.drawCenteredString(this.font, "INCORRECT!", this.width / 2, statusY + 5, 0xFF0000);
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
        if (this.answerStatus != 0) {
            boolean canClick = (net.minecraft.Util.getMillis() - this.answerTime >= 1000);
            for (net.minecraft.client.gui.components.events.GuiEventListener widget : this.children()) {
                if (widget instanceof Button btn) {
                    btn.active = canClick;
                }
            }
        }
    }
}
