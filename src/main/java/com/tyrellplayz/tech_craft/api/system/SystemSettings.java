package com.tyrellplayz.tech_craft.api.system;

import com.tyrellplayz.tech_craft.AdvancedComputing;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class SystemSettings {

    private final System system;

    private ResourceLocation backgroundLocation;

    // Time & Date
    private boolean realTime;
    private boolean amPmTime;

    public SystemSettings(System system) {
        this.system = system;

        this.backgroundLocation = new ResourceLocation(AdvancedComputing.MOD_ID,"textures/gui/background/hill.png");
        this.realTime = true;
        this.amPmTime = true;
    }

    public ResourceLocation getBackgroundLocation() {
        return this.backgroundLocation;
    }

    public void setBackgroundLocation(ResourceLocation backgroundLocation) {
        this.backgroundLocation = backgroundLocation;
        system.updateData();
    }

    // Time & Date

    public boolean isRealTime() {
        return realTime;
    }

    public void setRealTime(boolean realTime) {
        this.realTime = realTime;
        system.updateData();
    }

    public boolean isAmPmTime() {
        return amPmTime;
    }

    public void setAmPmTime(boolean amPmTime) {
        this.amPmTime = amPmTime;
        system.updateData();
    }

    public void read(CompoundTag compound) {
        this.backgroundLocation = new ResourceLocation(compound.getString("Background"));
        // Time & Date
        this.realTime = compound.getBoolean("RealTime");
        this.amPmTime = compound.getBoolean("AmPmTime");
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Background", this.backgroundLocation.toString());
        // Time & Date
        tag.putBoolean("RealTime",this.realTime);
        tag.putBoolean("AmPmTime",this.amPmTime);
        return tag;
    }

}
