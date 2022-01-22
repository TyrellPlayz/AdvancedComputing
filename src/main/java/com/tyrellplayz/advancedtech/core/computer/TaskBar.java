package com.tyrellplayz.advancedtech.core.computer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.advancedtech.api.content.application.ApplicationManifest;
import com.tyrellplayz.advancedtech.api.icon.Icon;
import com.tyrellplayz.advancedtech.api.icon.Icons;
import com.tyrellplayz.advancedtech.api.system.ApplicationSystem;
import com.tyrellplayz.zlib.util.ClickListener;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskBar {

    public static final int BAR_HEIGHT = 16;

    private final ClientComputer system;
    private final List<TaskBar.Item> items;
    private double xPos;
    private double yPos;
    private final Color barColour = new Color(64, 64, 64);
    private final Color itemHover = (new Color(64, 64, 64, 200)).brighter();
    private final Color itemFocused = new Color(64, 64, 64, 200);
    private final Color itemNotFocused = new Color(64, 64, 64, 200);

    public TaskBar(ClientComputer system) {
        this.system = system;
        this.items = new ArrayList<>();
        this.items.add((new TaskBar.Item("start_menu", Icons.HOME, TaskBar.ItemType.START_MENU)).setClickListener((mouseButton) -> {
            system.openStartMenu();
        }));

        for (ApplicationManifest applicationManifest : system.getInstalledApplications()) {
            this.items.add(new Item(applicationManifest, system));
        }
    }

    public void init() {
        this.xPos = (double)this.system.getLeft() + 5.0D;
        this.yPos = (double)(this.system.getTop() + this.system.SCREEN_HEIGHT) + 5.0D - 16.0D;
    }

    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRectWithColour(stack, (float)this.xPos, (float)this.yPos, this.system.SCREEN_WIDTH, 16, new Color(64, 64, 64));
        String time = (new SimpleDateFormat("HH:mm")).format(new Date());
        RenderUtil.drawText(stack, time, (float)(this.system.getLeft() + this.system.SCREEN_WIDTH) + 5.0F - 27.0F, (float)this.yPos + 4.0F, Color.WHITE);
        this.drawItems(stack,mouseX, mouseY, partialTicks);
    }

    private void drawItems(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        for(int i = 0; i < this.items.size(); ++i) {
            TaskBar.Item item = this.items.get(i);
            double itemX = this.xPos + (double)(i * 16 + 1);
            double itemY = this.yPos + 1.0D;
            // If mouse is hovering over item then show hover effect
            if (RenderUtil.isMouseWithin(mouseX, mouseY, (int)itemX-1.0D, (int)itemY-1.0D, 15, 15)) {
                this.drawItemHighlight(stack,itemX, itemY);
            }

            if (item.getItemType() == TaskBar.ItemType.APPLICATION) {
                ResourceLocation id = new ResourceLocation(item.getId());
                if (this.system.isApplicationFocused(id)) {
                    RenderUtil.drawRectWithColour(stack,itemX, itemY - 1.0D, 14, 16, new Color(64, 64, 64));
                } else if (this.system.isApplicationOpen(id)) {
                    RenderUtil.drawRectWithColour(stack,itemX + 1.0D, itemY - 1.0D + 15.0D, 12, 1, (new Color(64, 64, 64)).brighter());
                }
            }

            item.getIcon().render(stack, itemX + 1.0D, itemY + 1.0D, 12, 12);
        }

    }

    private void drawItemHighlight(PoseStack stack, double x, double y) {
        RenderUtil.drawRectWithColour(stack,x - 1.0D, y - 1.0D, 16, 16, new Color(255, 255, 255, 50));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int code) {
        for(int i = 0; i < this.items.size(); ++i) {
            TaskBar.Item item = this.items.get(i);
            double itemX = this.xPos + 1.0D + (double)(i * 16 + 1);
            double itemY = this.yPos + 1.0D;
            if (RenderUtil.isMouseWithin((int)mouseX, (int)mouseY, (int)itemX-2.0D, (int)itemY-1.0D, 15, 15)) {
                item.onClick(code);
            }
        }

        return false;
    }

    public enum ItemType {
        START_MENU, SYSTEM, APPLICATION
    }

    public static class Item {
        private final String id;
        private final Icon icon;
        private final TaskBar.ItemType itemType;
        private ClickListener clickListener;

        public Item(String id, Icon icon, TaskBar.ItemType itemType) {
            this.id = id;
            this.icon = icon;
            this.itemType = itemType;
        }

        public Item(ApplicationManifest applicationManifest, ApplicationSystem system) {
            this(applicationManifest.getId().toString(), applicationManifest.getIcon(), TaskBar.ItemType.APPLICATION);
            this.clickListener = (mouseButton) -> {
                system.openApplication(applicationManifest.getId());
            };
        }

        public String getId() {
            return this.id;
        }

        public Icon getIcon() {
            return this.icon;
        }

        public TaskBar.ItemType getItemType() {
            return this.itemType;
        }

        public TaskBar.Item setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }

        public void onClick(int code) {
            if (this.clickListener != null) {
                this.clickListener.onClick(code);
            }

        }
    }

}
