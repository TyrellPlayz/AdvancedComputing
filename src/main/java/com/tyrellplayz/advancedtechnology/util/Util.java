package com.tyrellplayz.advancedtechnology.util;

import com.tyrellplayz.advancedtechnology.AdvancedTechnology;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class Util {

    private Util() {}

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(AdvancedTechnology.MOD_ID,path);
    }

    public static String simpleItemName(ItemLike item) {
        return item.asItem().getRegistryName().getPath();
    }

}
