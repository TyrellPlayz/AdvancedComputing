package com.tyrellplayz.tech_craft.api.content;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.content.dialog.Dialog;
import com.tyrellplayz.zlib.util.RenderUtil;

public abstract class LayeredContent extends Content {

    private boolean mouseOver;

    protected Layer activeLayer;
    protected Dialog activeDialog;
    protected LayeredContent parentContent;

    public LayeredContent() {}

    public void onActiveLayerChanged(Layer newActiveLayer) {
    }

    public void setActiveLayer(Layer newActiveLayer) {
        if (newActiveLayer == null) {
            throw new NullPointerException("Can't set active layer to null");
        } else {
            if (this.activeLayer != null) {
                this.activeLayer.onLayerChanged(false);
            }

            this.activeLayer = newActiveLayer;
            this.activeLayer.onLayerChanged(true);
            this.getWindow().setSize(this.activeLayer.getWidth(), this.activeLayer.getHeight());
            this.activeLayer.updatePos(this.getWindow().getContentX(), this.getWindow().getContentY());
            this.onActiveLayerChanged(this.activeLayer);
        }
    }

    public Layer getActiveLayer() {
        return this.activeLayer == null ? new Layer(this) : this.activeLayer;
    }

    public void openDialog(Dialog dialog) {
        if (this.activeDialog == null) {
            if (!(this instanceof Dialog)) {
                this.activeDialog = dialog;
                this.activeDialog.parentContent = this;
                this.getWindow().getComputer().openDialog(this.activeDialog);
                this.activeDialog.getWindow().setPosition(this.getWindow().getFromLeft(), this.getWindow().getFromTop());
            }
        }
    }

    public void onWindowClosed() {
        if (this instanceof Dialog && this.parentContent != null) {
            this.parentContent.activeDialog = null;
        }

        if (this.activeDialog != null) {
            this.activeDialog.getWindow().close();
        }

    }

    @Override
    public void onTick() {
        this.activeLayer.tick();
    }

    @Override
    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        this.activeLayer.render(stack,mouseX,mouseY,partialTicks);
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int code) {
        this.activeLayer.onMouseClicked(mouseX,mouseY,code);
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, int code) {
        this.activeLayer.onMouseReleased(mouseX, mouseY, code);
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY) {
        if (this.isMouseInside(mouseX, mouseY)) {
            this.mouseOver = true;
        } else {
            if (this.mouseOver) {
                this.onMouseLeave();
            }

            this.mouseOver = false;
        }

        this.activeLayer.onMouseMoved(mouseX, mouseY);
    }

    @Override
    public void onMouseLeave() {
    }

    @Override
    public void onMouseDragged(double mouseX, double mouseY, int code, double distanceX, double distanceY) {
        this.activeLayer.onMouseDragged(mouseX, mouseY, code, distanceX, distanceY);
    }

    @Override
    public void onMouseScrolled(double mouseX, double mouseY, double scroll) {
        this.activeLayer.onMouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        this.activeLayer.onKeyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onKeyReleased(int keyCode, int scanCode, int modifiers) {
        this.activeLayer.onKeyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void onCharTyped(char character, int modifiers) {
        this.activeLayer.onCharTyped(character, modifiers);
    }

    @Override
    public void onWindowMoved(double x, double y) {
        this.activeLayer.updatePos(this.getWindow().getContentX(), this.getWindow().getContentY());
    }

    @Override
    public void onFocusChanged(boolean lostFocus) {
        if (this.activeLayer != null) {
            this.activeLayer.onFocusChanged(lostFocus);
        }
    }

    @Override
    public boolean isMouseInside(double mouseX, double mouseY) {
        return RenderUtil.isMouseWithin(mouseX, mouseY, this.getWindow().getX() + 2.0D, this.getWindow().getY() + 10.0D, this.getWindow().getWidth() - 1 - 4, this.getWindow().getHeight() - 1 - -8);
    }

    public Dialog getActiveDialog() {
        return this.activeDialog;
    }

    public LayeredContent getParentContent() {
        return this.parentContent;
    }

}
