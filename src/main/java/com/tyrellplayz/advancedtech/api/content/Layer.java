package com.tyrellplayz.advancedtech.api.content;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.advancedtech.api.component.Component;
import com.tyrellplayz.advancedtech.api.util.IRenderable;
import com.tyrellplayz.advancedtech.api.util.IScreenEventListener;
import com.tyrellplayz.zlib.util.RenderUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Layer implements IScreenEventListener, IRenderable {

    private static final int AUTO_PADDING = 2;

    private final Content content;

    private double x, y;
    private int width, height;
    private boolean mouseOver;

    private final ArrayList<Component> components;

    public Layer(Content content) {
        this(content,100, 100);
    }

    public Layer(Content content, int width, int height) {
        this.components = new ArrayList<>();
        this.content = content;
        this.width = width;
        this.height = height;
    }

    public void tick() {
        this.getComponents().forEach(Component::tick);
    }

    @Override
    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        this.getComponents().forEach(component -> component.render(stack,mouseX,mouseY,partialTicks));
        this.getComponents().forEach(component -> component.renderTooltip(stack,mouseX,mouseY,partialTicks));
    }

    public void onLayerChanged(boolean active) {
        if(!active) {
            this.getComponents().forEach(IScreenEventListener::onMouseLeave);
        }
    }

    public void updatePos(double x, double y) {
        this.x = x;
        this.y = y;
        this.getComponents().forEach((component) -> component.updatePos(x, y, this));
    }

    public void addComponent(Component component) {
        if (component != null) {
            this.components.add(component);
            this.autoSizeLayer();
        }
    }

    private void autoSizeLayer() {
        if (this.width == -1 && this.height == -1) {
            double lastX = 0.0D;
            double lastY = 0.0D;

            for (Component component : this.components) {
                double componentEndX = component.getXPos() - this.content.getWindow().getContentX() + (double) component.getWidth();
                double componentEndY = component.getYPos() - this.content.getWindow().getContentY() + (double) component.getHeight();
                if (componentEndX > lastX) {
                    lastX = componentEndX;
                }

                if (componentEndY > lastY) {
                    lastY = componentEndY;
                }
            }

            if (this.width == -1) {
                this.width = (int)(lastX + 2.0D);
            }

            if (this.height == -1) {
                this.height = (int)(lastY + 2.0D);
            }

            this.content.getWindow().setSize(this.width, this.height);
            this.updatePos(this.content.getWindow().getContentX(), this.content.getWindow().getContentY());
        }
    }

    public List<Component> getComponentsInLocation(double mouseX, double mouseY) {
        ArrayList<Component> componentsInLocation = new ArrayList<>();

        for (Component component : this.getComponents()) {
            if (RenderUtil.isMouseWithin(mouseX, mouseY, component.getXPos(), component.getYPos(), component.getWidth(), component.getHeight())) {
                componentsInLocation.add(component);
            }
        }

        return componentsInLocation;
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        List<Component> componentsInLocation = this.getComponentsInLocation(mouseX, mouseY);
        Component topComponent = null;
        if (!componentsInLocation.isEmpty()) {
            topComponent = componentsInLocation.get(componentsInLocation.size() - 1);
            topComponent.onFocusChanged(false);
            topComponent.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        for (Component component : this.getComponents()) {
            if (component != topComponent) {
                component.onFocusChanged(true);
            }
        }
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, int mouseButton) {
        List<Component> componentsInLocation = this.getComponentsInLocation(mouseX, mouseY);
        if (!componentsInLocation.isEmpty()) {
            Component component = componentsInLocation.get(componentsInLocation.size() - 1);
            component.onMouseReleased(mouseX, mouseY, mouseButton);
        }
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

        for (Component component : this.getComponents()) {
            component.onMouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public void onMouseLeave() {

    }

    @Override
    public void onMouseScrolled(double mouseX, double mouseY, double scroll) {
        for (Component component : this.getComponents()) {
            component.onMouseScrolled(mouseX, mouseY, scroll);
        }
    }

    @Override
    public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double distanceX, double distanceY) {
        for (Component component : this.getComponents()) {
            component.onMouseDragged(mouseX, mouseY, mouseButton, distanceX, distanceY);
        }
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        for (Component component : this.getComponents()) {
            component.onKeyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onKeyReleased(int keyCode, int scanCode, int modifiers) {
        for (Component component : this.getComponents()) {
            component.onKeyReleased(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onCharTyped(char character, int modifiers) {
        for (Component component : this.getComponents()) {
            component.onCharTyped(character, modifiers);
        }
    }

    @Override
    public void onFocusChanged(boolean lostFocus) {
        for (Component component : this.getComponents()) {
            component.onFocusChanged(lostFocus);
        }
    }

    @Override
    public boolean isMouseInside(double mouseX, double mouseY) {
        return RenderUtil.isMouseWithin(mouseX, mouseY, this.x, this.y, this.width, this.height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Collection<Component> getComponents() {
        return this.components;
    }

}
