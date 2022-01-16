package com.tyrellplayz.advancedtech.util;

import com.tyrellplayz.advancedtech.AdvancedTech;
import net.minecraft.resources.ResourceLocation;

public class Util {

    private Util() {}

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(AdvancedTech.MOD_ID,path);
    }

}
