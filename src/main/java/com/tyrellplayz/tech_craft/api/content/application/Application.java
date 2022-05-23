package com.tyrellplayz.tech_craft.api.content.application;

import com.tyrellplayz.tech_craft.api.content.Content;
import com.tyrellplayz.tech_craft.api.content.Layer;
import com.tyrellplayz.tech_craft.api.content.LayeredContent;
import com.tyrellplayz.tech_craft.api.system.IFileSystem;
import com.tyrellplayz.tech_craft.api.system.IWindow;
import com.tyrellplayz.tech_craft.api.system.filesystem.Folder;
import com.tyrellplayz.zlib.util.RenderUtil;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class Application extends LayeredContent {

    private ApplicationManifest applicationManifest;
    private ApplicationPreferences preferences;

    private boolean hasPreferences = false;

    public Application() {}

    /**
     * Loads when the application is loaded / opened. Create layers and assign components here.
     */
    @Override
    public void onLoad() {
        if(hasPreferences()) {
            this.preferences.load();
        }
        this.setActiveLayer(new Layer(this,100,100));
    }

    public Folder getApplicationFolder() {
        com.tyrellplayz.tech_craft.api.system.filesystem.FileSystem fileSystem = ((IFileSystem)this.getWindow().getComputer()).getFileSystem();
        String path = "Root\\Applications\\" + this.getApplicationManifest().getId().getNamespace() + "\\" + this.getApplicationManifest().getId().getPath();
        if (!fileSystem.containsFolder(path)) {
            try {
                fileSystem.createFolder(path);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return fileSystem.getFolder(path);
    }

    public ApplicationManifest getApplicationManifest() {
        return applicationManifest;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Application application) {
            return this.applicationManifest.equals(application.applicationManifest);
        }
        return false;
    }

    public final void setWindow(IWindow<? extends Content> window) {
        super.setWindow(window);
        this.getWindow().setTitle(this.applicationManifest.getName());
        this.getWindow().setIcon(this.applicationManifest.getIcon());
    }

    @Nullable
    public ApplicationPreferences getPreferences() {
        return preferences;
    }

    public boolean hasPreferences() {
        return hasPreferences;
    }

    public void setHasPreferences(boolean hasPreferences) {
        if (hasPreferences) preferences = new ApplicationPreferences(this);
        this.hasPreferences = hasPreferences;
    }

    public static Application create(Class<? extends Application> applicationClass, ApplicationManifest manifest) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Application application = applicationClass.getDeclaredConstructor().newInstance();
        application.applicationManifest = manifest;
        return application;
    }

}
