package com.tyrellplayz.advancedtech.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public abstract class ScrollableComponent extends Component {

    private boolean scrolling;
    protected float scrollDistance;
    protected boolean captureMouse = true;
    protected final int border = 4;
    protected final int barWidth = 6;
    private final int barLeft;
    private Color backgroundColour;

    public ScrollableComponent(int left, int top, int width, int height) {
        super(left, top, width, height);
        this.backgroundColour = Color.DARK_GRAY;
        this.barLeft = width - 6;
    }

    protected abstract int getContentHeight();

    protected void drawBackground() {
    }

    protected abstract void drawPanel(PoseStack stack, double entryRight, double relativeY, double mouseX, double mouseY);

    protected void clickPanel(double mouseX, double mouseY, int button) {
    }

    private int getMaxScroll() {
        return this.getContentHeight() - (this.getHeight() - 4);
    }

    private void applyScrollLimits() {
        int max = this.getMaxScroll();
        if (max < 0) {
            max /= 2;
        }

        if (this.scrollDistance < 0.0F) {
            this.scrollDistance = 0.0F;
        }

        if (this.scrollDistance > (float)max) {
            this.scrollDistance = (float)max;
        }

    }

    public void onMouseScrolled(double mouseX, double mouseY, double scroll) {
        if (this.isMouseInside(mouseX, mouseY) && scroll != 0.0D) {
            this.scrollDistance = (float)((double)this.scrollDistance + -scroll * (double)this.getScrollAmount());
            this.applyScrollLimits();
        }

    }

    protected int getScrollAmount() {
        return 20;
    }

    public void onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.scrolling = mouseButton == 0 && this.isMouseInScrollBar(mouseX, mouseY);
        if (!this.scrolling) {
            int mouseListY = (int)mouseY - (int)this.getYPos() - this.getContentHeight() + (int)this.scrollDistance - 4;
            if (mouseX >= this.getXPos() && mouseX <= this.getXPos() + (double)this.getWidth() && mouseListY < 0) {
                this.clickPanel(mouseX - this.getXPos(), mouseY - this.getYPos() + (double)((int)this.scrollDistance) - 4.0D, mouseButton);
            }

        }
    }

    public void onMouseReleased(double mouseX, double mouseY, int mouseButton) {
        this.scrolling = false;
    }

    private int getBarHeight() {
        int barHeight = this.getHeight() * this.getHeight() / this.getContentHeight();
        if (barHeight < 32) {
            barHeight = 32;
        }

        if (barHeight > this.getHeight() - 8) {
            barHeight = this.getHeight() - 8;
        }

        return barHeight;
    }

    public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double distanceX, double distanceY) {
        if (this.scrolling) {
            int maxScroll = this.getHeight() - this.getBarHeight();
            double moved = distanceY / (double)maxScroll;
            this.scrollDistance = (float)((double)this.scrollDistance + (double)this.getMaxScroll() * moved);
            this.applyScrollLimits();
        }

    }

    public boolean isScrollbarEnabled() {
        int extraHeight = this.getContentHeight() + 4 - this.getHeight();
        return extraHeight > 0;
    }

    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        this.drawBackground();
        Minecraft client = Minecraft.getInstance();
        double scale = client.getWindow().getGuiScale();
        GL11.glEnable(3089);
        GL11.glScissor((int)(this.getXPos() * scale), (int)((double)client.getWindow().getHeight() - (this.getYPos() + (double)this.getHeight() - 1.0D) * scale), (int)((double)(this.getWidth() - 1) * scale), (int)((double)(this.getHeight() - 1) * scale));

        // Box Background
        RenderUtil.drawRectWithColour(stack,this.getXPos(), this.getYPos(), this.getWidth(), this.getHeight(), this.backgroundColour.darker().darker());
        // Box Foreground
        RenderUtil.drawRectWithColour(stack,this.getXPos() + 1.0D, this.getYPos() + 1.0D, this.getWidth() - 1, this.getHeight() - 1, this.backgroundColour);

        double baseY = this.getYPos() + 4.0D - (double)((int)this.scrollDistance);
        this.drawPanel(stack, this.getXPos() + (double)this.getWidth(), baseY, mouseX, mouseY);
        RenderSystem.disableDepthTest();
        int extraHeight = this.getContentHeight() + 4 - this.getHeight();
        if (extraHeight > 0) {
            int barHeight = this.getBarHeight();
            int barTop = (int)this.scrollDistance * (this.getHeight() - barHeight) / extraHeight + (int)this.getYPos();
            if (barTop < (int)this.getYPos()) {
                barTop = (int)this.getYPos();
            }

            double barX = this.getXPos() + (double)this.barLeft;
            RenderUtil.drawRectWithColour(stack,barX, this.getYPos(), 6, this.getHeight(), Color.BLACK);
            RenderUtil.drawRectWithColour(stack,barX, barTop, 6, barHeight, new Color(112, 112, 112));
        }

        //RenderSystem.shadeModel(7424);
        //RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        GL11.glDisable(3089);
    }

    public boolean isMouseInScrollBar(double mouseX, double mouseY) {
        return RenderUtil.isMouseWithin(mouseX, mouseY, this.getXPos() + (double)this.barLeft, this.getYPos(), 6, this.getHeight());
    }

}
