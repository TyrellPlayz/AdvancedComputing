package com.tyrellplayz.advancedtech.api.component;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.advancedtech.api.icon.Icon;
import com.tyrellplayz.advancedtech.api.util.CustomRender;
import com.tyrellplayz.zlib.util.ClickListener;
import com.tyrellplayz.zlib.util.MathsUtil;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class Button extends Component {

    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
    public static final int DEFAULT_BUTTON_HEIGHT = 18;
    public static final int PADDING = 5;
    private String text;
    private Icon icon;
    private ClickListener clickListener;
    private CustomRender customRender;

    public Button(int left, int top, int width) {
        super(left, top, MathsUtil.min(width, 18), 18);
    }

    public Button(int left, int top, String text) {
        super(left, top, MathsUtil.min(RenderUtil.getTextWidth(text) + 10, 18), 18);
        this.text = text;
    }

    public Button(int left, int top, int width, String text) {
        super(left, top, MathsUtil.min(width, 18), 18);
        this.text = text;
    }

    public Button(int left, int top, Icon icon) {
        super(left, top, MathsUtil.min(icon.getWidth() + 5, 18), 18);
        this.icon = icon;
    }

    public Button(int left, int top, int width, Icon icon) {
        super(left, top, MathsUtil.min(width, 18), 18);
        this.icon = icon;
    }

    public Button(int left, int top, Icon icon, String text) {
        super(left, top, MathsUtil.min(RenderUtil.getTextWidth(text) + 10 + icon.getWidth(), 18), 18);
        this.icon = icon;
        this.text = text;
    }

    public Button(int left, int top, int width, Icon icon, String text) {
        super(left, top, MathsUtil.min(width, 18), 18);
        this.icon = icon;
        this.text = text;
    }

    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.isVisible()) {
            RenderSystem.setShaderTexture(0, BUTTON_TEXTURES);
            byte flag;
            if (this.isEnabled()) {
                if (this.isHovering()) {
                    flag = 2;
                } else {
                    flag = 1;
                }
            } else {
                flag = 0;
            }

            int buttonV = 46 + flag * 20;
            if (this.customRender != null) {
                this.customRender.render(stack, this.getXPos(), this.getYPos(), this.getWidth(), this.getHeight(), flag);
            } else {
                // Top Left Corner
                RenderUtil.drawRectWithDefaultTexture(stack,this.getXPos(), this.getYPos(), 0, buttonV, 2, 2, 2, 2);
                // Top Right Corner
                RenderUtil.drawRectWithDefaultTexture(stack,this.getXPos() + (double)this.getWidth() - 2.0D, this.getYPos(), 198, buttonV, 2, 2, 2, 2);
                // Bottom Right Corner
                RenderUtil.drawRectWithDefaultTexture(stack,this.getXPos() + (double)this.getWidth() - 2.0D, this.getYPos() + (double)this.getHeight() - 2.0D, 198, (buttonV + 20 - 2), 2, 2, 2, 2);
                // Bottom Left Corner
                RenderUtil.drawRectWithDefaultTexture(stack,this.getXPos(), this.getYPos() + (double)this.getHeight() - 2.0D, 0, (buttonV + 20 - 2), 2, 2, 2, 2);

                // Top
                RenderUtil.drawRectWithDefaultTexture(stack,this.getXPos() + 2.0D, this.getYPos(), 2, buttonV, this.getWidth() - 4, 2, 1, 2);
                // Right
                RenderUtil.drawRectWithDefaultTexture(stack,this.getXPos() + (double)this.getWidth() - 2.0D, this.getYPos() + 2.0D, 198, (buttonV + 2), 2, this.getHeight() - 4, 2, 1);
                // Bottom
                RenderUtil.drawRectWithDefaultTexture(stack,this.getXPos() + 2.0D, this.getYPos() + (double)this.getHeight() - 2.0D, 3, (buttonV + 20 - 2), this.getWidth() - 4, 2, 1, 2);
                // Left
                RenderUtil.drawRectWithDefaultTexture(stack,this.getXPos(), this.getYPos() + 2.0D, 0, (buttonV + 2), 2, this.getHeight() - 4, 2, 1);
                // Center
                RenderUtil.drawRectWithDefaultTexture(stack,this.getXPos() + 2.0D, this.getYPos() + 2.0D, 2, (buttonV + 2), this.getWidth() - 4, this.getHeight() - 4, 1, 1);
            }

            if (!Strings.isNullOrEmpty(this.text)) {
                double x = this.getXPos() + 5.0D;
                if (this.icon != null) {
                    x += this.icon.getWidth();
                }

                RenderUtil.drawText(stack, this.text, (float)x, (float)this.getYPos() + 5.0F, Color.WHITE);
            }

            if (this.icon != null) {
                this.icon.render(stack, this.getXPos() + 3.0D, this.getYPos() + 3.0D, 12, 12);
            }
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int code) {
        if (this.isHovering() && this.isVisible() && this.isEnabled() && this.clickListener != null) {
            this.clickListener.onClick(code);
        }
    }

    public ClickListener getClickListener() {
        return this.clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setCustomRender(CustomRender customRender) {
        this.customRender = customRender;
    }

    public String getText() {
        return this.text;
    }

}
