package com.tyrellplayz.advancedtech.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.zlib.util.RenderUtil;

import java.awt.*;

public class Label extends Component {

    private String text;
    private Color colour;

    public Label(int left, int top, String text) {
        this(left, top, text, Color.WHITE);
    }

    public Label(int left, int top, String text, Color colour) {
        super(left, top, RenderUtil.getTextWidth(text) - 2, 7);
        this.text = text;
        this.colour = colour;
    }

    @Override
    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.isVisible()) {
            String[] stringArray = this.text.split("\\n");
            int fontHeight = 9;

            for(int i = 0; i < stringArray.length; ++i) {
                RenderUtil.drawText(stack, stringArray[i], this.getXPos(), this.getYPos() + (fontHeight * i), this.colour);
            }
        }
    }
}
