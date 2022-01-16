package com.tyrellplayz.advancedtech.api.icon;

import com.tyrellplayz.advancedtech.AdvancedTech;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public enum Icons implements Icon {
    ERROR("error", 0, 0),
    WARNING("warning", 1, 0),
    CROSS("cross", 2, 0),
    CHECK_MARK("check_mark", 3, 0),
    HOME("home", 4, 0),
    MENU("menu", 5, 0),
    LEFT_ARROW("left_arrow", 6, 0),
    RIGHT_ARROW("right_arrow", 7, 0),
    CLIPBOARD("clipboard", 0, 1),
    COPY("copy", 1, 1),
    CUT("cut", 2, 1),
    NEW_FILE("new_file", 3, 1),
    NEW_FOLDER("new_folder", 4, 1),
    FILE("file", 5, 1),
    FOLDER("folder", 6, 1);

    private ResourceLocation id;
    private int xPos;
    private int yPos;
    private Color colour;

    Icons(ResourceLocation id, int xPos, int yPos) {
        this.colour = Color.WHITE;
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    Icons(String id, int xPos, int yPos) {
        this.colour = Color.WHITE;
        this.id = new ResourceLocation(AdvancedTech.MOD_ID, id);
        this.xPos = xPos;
        this.yPos = yPos;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public int getU() {
        return this.xPos * this.getWidth();
    }

    @Override
    public int getV() {
        return this.yPos * this.getHeight();
    }

    @Override
    public int getWidth() {
        return 12;
    }

    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public int getImageWidth() {
        return 256;
    }

    @Override
    public int getImageHeight() {
        return 256;
    }

    @Override
    public Color getColour() {
        return colour;
    }

    @Override
    public Icon setColour(Color colour) {
        this.colour = colour;
        return this;
    }

    @Override
    public ResourceLocation getImageLocation() {
        return new ResourceLocation(AdvancedTech.MOD_ID, "textures/gui/icons.png");
    }
}
