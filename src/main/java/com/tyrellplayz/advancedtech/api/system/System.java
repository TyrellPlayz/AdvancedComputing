package com.tyrellplayz.advancedtech.api.system;

public interface System {

    /**
     * Gets called when the computer starts up.
     */
    void onStartup();

    /**
     * Gets called when the computer shuts down.
     */
    void onShutdown();

    /**
     * Gets called on each tick.
     */
    void tick();

    void updateData();

    SystemSettings getSystemSettings();

}
