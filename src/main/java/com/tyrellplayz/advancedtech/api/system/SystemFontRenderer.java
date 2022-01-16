package com.tyrellplayz.advancedtech.api.system;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class SystemFontRenderer extends Font {

    private boolean showHiddenCharacters = false;

    public SystemFontRenderer(Function<ResourceLocation, FontSet> function) {
        super(function);
    }

}
