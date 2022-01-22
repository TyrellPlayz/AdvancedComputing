package com.tyrellplayz.tech_craft.core.computer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.component.ItemList;
import com.tyrellplayz.tech_craft.api.content.Content;
import com.tyrellplayz.tech_craft.api.content.Layer;
import com.tyrellplayz.tech_craft.api.content.application.ApplicationManifest;
import com.tyrellplayz.tech_craft.api.icon.Icon;
import com.tyrellplayz.zlib.util.ClickListener;

import java.util.*;

public class StartMenu extends Content {

    private static final char[] ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final ClientComputer computer;
    private Layer currentLayer;

    public StartMenu(ClientComputer computer) {
        this.computer = computer;
        this.setBorder(false);
        this.setMovable(false);
    }

    public void onLoad() {
        this.getWindow().setPosition(0.0D, this.computer.SCREEN_HEIGHT - 16 - 150);
        Layer layer = new Layer(this, 100, 150);
        Collection<ApplicationManifest> installedApplications = this.computer.getInstalledApplications();
        Map<Character, List<AppListItem>> listMap = new HashMap<>();

        for (char c : ALPHABET) {
            listMap.put(c, new ArrayList<>());
        }

        for (Character letter : listMap.keySet()) {
            listMap.get(letter).add(new AppListItem(letter.toString(), null));
            List<ApplicationManifest> remove = new ArrayList<>();

            for (ApplicationManifest installedApplication : installedApplications) {
                String name = installedApplication.getName();
                if (name.charAt(0) == letter) {
                    listMap.get(letter).add((new AppListItem(name, installedApplication.getIcon())).setClickListener((mouseButton) -> {
                        this.computer.openApplication(installedApplication.getId());
                    }));
                    remove.add(installedApplication);
                }
            }

            installedApplications.removeAll(remove);
        }

        List<StartMenu.AppListItem> appListItems = new ArrayList<>();

        for (Character character : listMap.keySet()) {
            List<AppListItem> letterList = listMap.get(character);
            if (letterList.size() > 1) {
                appListItems.addAll(letterList);
            }
        }

        ItemList<AppListItem> applicationList = new ItemList<>(2, 2, 80, 145);
        applicationList.setItems(appListItems);
        applicationList.setGetName(StartMenu.AppListItem::getName);
        applicationList.setGetIcon(StartMenu.AppListItem::getIcon);
        applicationList.setOnItemSelected((appListItem, integer) -> {
            if (appListItem != null) {
                appListItem.onClick(integer);
            }
            return true;
        });
        layer.addComponent(applicationList);
        this.setCurrentLayer(layer);
    }

    public final void setCurrentLayer(Layer currentLayer) {
        if (this.currentLayer != null) {
            this.currentLayer.onLayerChanged(false);
        }

        this.currentLayer = currentLayer;
        currentLayer.onLayerChanged(true);
        this.getWindow().setSize(currentLayer.getWidth(), currentLayer.getHeight());
        currentLayer.updatePos(this.getWindow().getContentX(), this.getWindow().getContentY());
    }

    public void onTick() {
        this.currentLayer.tick();
    }

    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        this.currentLayer.render(stack, mouseX, mouseY, partialTicks);
    }

    public void onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.currentLayer.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    public void onMouseReleased(double mouseX, double mouseY, int mouseButton) {
        this.currentLayer.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    public void onMouseMoved(double mouseX, double mouseY) {
        this.currentLayer.onMouseMoved(mouseX, mouseY);
    }

    public void onMouseLeave() {
    }

    public void onMouseScrolled(double mouseX, double mouseY, double delta) {
        this.currentLayer.onMouseScrolled(mouseX, mouseY, delta);
    }

    public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double distanceX, double distanceY) {
        this.currentLayer.onMouseDragged(mouseX, mouseY, mouseButton, distanceX, distanceY);
    }

    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        this.currentLayer.onKeyPressed(keyCode, scanCode, modifiers);
    }

    public void onKeyReleased(int keyCode, int scanCode, int modifiers) {
        this.currentLayer.onKeyReleased(keyCode, scanCode, modifiers);
    }

    public void onCharTyped(char character, int modifiers) {
        this.currentLayer.onCharTyped(character, modifiers);
    }

    public void onFocusChanged(boolean lostFocus) {
        if (this.currentLayer != null) {
            this.currentLayer.onFocusChanged(lostFocus);
        }

        if (this.getWindow().isOpen() && lostFocus) {
            this.getWindow().close();
        }

    }

    public boolean isMouseInside(double mouseX, double mouseY) {
        return false;
    }

    public void onWindowMoved(double x, double y) {
        if (this.currentLayer != null) {
            this.currentLayer.updatePos(this.getWindow().getContentX(), this.getWindow().getContentY());
        }

    }

    public static class AppListItem {
        private ClickListener clickListener;
        private final String name;
        private final Icon icon;

        public AppListItem(String name, Icon icon) {
            this.name = name;
            this.icon = icon;
        }

        public String getName() {
            return this.name;
        }

        public Icon getIcon() {
            return this.icon;
        }

        public StartMenu.AppListItem setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }

        public void onClick(int mouseButton) {
            if (this.clickListener != null) {
                this.clickListener.onClick(mouseButton);
            }

        }
    }

}
