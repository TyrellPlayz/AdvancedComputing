package com.tyrellplayz.advancedtech.api.content;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.advancedtech.api.system.IWindow;
import com.tyrellplayz.advancedtech.api.util.IScreenEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Content implements IScreenEventListener {

    private IWindow<? extends Content> window;
    private boolean border = true;
    private boolean movable = true;

    public Content() { }

    public abstract void onLoad();

    public abstract void onTick();

    @OnlyIn(Dist.CLIENT)
    public abstract void render(PoseStack stack, double mouseX, double mouseY, float partialTicks);

    public abstract void onWindowMoved(double var1, double var3);

    public void onWindowClosed() {
    }

    public void setWindow(IWindow<? extends Content> window) {
        this.window = window;
    }

    public IWindow<? extends Content> getWindow() {
        return this.window;
    }

    public boolean hasBorder() {
        return this.border;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    public boolean isMovable() {
        return this.movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

}
