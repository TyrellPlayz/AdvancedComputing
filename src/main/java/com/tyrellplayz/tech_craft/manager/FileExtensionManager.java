package com.tyrellplayz.tech_craft.manager;

import com.tyrellplayz.tech_craft.api.system.filesystem.FileExtension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileExtensionManager {

    private static FileExtensionManager instance;
    private Map<String, FileExtension> fileExtensionsMap = new HashMap();

    private FileExtensionManager() {
    }

    public static FileExtensionManager get() {
        if (instance == null) {
            instance = new FileExtensionManager();
        }

        return instance;
    }

    public void registerExtension(FileExtension fileExtension) {
        if (this.fileExtensionsMap.containsKey(fileExtension.getName())) {
            System.out.println("The file extension " + fileExtension.getName() + " is already registered.");
        } else {
            this.fileExtensionsMap.put(fileExtension.getName(), fileExtension);
        }
    }

    public FileExtension getExtension(String extension) {
        return this.fileExtensionsMap.get(extension);
    }

    public Collection<FileExtension> getFileExtensions() {
        return this.fileExtensionsMap.values();
    }
}
