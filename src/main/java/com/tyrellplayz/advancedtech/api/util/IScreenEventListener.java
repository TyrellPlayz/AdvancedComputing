package com.tyrellplayz.advancedtech.api.util;

public interface IScreenEventListener {

    void onMouseClicked(double mouseX, double mouseY, int mouseButton);

    void onMouseReleased(double mouseX, double mouseY, int mouseButton);

    void onMouseMoved(double mouseX, double mouseY);

    void onMouseLeave();

    void onMouseScrolled(double mouseX, double mouseY, double scroll);

    void onMouseDragged(double mouseX, double mouseY, int mouseButton, double distanceX, double distanceY);

    void onKeyPressed(int keyCode, int scanCode, int modifiers);

    void onKeyReleased(int keyCode, int scanCode, int modifiers);

    void onCharTyped(char character, int modifiers);

    void onFocusChanged(boolean lostFocus);

    boolean isMouseInside(double mouseX, double mouseY);

}
