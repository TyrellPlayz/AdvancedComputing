package com.tyrellplayz.advancedtech.api.system.filesystem;

import com.tyrellplayz.advancedtech.api.manager.IFileExtensionManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileExtensionManager implements IFileExtensionManager {

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

    @Override
    public FileExtension getExtension(String extension) {
        return this.fileExtensionsMap.get(extension);
    }

    @Override
    public Collection<FileExtension> getFileExtensions() {
        return this.fileExtensionsMap.values();
    }
}
