package com.tyrellplayz.tech_craft.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.content.Layer;

import java.util.ArrayList;
import java.util.List;

public class Tab extends Component {

    private List<Layer> tabList = new ArrayList<>();
    private Layer activeTab;

    public Tab(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    public void addTab(Layer layer) {
        this.tabList.add(layer);
    }

    public void setActiveTab(int num) {
        this.activeTab = tabList.get(num);
    }

    @Override
    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if(activeTab != null) {
            this.activeTab.render(stack,mouseX,mouseY,partialTicks);
        }
    }

    @Override
    public void tick() {
        if(activeTab != null) {
            this.activeTab.tick();
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int code) {
        if(activeTab != null) {
            this.activeTab.onMouseClicked(mouseX,mouseY,code);
        }
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, int code) {
        if(activeTab != null) {
            this.activeTab.onMouseReleased(mouseX,mouseY,code);
        }
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY) {
        if(activeTab != null) {
            this.activeTab.onMouseMoved(mouseX,mouseY);
        }
    }

    @Override
    public void onMouseLeave() {
        if(activeTab != null) {
            this.activeTab.onMouseLeave();
        }
    }

    @Override
    public void onMouseScrolled(double mouseX, double mouseY, double delta) {
        if(activeTab != null) {
            this.activeTab.onMouseScrolled(mouseX,mouseY,delta);
        }
    }

    @Override
    public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double distanceX, double distanceY) {
        if(activeTab != null) {
            this.activeTab.onMouseDragged(mouseX,mouseY,mouseButton,distanceX,distanceY);
        }
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if(activeTab != null) {
            this.activeTab.onKeyPressed(keyCode,scanCode,modifiers);
        }
    }

    @Override
    public void onKeyReleased(int keyCode, int scanCode, int modifiers) {
        if(activeTab != null) {
            this.activeTab.onKeyReleased(keyCode,scanCode,modifiers);
        }
    }

    @Override
    public void onCharTyped(char character, int modifiers) {
        if(activeTab != null) {
            this.activeTab.onCharTyped(character,modifiers);
        }
    }

    @Override
    public void onFocusChanged(boolean lostFocus) {
        if(activeTab != null) {
            this.activeTab.onFocusChanged(lostFocus);
        }
    }

    @Override
    public void updatePos(double xPos, double yPos, Layer layer) {
        super.updatePos(xPos, yPos, layer);
        if(this.activeTab != null) {
            this.activeTab.updatePos(this.xPos,this.yPos);
        }
    }
}
