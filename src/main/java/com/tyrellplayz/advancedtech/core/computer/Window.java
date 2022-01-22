package com.tyrellplayz.advancedtech.core.computer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.advancedtech.api.content.Content;
import com.tyrellplayz.advancedtech.api.content.dialog.ErrorDialog;
import com.tyrellplayz.advancedtech.api.icon.Icon;
import com.tyrellplayz.advancedtech.api.system.ApplicationSystem;
import com.tyrellplayz.advancedtech.api.system.IWindow;
import com.tyrellplayz.zlib.util.MathsUtil;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a windows within the screen.
 */
public class Window<T extends Content> implements IWindow<T> {

    public static final int TOP_BAR_HEIGHT = 10;
    public static final int BORDER = 2;

    private final T content;
    private ApplicationSystem computer;
    private final UUID id;
    private Icon icon;
    private String title;
    private int screenWidth;
    private int screenHeight;
    private double screenLeft;
    private double screenTop;
    private double fromLeft = 0.0D;
    private double fromTop = 0.0D;
    private int width;
    private int height;
    private boolean mouseOver;
    private boolean focused;
    private boolean dragging;
    private Color exitButtonColour;
    private boolean mouseOverX;
    double mouseLeft;
    double mouseTop;

    public Window(T content) {
        this.exitButtonColour = Color.WHITE;
        this.content = content;
        content.setWindow(this);
        this.id = UUID.randomUUID();
    }

    public final void load(ApplicationSystem computer, int screenWidth, int screenHeight, int screenLeft, int screenTop) {
        if (this.hasBorder()) {
            this.width = 24;
            this.height = 32;
        } else {
            this.width = 20;
            this.height = 20;
        }

        this.computer = computer;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTop = screenTop;
        this.screenLeft = screenLeft;

        try {
            this.content.onLoad();
        } catch (Exception var7) {
            this.onError(var7);
            return;
        }

        this.onPositionChanged((double)screenLeft, (double)screenTop);
        this.setPosition((double)(screenWidth / 2 - this.getWidth() / 2), (double)(screenHeight / 2 - this.getHeight() / 2));
    }

