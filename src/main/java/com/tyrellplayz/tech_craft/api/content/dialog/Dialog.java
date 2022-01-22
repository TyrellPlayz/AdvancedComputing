package com.tyrellplayz.tech_craft.api.content.dialog;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.content.Content;
import com.tyrellplayz.tech_craft.api.content.LayeredContent;
import com.tyrellplayz.tech_craft.api.icon.Icon;
import com.tyrellplayz.tech_craft.api.system.IWindow;

public abstract class Dialog extends LayeredContent {

    private final Icon icon;
    private final String title;
    private final String message;

    public Dialog(Icon icon, String title, String message) {
        this.icon = icon;
        this.title = title;
        this.message = message;
    }

    @Override
    public void onTick() {
    }

    @Override
    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        this.getActiveLayer().render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int code) {
        this.getActiveLayer().onMouseClicked(mouseX, mouseY, code);
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY) {
        this.getActiveLayer().onMouseMoved(mouseX, mouseY);
    }

    @Override
    public void onMouseLeave() {
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, int mouseButton) {
        this.getActiveLayer().onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double distanceX, double distanceY) {
        this.getActiveLayer().onMouseDragged(mouseX, mouseY, mouseButton, distanceX, distanceY);
    }

    @Override
    public void onMouseScrolled(double mouseX, double mouseY, double delta) {
        this.getActiveLayer().onMouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        this.getActiveLayer().onKeyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onKeyReleased(int keyCode, int scanCode, int modifiers) {
        this.getActiveLayer().onKeyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void onCharTyped(char character, int modifiers) {
        this.getActiveLayer().onCharTyped(character, modifiers);
    }

    @Override
    public void onFocusChanged(boolean lostFocus) {
        this.getActiveLayer().onFocusChanged(lostFocus);
    }

    @Override
    public void onWindowMoved(double x, double y) {
        this.getActiveLayer().updatePos(this.getWindow().getX() + 2.0D, this.getWindow().getY() + 10.0D);
    }

    @Override
    public boolean isMouseInside(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void onWindowClosed() {
        super.onWindowClosed();
    }

    @Override
    public void setWindow(IWindow<? extends Content> window) {
        super.setWindow(window);
        this.getWindow().setTitle(this.getTitle());
        this.getWindow().setIcon(this.getIcon());
    }

    public Icon getIcon() {
        return this.icon;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessage() {
        return this.message;
    }



}
