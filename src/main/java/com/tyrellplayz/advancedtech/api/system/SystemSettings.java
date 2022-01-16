package com.tyrellplayz.advancedtech.api.system;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class SystemSettings {

    private ResourceLocation backgroundLocation;

    public SystemSettings(ResourceLocation backgroundLocation) {
        this.backgroundLocation = backgroundLocation;
    }

    public ResourceLocation getBackgroundLocation() {
        return this.backgroundLocation;
    }

    public void setBackgroundLocation(ResourceLocation backgroundLocation) {
        this.backgroundLocation = backgroundLocation;
    }

    public void read(CompoundTag compound) {
        this.backgroundLocation = new ResourceLocation(compound.getString("background"));
    }

    public CompoundTag write(CompoundTag compound) {
        compound.putString("background", this.backgroundLocation.toString());
        return compound;
    }

}