    public void tick() {
        try {
            this.content.onTick();
        } catch (Exception var2) {
            this.onError(var2);
        }
    }

    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        try {
            if (this.hasBorder()) {
                RenderUtil.drawRectWithColour(stack,this.getX(), this.getY(), this.width, 10, MainTheme.WINDOW_BORDER_COLOUR);
                RenderUtil.drawRectWithColour(stack,this.getX(), this.getY(), 2, this.height, MainTheme.WINDOW_BORDER_COLOUR);
                RenderUtil.drawRectWithColour(stack,this.getX() + (double)(this.width - 1) - 1.0D, this.getY(), 2, this.height, MainTheme.WINDOW_BORDER_COLOUR);
                RenderUtil.drawRectWithColour(stack,this.getX(), this.getY() + (double)(this.height - 1) - 1.0D, this.width, 2, MainTheme.WINDOW_BORDER_COLOUR);
                if (this.hasIcon() && this.iconIsShown()) {
                    this.icon.render(stack, this.getX() + 2.0D, this.getY() + 1.0D, 7, 7);
                }

                Color titleColour = Color.WHITE;
                if (!this.mouseOverX) {
                    this.exitButtonColour = Color.WHITE;
                }

                if (!this.focused) {
                    titleColour = Color.LIGHT_GRAY;
                    this.exitButtonColour = Color.LIGHT_GRAY;
                }

                if (this.hasTitle()) {
                    double textX = this.getX() + 3.0D;
                    if(this.hasIcon() && this.iconIsShown()) {
                        textX += 9.0D;
                    }
                    RenderUtil.drawText(stack, this.getTitle(), textX, this.getY() + 1.0D, titleColour);
                }

                RenderUtil.drawText(stack, "X", this.getX() + (double)this.width - 7.0D, this.getY() + 1.0D, this.exitButtonColour);
                RenderUtil.drawRectWithColour(stack,this.getX() + 2.0D, this.getY() + 10.0D, this.width - 4, this.height - 12, Color.LIGHT_GRAY);
            } else {
                RenderUtil.drawRectWithColour(stack,this.getX(), this.getY(), this.width, this.height, Color.LIGHT_GRAY);
            }

            this.content.render(stack, mouseX, mouseY, partialTicks);
        } catch (Exception e) {
            this.onError(e);
        }

    }

    public void onPositionChanged(double screenLeft, double screenTop) {
        this.screenLeft = screenLeft;
        this.screenTop = screenTop;

        try {
            this.content.onWindowMoved(this.getX(), this.getY());
        } catch (Exception var6) {
            this.onError(var6);
        }

    }

    public void open(ApplicationSystem computer) {
        this.computer.openWindow(this);
    }

    public void close() {
        this.computer.closeWindow(this);
    }

    public boolean isOpen() {
        return this.computer.isWindowOpen(this);
    }

    public double getX() {
        return this.screenLeft + this.fromLeft;
    }

    public double getContentX() {
        return this.hasBorder() ? this.getX() + 2.0D : this.getX();
    }

    public double getY() {
        return this.screenTop + this.fromTop;
    }

    public double getContentY() {
        return this.hasBorder() ? this.getY() + 10.0D : this.getY();
    }

    public double getFromLeft() {
        return this.fromLeft;
    }

    public double getFromTop() {
        return this.fromTop;
    }

    public void setPosition(double fromLeft, double fromTop) {
        fromLeft = MathsUtil.min(fromLeft, 0.0D);
        fromTop = MathsUtil.min(fromTop, 0.0D);
        double tempBottomXPos = this.screenLeft + fromLeft + (double)this.getWidth();
        double tempBottomYPos = this.screenTop + fromTop + (double)this.getHeight();
        if (tempBottomXPos > this.screenLeft + (double)this.screenWidth) {
            tempBottomXPos = (double)this.screenWidth;
            fromLeft = tempBottomXPos - (double)this.getWidth();
        }

        if (tempBottomYPos > this.screenTop + (double)this.screenHeight) {
            tempBottomYPos = (double)this.screenHeight;
            fromTop = tempBottomYPos - (double)this.getHeight();
        }

        this.fromLeft = fromLeft;
        this.fromTop = fromTop;
        this.onPositionChanged(this.screenLeft, this.screenTop);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setSize(int width, int height) {
        if (this.hasBorder()) {
            width += 4;
            height += 12;
        }

        this.width = Mth.clamp(width, 20, this.screenWidth);
        this.height = Mth.clamp(height, 20, this.screenHeight);
    }

    @Nonnull
    public ApplicationSystem getComputer() {
        return this.computer;
    }

    @Nonnull
    public T getContent() {
        return this.content;
    }

    @Nullable
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public Icon getIcon() {
        return this.icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void setFocus(boolean focused) {
        this.focused = focused;
        this.onFocusChanged(!focused);
    }

    public boolean hasBorder() {
        return this.content.hasBorder();
    }

    public void setBorder(boolean border) {
        this.content.setBorder(border);
    }

    public boolean isMovable() {
        return this.content.isMovable();
    }

    public void setMovable(boolean movable) {
        this.content.setMovable(movable);
    }

    public boolean iconIsShown() {
        return this.content.iconIsShown();
    }

    public void setShowIcon(boolean showIcon) {
        this.content.setShowIcon(showIcon);
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        try {
            if (this.isMouseInsideTopBar(mouseX, mouseY) && this.isMouseInsideX(mouseX, mouseY)) {
                this.close();
            }

            if (this.isMouseInsideContent(mouseX, mouseY)) {
                this.content.onMouseClicked(mouseX, mouseY, mouseButton);
            }
        } catch (Exception var7) {
            this.onError(var7);
        }

    }

    public void onMouseReleased(double mouseX, double mouseY, int mouseButton) {
        this.dragging = false;

        try {
            if (this.isMouseInsideContent(mouseX, mouseY)) {
                this.content.onMouseReleased(mouseX, mouseY, mouseButton);
            }
        } catch (Exception var7) {
            this.onError(var7);
        }

    }

    public void onMouseMoved(double mouseX, double mouseY) {
        try {
            if (this.isMouseInside(mouseX, mouseY)) {
                this.mouseOver = true;
            } else {
                if (this.mouseOver) {
                    this.onMouseLeave();
                }

                this.mouseOver = false;
            }

            if (this.isMouseInsideX(mouseX, mouseY)) {
                this.mouseOverX = true;
                this.exitButtonColour = Color.RED;
            } else {
                this.mouseOverX = false;
                this.exitButtonColour = Color.WHITE;
            }

            if (this.isMouseInsideContent(mouseX, mouseY)) {
                this.content.onMouseMoved(mouseX, mouseY);
            }
        } catch (Exception var6) {
            this.onError(var6);
        }

    }

    public void onMouseLeave() {
    }

    public void onMouseScrolled(double mouseX, double mouseY, double delta) {
        try {
            if (this.isMouseInsideContent(mouseX, mouseY)) {
                this.content.onMouseScrolled(mouseX, mouseY, delta);
            }
        } catch (Exception var8) {
            this.onError(var8);
        }

    }

    public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double distanceX, double distanceY) {
        try {
            if (this.dragging) {
                double left = this.fromLeft + distanceX;
                double top = this.fromTop + distanceY;
                this.setPosition(left, top);
                return;
            }

            if (this.isMovable() && this.isMouseInsideTopBar(mouseX, mouseY)) {
                this.dragging = true;
            }

            if (this.isMouseInsideContent(mouseX, mouseY)) {
                this.content.onMouseDragged(mouseX, mouseY, mouseButton, distanceX, distanceY);
            }
        } catch (Exception var14) {
            this.onError(var14);
        }

    }

    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            this.content.onKeyPressed(keyCode, scanCode, modifiers);
        } catch (Exception var5) {
            this.onError(var5);
        }

    }

    public void onKeyReleased(int keyCode, int scanCode, int modifiers) {
        try {
            this.content.onKeyReleased(keyCode, scanCode, modifiers);
        } catch (Exception var5) {
            this.onError(var5);
        }

    }

    public void onCharTyped(char character, int modifiers) {
        try {
            this.content.onCharTyped(character, modifiers);
        } catch (Exception var4) {
            this.onError(var4);
        }

    }

    public void onFocusChanged(boolean lostFocus) {
        try {
            this.content.onFocusChanged(lostFocus);
        } catch (Exception var3) {
            this.onError(var3);
        }

    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return RenderUtil.isMouseWithin(mouseX, mouseY, this.getX(), this.getY(), this.width - 1, this.height - 1);
    }

    public boolean isMouseInsideContent(double mouseX, double mouseY) {
        return RenderUtil.isMouseWithin(mouseX, mouseY, this.getX() + 2.0D, this.getY() + 10.0D, this.width - 1 - 4, this.height - 1 - -8);
    }

    public boolean isMouseInsideTopBar(double mouseX, double mouseY) {
        return RenderUtil.isMouseWithin(mouseX, mouseY, this.getX(), this.getY(), this.width - 2, 10);
    }

    public boolean isMouseInsideX(double mouseX, double mouseY) {
        return RenderUtil.isMouseWithin(mouseX, mouseY, this.getX() + (double)this.width - 7.0D, this.getY() + 1.0D, 5, 7);
    }

    public void onError(Exception e) {
        ErrorDialog dialog = new ErrorDialog(this.getTitle(), "There was an unexpected error.\nClosing application.");
        this.computer.openDialog(dialog);
        dialog.getWindow().setPosition(this.fromLeft, this.fromTop);
        e.printStackTrace();
        this.close();
    }

    public UUID getId() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Window<?> window = (Window)o;
            return this.id.equals(window.id);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id});
    }

    public String toString() {
        return "Window{content=" + this.content + ", id=" + this.id + '}';
    }

}
