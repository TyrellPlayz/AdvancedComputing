package com.tyrellplayz.advancedtech.api.system;

import com.tyrellplayz.advancedtech.AdvancedTech;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class SystemSettings {

    private ResourceLocation backgroundLocation;

    public SystemSettings() {
        this.backgroundLocation = new ResourceLocation(AdvancedTech.MOD_ID,"textures/gui/background/hill.png");
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
