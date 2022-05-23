package com.tyrellplayz.tech_craft.core.computer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.content.RightClickMenu;
import com.tyrellplayz.tech_craft.api.content.application.ApplicationManifest;
import com.tyrellplayz.tech_craft.api.icon.Icon;
import com.tyrellplayz.tech_craft.api.icon.Icons;
import com.tyrellplayz.tech_craft.api.system.ApplicationSystem;
import com.tyrellplayz.zlib.util.ClickListener;
import com.tyrellplayz.zlib.util.RenderUtil;
import com.tyrellplayz.zlib.util.Util;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import javax.swing.plaf.ListUI;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TaskBar {

    public static final int BAR_HEIGHT = 16;

    private final ClientComputer system;
    private final List<TaskBar.Item> items;
    private double xPos;
    private double yPos;
    private final Color itemHover = (new Color(64, 64, 64, 200)).brighter();
    private final Color itemFocused = new Color(64, 64, 64, 200);
    private final Color itemNotFocused = new Color(64, 64, 64, 200);

    public TaskBar(ClientComputer system) {
        this.system = system;
        this.items = new ArrayList<>();
        this.items.add(new StartMenuItem(system));

        for (ApplicationManifest applicationManifest : system.getInstalledApplications()) {
            this.items.add(new ApplicationItem(applicationManifest, this));
        }
    }

    public void init() {
        this.xPos = (double)this.system.getLeft() + 5.0D;
        this.yPos = (double)(this.system.getTop() + this.system.SCREEN_HEIGHT) + 5.0D - 16.0D;
    }

    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRectWithColour(stack, (float)this.xPos, (float)this.yPos, this.system.SCREEN_WIDTH, 16, MainTheme.TASK_BAR_COLOUR);
        this.drawTime(stack,mouseX,mouseY);
        this.drawItems(stack,mouseX, mouseY);
    }

    private void drawTime(PoseStack stack, int mouseX, int mouseY) {
        String time;
        if(system.getSystemSettings().isAmPmTime()) {
            if(system.getSystemSettings().isRealTime()) {
                // Real time 12hr
                time = (new SimpleDateFormat("hh:mm aa")).format(new Date());
            }else {
                // Game time 12hr
                time = (new SimpleDateFormat("hh:mm aa")).format(Util.getGameTime());
            }
        }else {
            if(system.getSystemSettings().isRealTime()) {
                // Real time 24hr
                time = (new SimpleDateFormat("HH:mm")).format(new Date());
            }else {
                // Game time 24hr
                time = (new SimpleDateFormat("HH:mm")).format(Util.getGameTime());
            }
        }

        double flag = 0;
        if(system.getSystemSettings().isAmPmTime()) {
            flag = 16;
        }
        RenderUtil.drawText(stack, time.toUpperCase(), (float)((this.system.getLeft() + this.system.SCREEN_WIDTH) + 5.0F - 27.0F)-flag, (float)this.yPos + 4.0F, Color.WHITE);
    }

    private void drawItems(PoseStack stack, int mouseX, int mouseY) {
        for(int i = 0; i < this.items.size(); ++i) {
            TaskBar.Item item = this.items.get(i);
            double itemX = this.xPos + (double)(i * 16 + 1);
            double itemY = this.yPos + 1.0D;
            // If mouse is hovering over item then show hover effect
            if (RenderUtil.isMouseWithin(mouseX, mouseY, (int)itemX-1.0D, (int)itemY-1.0D, 15, 15)) {
                this.drawItemHighlight(stack,itemX, itemY);
            }

            if (item instanceof ApplicationItem) {
                ResourceLocation id = new ResourceLocation(item.getId());
                if (this.system.isApplicationFocused(id)) {
                    drawItemFocusedHighlight(stack,itemX,itemY);
                }
                if(this.system.isApplicationOpen(id)) {
                    drawItemOpenedHighlight(stack,itemX,itemY);
                }
            }

            item.getIcon().render(stack, itemX + 1.0D, itemY + 1.0D, 12, 12);
        }

    }

    private void drawItemHighlight(PoseStack stack, double x, double y) {
        RenderUtil.drawRectWithColour(stack,x - 1.0D, y - 1.0D, 16, 16, new Color(255, 255, 255, 50));
    }

    private void drawItemOpenedHighlight(PoseStack stack, double x, double y) {
        RenderUtil.drawRectWithColour(stack,x, y + 14.0D, 14, 1, MainTheme.TASK_BAR_COLOUR.brighter().brighter());
    }

    private void drawItemFocusedHighlight(PoseStack stack, double x, double y) {
        RenderUtil.drawRectWithColour(stack,x - 1.0D, y - 1.0D, 16, 16, MainTheme.setAlpha(MainTheme.TASK_BAR_COLOUR,200).brighter());
    }

    public int getItemLeft(Item item) {
        int index = items.indexOf(item);
        return index * 16 + 1;
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

    public static class StartMenuItem extends Item {

        public StartMenuItem(ClientComputer system) {
            super("start_menu", Icons.HOME);
            this.clickListener = i -> system.openStartMenu();
        }
    }

    public static class ApplicationItem extends Item {

        private final ApplicationManifest manifest;

        public ApplicationItem(ApplicationManifest manifest, TaskBar taskBar) {
            super(manifest.getId().toString(), manifest.getIcon());
            this.manifest = manifest;
            this.clickListener = (mouseButton) -> {
                if(mouseButton == GLFW.GLFW_MOUSE_BUTTON_1) {
                    // Left
                    taskBar.system.openApplication(manifest.getId());
                }
            };
        }
    }

    public static class Item {
        protected final String id;
        protected final Icon icon;
        protected ClickListener clickListener;

        public Item(String id, Icon icon) {
            this.id = id;
            this.icon = icon;
        }

        public String getId() {
            return this.id;
        }

        public Icon getIcon() {
            return this.icon;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return id.equals(item.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

}
