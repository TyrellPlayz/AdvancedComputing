package com.tyrellplayz.advancedtechnology.proxy;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public interface ModProxy {

    default void onCommonSetup(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }

    default void onClientSetup(FMLClientSetupEvent fmlClientSetupEvent) {

    }

}
