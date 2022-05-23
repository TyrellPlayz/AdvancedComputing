package com.tyrellplayz.tech_craft.util;

import com.tyrellplayz.tech_craft.AdvancedComputing;
import net.minecraft.resources.ResourceLocation;

public class Util {

    private Util() {}

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(AdvancedComputing.MOD_ID,path);
    }

}
