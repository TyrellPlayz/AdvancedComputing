package com.tyrellplayz.tech_craft.api.system;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.util.IRenderable;
import com.tyrellplayz.tech_craft.api.util.Util;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.Mth;

import java.awt.*;

public class Tooltip implements IRenderable {

    private final Font font;
    private double xPos = -1.0D;
    private double yPos = -1.0D;
    private final int width;
    private final int height;
    private final String[] text;
    private final int maxWidth;
    private boolean moveWithMouse;
    private Color bgColour;

    public Tooltip(String... text) {
        this.bgColour = Color.DARK_GRAY;
        this.font = Minecraft.getInstance().font;
        this.text = text;
        this.maxWidth = 2147483647;
        this.width = Mth.clamp(Util.longestString(text) + 4, 0, this.maxWidth);
        this.height = text.length * (9 + 2) + 2;
    }

    public Tooltip(int maxWidth, String... text) {
        this.bgColour = Color.DARK_GRAY;
        this.font = Minecraft.getInstance().font;
        this.text = text;
        this.maxWidth = maxWidth;
        this.width = Mth.clamp(Util.longestString(text) + 4, 0, maxWidth);
        this.height = text.length * (9 + 2) + 2;
    }

    @Override
    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.moveWithMouse) {
            this.xPos = mouseX;
            this.yPos = mouseY;
        }else if(this.xPos == -1.0D || this.yPos == -1.0D) {
            this.xPos = mouseX;
            this.yPos = mouseY;
        }

        RenderUtil.drawRectWithColour(stack, this.xPos, this.yPos, this.width, this.height, Color.BLACK);
        RenderUtil.drawRectWithColour(stack,this.xPos + 1.0D, this.yPos + 1.0D, this.width - 2, this.height - 2, this.bgColour);
        double textY = this.yPos + 2.0D;
        for (int i = 0; i < text.length; i++) {
            String s = this.text[i];
            RenderUtil.drawText(stack,s,this.xPos + 2.0D, textY, Color.WHITE);
            textY += (9+2);
        }
    }

    public void setPos(double xPos, double yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void reset() {
        this.xPos = -1.0D;
        this.yPos = -1.0D;
    }

    public String[] getText() {
        return text;
    }

    public boolean getMoveWithMouse() {
        return moveWithMouse;
    }

    public void setMoveWithMouse(boolean moveWithMouse) {
        this.moveWithMouse = moveWithMouse;
    }

    public Color getBgColour() {
        return bgColour;
    }

    public void setBgColour(Color bgColour) {
        this.bgColour = bgColour;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getMaxWidth() {
        return maxWidth;
    }
}
