package com.tyrellplayz.tech_craft.api.registration;

import com.tyrellplayz.tech_craft.api.content.application.Application;
import net.minecraft.resources.ResourceLocation;

public interface IApplicationRegistration {

    void register(ResourceLocation id, Class<? extends Application> application);

}
