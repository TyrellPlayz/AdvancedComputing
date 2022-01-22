package com.tyrellplayz.tech_craft.util;

import com.tyrellplayz.tech_craft.TechCraft;
import net.minecraft.resources.ResourceLocation;

public class Util {

    private Util() {}

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(TechCraft.MOD_ID,path);
    }

}
