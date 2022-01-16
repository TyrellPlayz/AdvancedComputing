package com.tyrellplayz.advancedtech.api.system.filesystem;

import com.tyrellplayz.advancedtech.api.content.application.Application;
import com.tyrellplayz.advancedtech.api.content.application.ApplicationManifest;
import com.tyrellplayz.advancedtech.api.icon.Icon;
import com.tyrellplayz.advancedtech.api.system.ApplicationSystem;
import com.tyrellplayz.advancedtech.api.system.System;
import joptsimple.internal.Strings;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class FileExtension {

    public static final String VALID_CHARACTERS = "[a-z]+";
    private final String name;
    private final Icon icon;
    @Nullable
    private ResourceLocation appId;
    private FileExtension.ClickListener clickListener;

    public FileExtension(String fileExtension, ResourceLocation appId) {
        this(fileExtension, new ApplicationManifest.AppIcon(appId), appId);
    }

    public FileExtension(String fileExtension, Icon icon, ResourceLocation appId) {
        this(fileExtension, icon);
        this.appId = appId;
    }

    public FileExtension(String fileExtension, Icon icon) {
        if (!Strings.isNullOrEmpty(fileExtension) && Pattern.matches(VALID_CHARACTERS, fileExtension)) {
            this.name = fileExtension.toLowerCase();
            this.icon = icon;
        } else {
            throw new IllegalArgumentException("File extension name is not valid: " + fileExtension);
        }
    }

    public FileExtension setDefaultClickListener() {
        this.clickListener = (system, file) -> {
            if (system instanceof ApplicationSystem) {
                Application application = ((ApplicationSystem)system).openApplication(this.appId);
                if (application instanceof IOpenFile) {
                    ((IOpenFile)application).openFile(file);
                }
            }

        };
        return this;
    }

    public ClickListener getClickListener() {
        return clickListener;
    }

    public FileExtension setClickListener(FileExtension.ClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }

    public interface ClickListener {
        void onClick(System system, File file);
    }

}
