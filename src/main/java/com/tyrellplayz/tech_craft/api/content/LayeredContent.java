package com.tyrellplayz.tech_craft.api.content;

import com.tyrellplayz.tech_craft.api.content.dialog.Dialog;

public abstract class LayeredContent extends Content {

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

    public Dialog getActiveDialog() {
        return this.activeDialog;
    }

    public LayeredContent getParentContent() {
        return this.parentContent;
    }

}
