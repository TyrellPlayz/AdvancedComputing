package com.tyrellplayz.advancedtech.util;

import com.google.gson.JsonDeserializer;
import com.tyrellplayz.advancedtech.api.content.application.ApplicationManifest;
import com.tyrellplayz.advancedtech.api.icon.Icon;
import net.minecraft.resources.ResourceLocation;

public class JsonDeserializers {

    public static final JsonDeserializer<ResourceLocation> RESOURCE_LOCATION = (jsonElement, type, jsonDeserializationContext) ->
            new ResourceLocation(jsonElement.getAsString());
    public static final JsonDeserializer<Icon> APPLICATION_ICON = (jsonElement, type, jsonDeserializationContext) ->
            new ApplicationManifest.AppIcon(new ResourceLocation(jsonElement.getAsString()));

}
