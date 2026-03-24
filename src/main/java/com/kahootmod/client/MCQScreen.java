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
    private long freezeEndTime = 0;
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
        
        for (int i = 0; i < answers.size(); i++) {
            int x = (this.width / 2) - (btnWidth / 2) + (i % 2 == 0 ? -80 : 80);
            int y = (this.height / 2) + (i / 2 == 0 ? -10 : 20);
            
            final int index = i;
            this.addRenderableWidget(Button.builder(Component.literal(answers.get(i)), btn -> {
                if (this.freezeEndTime > 0) return;
                if (index == this.correctIndex) {
                    com.kahootmod.network.NetworkHandler.CHANNEL.send(new com.kahootmod.network.RewardEnergyPacket(20), net.minecraftforge.network.PacketDistributor.SERVER.noArg());
                    this.answeredCorrectly = true;
                    this.answerStatus = 1;
                } else {
                    this.answerStatus = 2;
                }
                this.freezeEndTime = net.minecraft.Util.getMillis() + 1500;
            }).bounds(x, y, btnWidth, btnHeight).build());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.freezeEndTime > 0 && net.minecraft.Util.getMillis() > this.freezeEndTime) {
            if (this.answerStatus == 1) {
                this.minecraft.setScreen(null);
            } else if (this.answerStatus == 2) {
                com.kahootmod.QuestionManager.Question q = com.kahootmod.QuestionManager.getRandomQuestion();
                if (q != null) {
                    this.minecraft.setScreen(new MCQScreen(q.text, q.answers, q.correctIndex));
                }
            }
            return;
        }

        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        guiGraphics.drawCenteredString(this.font, this.question, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        
        if (this.answerStatus == 1) {
            guiGraphics.fill(0, this.height / 2 - 80, this.width, this.height / 2 + 80, 0x5500FF00);
            guiGraphics.drawCenteredString(this.font, "CORRECT!", this.width / 2, this.height / 2 - 70, 0x00FF00);
        } else if (this.answerStatus == 2) {
            guiGraphics.fill(0, this.height / 2 - 80, this.width, this.height / 2 + 80, 0x55FF0000);
            guiGraphics.drawCenteredString(this.font, "INCORRECT!", this.width / 2, this.height / 2 - 70, 0xFF0000);
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
