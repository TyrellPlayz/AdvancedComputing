package com.tyrellplayz.tech_craft.api.system;

import com.tyrellplayz.tech_craft.api.content.Content;
import com.tyrellplayz.tech_craft.api.content.application.Application;
import com.tyrellplayz.tech_craft.api.content.application.ApplicationManifest;
import com.tyrellplayz.tech_craft.api.content.dialog.Dialog;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;

public interface ApplicationSystem extends System {

    @Nullable
    Application getApplication(ResourceLocation id);

    Collection<ApplicationManifest> getInstalledApplications();

    Application[] getRunningApplications();

    Application openApplication(ResourceLocation id);

    boolean isApplicationOpen(ResourceLocation id);

    boolean isApplicationFocused(ResourceLocation id);

    boolean isApplicationInstalled(ResourceLocation id);

    void closeApplication(ResourceLocation id);

    void openDialog(Dialog dialog);

    void closeDialog(Dialog dialog);

    void focusWindow(IWindow<? extends Content> window);

    void openWindow(IWindow<? extends Content> window);

    void closeWindow(IWindow<? extends Content> window);

    boolean isWindowOpen(IWindow<? extends Content> window);

}
