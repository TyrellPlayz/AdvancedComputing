package com.tyrellplayz.advancedtech.api.content.application;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class ApplicationType<T extends Application> extends ForgeRegistryEntry<ApplicationType<?>> {

    private final Class<T> applicationClass;

    public ApplicationType(Class<T> applicationClass) {
        this.applicationClass = applicationClass;
    }

    public Class<T> getApplicationClass() {
        return this.applicationClass;
    }

}
