package com.tyrellplayz.advancedtech.api.manager;

import com.tyrellplayz.advancedtech.api.system.filesystem.FileExtension;

import java.util.Collection;

public interface IFileExtensionManager {

    FileExtension getExtension(String var1);

    Collection<FileExtension> getFileExtensions();

}
