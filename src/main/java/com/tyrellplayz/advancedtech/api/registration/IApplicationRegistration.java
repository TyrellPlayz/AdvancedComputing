package com.tyrellplayz.advancedtech.api.registration;

import com.tyrellplayz.advancedtech.api.content.application.Application;
import net.minecraft.resources.ResourceLocation;

public interface IApplicationRegistration {

    void register(ResourceLocation id, Class<? extends Application> application);

}
