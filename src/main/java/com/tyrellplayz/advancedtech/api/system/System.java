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

    /**
     * @return The settings/preferences of the system. For the moment it's open for anyone but maybe restricted in the future.
     */
    SystemSettings getSystemSettings();

}
