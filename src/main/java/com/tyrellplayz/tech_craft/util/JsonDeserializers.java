package com.tyrellplayz.tech_craft.util;

import com.google.gson.JsonDeserializer;
import com.tyrellplayz.tech_craft.api.content.application.ApplicationManifest;
import com.tyrellplayz.tech_craft.api.icon.Icon;
import net.minecraft.resources.ResourceLocation;

public class JsonDeserializers {

    public static final JsonDeserializer<ResourceLocation> RESOURCE_LOCATION = (jsonElement, type, jsonDeserializationContext) ->
            new ResourceLocation(jsonElement.getAsString());
    public static final JsonDeserializer<Icon> APPLICATION_ICON = (jsonElement, type, jsonDeserializationContext) ->
            new ApplicationManifest.AppIcon(new ResourceLocation(jsonElement.getAsString()));

}
