package com.tyrellplayz.tech_craft.api.manager;

import com.tyrellplayz.tech_craft.api.system.filesystem.FileExtension;

import java.util.Collection;

public interface IFileExtensionManager {

    FileExtension getExtension(String var1);

    Collection<FileExtension> getFileExtensions();

}
