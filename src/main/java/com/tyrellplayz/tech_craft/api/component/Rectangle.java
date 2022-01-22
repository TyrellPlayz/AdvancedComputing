package com.tyrellplayz.tech_craft.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.zlib.util.RenderUtil;

import java.awt.*;

public class Rectangle extends Component {

    private Color colour;

    public Rectangle(int left, int top, int width, int height, Color colour) {
        super(left, top, width, height);
        this.colour = colour;
    }

    @Override
    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        RenderUtil.drawRectWithColour(stack,getXPos(),getYPos(),width,height,colour);
    }
}
