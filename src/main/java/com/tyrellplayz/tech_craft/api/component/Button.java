package com.tyrellplayz.tech_craft.api.component;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.icon.Icon;
import com.tyrellplayz.tech_craft.api.theme.Style;
import com.tyrellplayz.tech_craft.api.util.CustomRender;
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
    private Style<Button> style = new DefaultStyle();
    private CustomRender<Button> customRender;

    public Button(int left, int top, int width) {
        this(left, top, MathsUtil.min(width, 18), 18);
    }

    public Button(int left, int top, int width, int height) {
        super(left, top, MathsUtil.min(width, 18), height);
    }

    public Button(int left, int top, String text) {
        super(left, top, MathsUtil.min(RenderUtil.getTextWidth(text) + 10, 18), DEFAULT_BUTTON_HEIGHT);
        this.text = text;
    }

    public Button(int left, int top, int width, String text) {
        this(left, top, MathsUtil.min(width, 18), DEFAULT_BUTTON_HEIGHT);
    }

    public Button(int left, int top, int width, int height, String text) {
        super(left, top, MathsUtil.min(width, 18), height);
        this.text = text;
    }

    public Button(int left, int top, Icon icon) {
        super(left, top, MathsUtil.min(icon.getWidth() + PADDING, 18), DEFAULT_BUTTON_HEIGHT);
        this.icon = icon;
    }

    public Button(int left, int top, int width, Icon icon) {
        this(left, top, MathsUtil.min(width, 18), DEFAULT_BUTTON_HEIGHT,icon);
    }

    public Button(int left, int top, int width, int height, Icon icon) {
        super(left, top, MathsUtil.min(width, 18), height);
        this.icon = icon;
    }

    public Button(int left, int top, Icon icon, String text) {
        super(left, top, MathsUtil.min(RenderUtil.getTextWidth(text) + 10 + icon.getWidth(), 18), DEFAULT_BUTTON_HEIGHT);
        this.icon = icon;
        this.text = text;
    }

    public Button(int left, int top, int width, Icon icon, String text) {
        this(left, top, MathsUtil.min(width, 18), DEFAULT_BUTTON_HEIGHT,icon,text);
    }

    public Button(int left, int top, int width, int height, Icon icon, String text) {
        super(left, top, MathsUtil.min(width, 18), height);
        this.icon = icon;
        this.text = text;
    }

    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.isVisible()) {
            if (this.customRender != null) {
                this.customRender.render(this,stack);
            } else {
                style.render(this,stack);
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

    public void setCustomRender(CustomRender<Button> customRender) {
        this.customRender = customRender;
    }

    public String getText() {
        return this.text;
    }

    public static class DefaultStyle extends Style<Button> {

        @Override
        public void render(Button button, PoseStack stack) {
            RenderSystem.setShaderTexture(0, BUTTON_TEXTURES);

            byte flag;
            if (button.isEnabled()) {
                if (button.isHovering()) flag = 2;
                else flag = 1;
            } else {
                flag = 0;
            }

            int buttonV = 46 + flag * 20;
            // Top Left Corner
            RenderUtil.drawRectWithDefaultTexture(stack,button.getXPos(), button.getYPos(), 0, buttonV, 2, 2, 2, 2);
            // Top Right Corner
            RenderUtil.drawRectWithDefaultTexture(stack,button.getXPos() + (double)button.getWidth() - 2.0D, button.getYPos(), 198, buttonV, 2, 2, 2, 2);
            // Bottom Right Corner
            RenderUtil.drawRectWithDefaultTexture(stack,button.getXPos() + (double)button.getWidth() - 2.0D, button.getYPos() + (double)button.getHeight() - 2.0D, 198, (buttonV + 20 - 2), 2, 2, 2, 2);
            // Bottom Left Corner
            RenderUtil.drawRectWithDefaultTexture(stack,button.getXPos(), button.getYPos() + (double)button.getHeight() - 2.0D, 0, (buttonV + 20 - 2), 2, 2, 2, 2);

            // Top
            RenderUtil.drawRectWithDefaultTexture(stack,button.getXPos() + 2.0D, button.getYPos(), 2, buttonV, button.getWidth() - 4, 2, 1, 2);
            // Right
            RenderUtil.drawRectWithDefaultTexture(stack,button.getXPos() + (double)button.getWidth() - 2.0D, button.getYPos() + 2.0D, 198, (buttonV + 2), 2, button.getHeight() - 4, 2, 1);
            // Bottom
            RenderUtil.drawRectWithDefaultTexture(stack,button.getXPos() + 2.0D, button.getYPos() + (double)button.getHeight() - 2.0D, 3, (buttonV + 20 - 2), button.getWidth() - 4, 2, 1, 2);
            // Left
            RenderUtil.drawRectWithDefaultTexture(stack,button.getXPos(), button.getYPos() + 2.0D, 0, (buttonV + 2), 2, button.getHeight() - 4, 2, 1);
            // Center
            RenderUtil.drawRectWithDefaultTexture(stack,button.getXPos() + 2.0D, button.getYPos() + 2.0D, 2, (buttonV + 2), button.getWidth() - 4, button.getHeight() - 4, 1, 1);
        }
    }

    public static class FlatStyle extends Style<Button> {

        @Override
        public void render(Button button, PoseStack stack) {
            if(button.isHovering()) {
                RenderUtil.drawRectWithColour(stack,button.getXPos(), button.getYPos(), button.getWidth(), button.getHeight(), new Color(255, 255, 255, 60));
                RenderUtil.drawRectWithColour(stack,button.getXPos(), button.getYPos(), button.getWidth(), 1, Color.WHITE);
                RenderUtil.drawRectWithColour(stack,button.getXPos(), button.getYPos() + (double)button.getHeight(), button.getWidth(), 1, Color.WHITE);
                RenderUtil.drawRectWithColour(stack,button.getXPos(), button.getYPos(), 1, button.getHeight(), Color.WHITE);
                RenderUtil.drawRectWithColour(stack,button.getXPos() + (double)button.getWidth() - 1.0D, button.getYPos(), 1, button.getHeight() + 1, Color.WHITE);
            }
        }
    }

}
