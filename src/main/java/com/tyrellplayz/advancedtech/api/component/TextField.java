package com.tyrellplayz.advancedtech.api.component;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.advancedtech.api.icon.Icon;
import com.tyrellplayz.advancedtech.api.icon.Icons;
import com.tyrellplayz.zlib.util.RenderUtil;

public class TextField extends TextArea {

    private Icon icon;
    private String error;

    public TextField(int left, int top, int width) {
        super(left, top, width, 16);
        this.setScrollBarVisible(false);
        this.setMaxLines(1);
    }

    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.icon != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtil.drawRectWithColour(stack,this.getXPos(), this.getYPos(), 15, 16, this.backgroundColour.darker().darker());
            RenderUtil.drawRectWithColour(stack,this.getXPos() + 1.0D, this.getYPos() + 1.0D, 15, 15, this.backgroundColour.brighter());
            this.icon.render(stack, this.getXPos() + 3.0D, this.getYPos() + 3.0D);
        }

        super.render(stack, mouseX, mouseY, partialTicks);
        if (!Strings.isNullOrEmpty(this.error)) {
            Icon errorIcon = Icons.ERROR;
            errorIcon.render(stack, this.getXPos() + (double)this.getWidth() - 2.0D - (double)errorIcon.getWidth(), this.getYPos() + 2.0D);
        }

    }

    public void onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.onMouseClicked(mouseX - (double)(this.icon != null ? 15 : 0), mouseY, mouseButton);
    }

    public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double distanceX, double distanceY) {
        super.onMouseDragged(mouseX - (double)(this.icon != null ? 15 : 0), mouseY, mouseButton, distanceX, distanceY);
    }

    public void onMouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.onMouseReleased(mouseX - (double)(this.icon != null ? 15 : 0), mouseY, mouseButton);
    }

    public void setIcon(Icon icon) {
        if (this.icon == null) {
            this.setWidth(this.getWidth() - 15);
        } else if (icon == null) {
            this.setWidth(this.getWidth() + 15);
        }

        this.icon = icon;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void clearError() {
        this.error = "";
    }

}
