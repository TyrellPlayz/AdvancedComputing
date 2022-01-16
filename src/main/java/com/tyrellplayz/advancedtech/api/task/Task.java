package com.tyrellplayz.advancedtech.api.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class Task {

    private String name;
    private Callback<CompoundTag> callback = null;
    private boolean success = false;

    public Task(String name) {
        this.name = name;
    }

    public final Task setCallback(Callback<CompoundTag> callback) {
        this.callback = callback;
        return this;
    }

    public final void callback(CompoundTag nbt) {
        if (this.callback != null) {
            this.callback.execute(nbt, this.success);
        }
    }

    public final void setSuccessful() {
        this.success = true;
    }

    public final boolean isSucessful() {
        return this.success;
    }

    public final void complete() {
        this.success = false;
    }

    public final String getName() {
        return this.name;
    }

    public abstract void prepareRequest(CompoundTag tag);

    public abstract void processRequest(CompoundTag tag, Level level, Player player);

    public abstract void prepareResponse(CompoundTag tag);

    public abstract void processResponse(CompoundTag tag);

}
