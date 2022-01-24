package com.tyrellplayz.tech_craft.api.content.application;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.content.Content;
import com.tyrellplayz.tech_craft.api.content.Layer;
import com.tyrellplayz.tech_craft.api.content.LayeredContent;
import com.tyrellplayz.tech_craft.api.system.IFileSystem;
import com.tyrellplayz.tech_craft.api.system.IWindow;
import com.tyrellplayz.tech_craft.api.system.filesystem.Folder;
import com.tyrellplayz.zlib.util.RenderUtil;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class Application extends LayeredContent {

    private ApplicationManifest applicationManifest;
    private boolean mouseOver;
    private ApplicationPreferences preferences;

    private boolean hasPreferences = false;

    public Application() {}

    /**
     * Loads when the application is loaded / opened. Create layers and assign components here.
     */
    @Override
    public void onLoad() {
        if(hasPreferences()) {
            this.preferences.load();
        }
        this.setActiveLayer(new Layer(this,100,100));
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

    public Folder getApplicationFolder() {
        com.tyrellplayz.tech_craft.api.system.filesystem.FileSystem fileSystem = ((IFileSystem)this.getWindow().getComputer()).getFileSystem();
        String path = "Root\\Applications\\" + this.getApplicationManifest().getId().getNamespace() + "\\" + this.getApplicationManifest().getId().getPath();
        if (!fileSystem.containsFolder(path)) {
            try {
                fileSystem.createFolder(path);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return fileSystem.getFolder(path);
    }

    public ApplicationManifest getApplicationManifest() {
        return applicationManifest;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Application application) {
            return this.applicationManifest.equals(application.applicationManifest);
        }
        return false;
    }

    public final void setWindow(IWindow<? extends Content> window) {
        super.setWindow(window);
        this.getWindow().setTitle(this.applicationManifest.getName());
        this.getWindow().setIcon(this.applicationManifest.getIcon());
    }

    @Nullable
    public ApplicationPreferences getPreferences() {
        return preferences;
    }

    public boolean hasPreferences() {
        return hasPreferences;
    }

    public void setHasPreferences(boolean hasPreferences) {
        if (hasPreferences) preferences = new ApplicationPreferences(this);
        this.hasPreferences = hasPreferences;
    }
}
