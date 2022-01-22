package com.tyrellplayz.advancedtech.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.advancedtech.api.icon.Icon;
import com.tyrellplayz.advancedtech.api.system.Tooltip;
import com.tyrellplayz.zlib.util.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ItemList<T> extends ScrollableComponent {

    protected static final int X_PADDING = 3;
    protected static final int Y_PADDING = 2;
    protected static final int ICON_SIZE = 8;

    protected List<T> items = new ArrayList<>();
    protected int selectedIndex = -1;
    protected int hoverIndex = -1;
    private boolean showToolTip = true;
    protected Function<T, String> getName = Object::toString;
    protected Function<T, Icon> getIcon;
    protected BiFunction<T, Integer,Boolean> onItemSelected;

    public ItemList(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    public void setItems(List<T> items) {
        this.deselect();
        this.items = items;
        this.hoverIndex = -1;
    }

    public void updateItems(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return this.items;
    }

    public void clear() {
        this.deselect();
        this.items.clear();
        this.hoverIndex = -1;
    }

    public T getSelectedItem() {
        if (!this.isItemSelected()) {
            return null;
        } else {
            try {
                return this.items.get(this.selectedIndex);
            } catch (Exception var2) {
                return null;
            }
        }
    }

    public void deselect() {
        this.selectedIndex = -1;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public boolean isItemSelected() {
        return this.selectedIndex != -1;
    }

    public T removeSelectedItem() {
        if (!this.isItemSelected()) {
            return null;
        } else {
            T item = this.items.remove(this.selectedIndex);
            this.selectedIndex = -1;
            return item;
        }
    }

    public void setShowToolTip(boolean showToolTip) {
        this.showToolTip = showToolTip;
    }

    public boolean isShowingToolTip() {
        return this.showToolTip;
    }

    public void setOnItemSelected(BiFunction<T, Integer,Boolean> onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public void setGetName(Function<T, String> getName) {
        this.getName = getName;
    }

    public void setGetIcon(Function<T, Icon> getIcon) {
        this.getIcon = getIcon;
    }

    protected int getContentHeight() {
        int height = this.items.size() * (9 + 4);
        if (height < this.getHeight() - 8) {
            height = this.getHeight() - 8;
        }
        return height;
    }

    protected int getScrollAmount() {
        return 9 + 4;
    }

    protected void drawPanel(PoseStack stack, double entryRight, double relativeY, double mouseX, double mouseY) {
        int fontHeight;
        double y;
        int width;

        // Draws the hover selection box.
        if (this.hoverIndex != -1) {
            fontHeight = 9 + 2;
            y = relativeY - 2.0D + (double)(fontHeight * this.hoverIndex);
            width = this.getWidth() - 1;
            if (this.isScrollbarEnabled()) {
                width = this.getWidth() - 5;
            }

            RenderUtil.drawRectWithColour(stack,this.getXPos()+1, y, width, fontHeight, new Color(255, 255, 255, 60));
            if (this.getTooltip() != null) {
                double toolTipY = relativeY - 2.0D + (double)(fontHeight * this.hoverIndex);
                this.getTooltip().setPos(this.getXPos() + 3.0D, toolTipY - (double)this.getTooltip().getHeight());
            }
        }

        // Draws the selection box
        if (this.getSelectedItem() != null) {
            fontHeight = 9 + 2;
            y = relativeY - 2.0D + (double)(fontHeight * this.selectedIndex);
            width = this.getWidth() - 1;
            if (this.isScrollbarEnabled()) {
                width = this.getWidth() - 6;
            }

            RenderUtil.drawRectWithColour(stack,this.getXPos(), y, width, fontHeight, new Color(255, 255, 255, 60));
            RenderUtil.drawRectWithColour(stack,this.getXPos(), y, width, 1, Color.WHITE);
            RenderUtil.drawRectWithColour(stack,this.getXPos(), y + (double)fontHeight, width, 1, Color.WHITE);
            RenderUtil.drawRectWithColour(stack,this.getXPos(), y, 1, fontHeight, Color.WHITE);
            RenderUtil.drawRectWithColour(stack,this.getXPos() + (double)width - 1.0D, y, 1, fontHeight + 1, Color.WHITE);
        }

        // Renders the items text
        for (T item : this.items) {
            if (item != null) {
                RenderSystem.enableBlend();
                Icon icon = null;
                if (this.getIcon != null) {
                    icon = this.getIcon.apply(item);
                }

                String text = this.getName.apply(item);
                int textWidth = RenderUtil.getTextWidth(text);
                double x = this.getXPos() + 3.0D;
                if (icon != null) {
                    icon.render(stack, this.getXPos() + 3.0D, relativeY, 8, 8);
                    x += 11.0D;
                }

                RenderUtil.drawText(stack, this.getClippedText(text, icon != null), x, relativeY, Color.WHITE);
                RenderSystem.disableBlend();
            }
            relativeY += 9 + 2;
        }

    }

    protected int findLine(double mouseX, double mouseY) {
        double offset = mouseY - this.getYPos() + (double)this.scrollDistance - 2.0D;
        if (offset <= 0.0D) {
            return -1;
        } else {
            int lineIndex = (int)Math.floor(offset / (double)(9 + 2));
            return lineIndex <= this.items.size() - 1 && lineIndex >= 0 ? lineIndex : -1;
        }
    }

    public void onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.isMouseInside(mouseX, mouseY)) {
            if (!this.isMouseInScrollBar(mouseX, mouseY)) {
                this.selectedIndex = this.findLine(mouseX, mouseY);
                if (this.onItemSelected != null) {
                    if(!this.onItemSelected.apply(this.getSelectedItem(), this.selectedIndex)){
                        deselect();
                    }

                }
                return;
            }
            super.onMouseClicked(mouseX, mouseY, mouseButton);
        }

    }

    public void onMouseMoved(double mouseX, double mouseY) {
        super.onMouseMoved(mouseX, mouseY);
        if (!this.isMouseInScrollBar(mouseX, mouseY) && this.isMouseInside(mouseX, mouseY)) {
            this.hoverIndex = this.findLine(mouseX, mouseY);
            if (this.hoverIndex != -1) {
                boolean hasIcon = false;
                T item = this.items.get(this.hoverIndex);
                String name = this.getName.apply(item);
                if (this.getIcon != null && this.getIcon.apply(item) != null) {
                    hasIcon = true;
                }

                if (this.isTextClipped(name, hasIcon) && this.showToolTip) {
                    if (this.getTooltip() != null) {
                        if (!this.getTooltip().getText()[0].equals(name)) {
                            this.setTooltip(new Tooltip(name));
                        }
                    } else {
                        this.setTooltip(new Tooltip(name));
                    }
                } else {
                    this.setTooltip(null);
                }
            } else {
                this.setTooltip(null);
            }
        } else {
            this.hoverIndex = -1;
            this.setTooltip(null);
        }

    }

    private String getClippedText(String text, boolean hasIcon) {
        int textWidth = RenderUtil.getTextWidth(text);
        if (hasIcon) {
            return textWidth > this.getWidth() - 17 ? RenderUtil.clipTextToWidth(text, this.getWidth() - 8 - 8) : text;
        } else {
            return textWidth > this.getWidth() - 3 ? RenderUtil.clipTextToWidth(text, this.getWidth() - 5) : text;
        }
    }

    private boolean isTextClipped(String text, boolean hasIcon) {
        int textWidth = RenderUtil.getTextWidth(text);
        if (hasIcon) {
            return textWidth > this.getWidth() - 17;
        } else {
            return textWidth > this.getWidth() - 3;
        }
    }

    public void renderToolTip(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.isMouseInside(mouseX, mouseY)) {
            this.toolTipAnimation.runTick(stack, mouseX, mouseY, partialTicks);
        } else {
            this.toolTipAnimation.reset();
            if (this.getTooltip() != null) {
                this.getTooltip().reset();
            }

        }
    }



}
