package com.tyrellplayz.tech_craft.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.AdvancedComputing;
import com.tyrellplayz.tech_craft.api.content.Layer;
import com.tyrellplayz.tech_craft.api.system.Tooltip;
import com.tyrellplayz.tech_craft.api.util.Animation;
import com.tyrellplayz.tech_craft.api.util.IScreenEventListener;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.resources.ResourceLocation;

public abstract class Component implements IScreenEventListener {

    protected static final ResourceLocation COMPONENT_TEXTURES = new ResourceLocation(AdvancedComputing.MOD_ID,"textures/gui/components.png");

    // X and Y position of the component.
    protected double xPos, yPos;
    // The position of the component from the left and top of the screen.
    protected int left, top;
    // The width and height of the component.
    protected int width, height;

    protected boolean visible;
    protected boolean enabled;
    protected boolean hovering;

    protected Alignment alignment;
    protected Tooltip tooltip;
    Animation toolTipAnimation = new Animation.Wait(60, (stack, mouseX, mouseY, partialTicks) -> {
        if(this.tooltip != null) this.tooltip.render(stack,mouseX,mouseY,partialTicks);
    });

    public Component(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.visible = true;
        this.enabled = true;
        this.alignment = Alignment.TOP_LEFT;
    }

    public void tick() {
    }

    public abstract void render(PoseStack stack, double mouseX, double mouseY, float partialTicks);

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int code) {
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, int code) {
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY) {
        if (this.isMouseInside(mouseX, mouseY)) {
            this.hovering = true;
        } else {
            if (this.hovering) {
                this.onMouseLeave();
            }
            this.hovering = false;
        }
    }

    @Override
    public void onMouseLeave() {
        this.hovering = false;
    }

    @Override
    public void onMouseScrolled(double mouseX, double mouseY, double delta) {
    }

    @Override
    public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double distanceX, double distanceY) {
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
    }


    @Override
    public void onKeyReleased(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public void onCharTyped(char character, int modifiers) {
    }

    @Override
    public void onFocusChanged(boolean lostFocus) {
        if(lostFocus) {
            this.hovering = false;
        }
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getXPos() {
        return xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isHovering() {
        return hovering;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public boolean isMouseInside(double mouseX, double mouseY) {
        return RenderUtil.isMouseWithin(mouseX,mouseY,this.xPos, this.yPos,this.width,this.height);
    }

    // Component tooltip

    public void renderTooltip(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.isMouseInside(mouseX, mouseY)) {
            this.toolTipAnimation.runTick(stack, mouseX, mouseY, partialTicks);
        } else {
            this.toolTipAnimation.reset();
            if (this.tooltip != null) {
                this.tooltip.reset();
            }

        }
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public void setTooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
        this.toolTipAnimation.reset();
    }

    /**
     * Updates the position of the component
     * @param xPos The xPos of the window.
     * @param yPos The yPos of the window
     */
    public void updatePos(double xPos, double yPos, Layer layer) {
        if (this.alignment == Component.Alignment.TOP_LEFT) {
            this.xPos = xPos + (double)this.getLeft();
            this.yPos = yPos + (double)this.getTop();
        } else if (this.alignment == Component.Alignment.TOP_RIGHT) {
            this.xPos = xPos + (double)layer.getWidth() - (double)this.getWidth() - (double)this.getLeft();
            this.yPos = yPos + (double)this.getTop();
        } else if (this.alignment == Component.Alignment.BOTTOM_LEFT) {
            this.xPos = xPos + (double)this.getLeft();
            this.yPos = yPos + (double)layer.getHeight() - (double)this.getHeight() - (double)this.getTop();
        } else if (this.alignment == Component.Alignment.BOTTOM_RIGHT) {
            this.xPos = xPos + (double)layer.getWidth() - (double)this.getWidth() - (double)this.getLeft();
            this.yPos = yPos + (double)layer.getHeight() - (double)this.getHeight() - (double)this.getTop();
        }
    }

    public enum Alignment {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
    }

}
