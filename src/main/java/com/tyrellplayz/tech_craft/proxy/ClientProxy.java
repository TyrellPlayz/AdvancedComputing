package com.tyrellplayz.tech_craft.proxy;

import com.tyrellplayz.zlib.proxy.ModProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements ModProxy {

    @Override
    public void onClientSetup(FMLClientSetupEvent fmlClientSetupEvent) {

    }
}
