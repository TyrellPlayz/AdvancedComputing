package com.tyrellplayz.advancedtech.api.system.filesystem;

import com.tyrellplayz.advancedtech.api.icon.Icon;
import com.tyrellplayz.advancedtech.api.icon.Icons;
import joptsimple.internal.Strings;
import net.minecraft.nbt.CompoundTag;

public class File implements FileSystemItem{

    private final String name;
    private final Folder folder;
    private final CompoundTag data;
    private String fileExtension;

    File(Folder folder, String name, CompoundTag data) {
        this.folder = folder;
        this.name = name;
        this.data = data;
        if (name.contains(".")) {
            this.fileExtension = name.substring(name.lastIndexOf(".") + 1);
        }

    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getNameWithoutExtension() {
        return this.name.lastIndexOf(".") != -1 ? this.name.substring(0, this.name.lastIndexOf(".")) : this.getName();
    }

    public Folder getFolder() {
        return folder;
    }

    public String getPath() {
        return this.folder.getPath() + "\\" + this.name;
    }

    public CompoundTag getData() {
        return data;
    }

    public FileExtension getFileExtension() {
        return !Strings.isNullOrEmpty(this.fileExtension) && FileExtensionManager.get().getExtension(this.fileExtension) != null ? FileExtensionManager.get().getExtension(this.fileExtension) : new FileExtension(this.fileExtension, Icons.FILE);
    }

    @Override
    public Icon getIcon() {
        return this.getFileExtension().getIcon();
    }

    public String toString() {
        return this.getPath();
    }

}
