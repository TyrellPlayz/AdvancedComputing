package com.tyrellplayz.tech_craft.api.system.filesystem;

import com.tyrellplayz.tech_craft.api.icon.Icon;
import com.tyrellplayz.tech_craft.api.icon.Icons;
import com.tyrellplayz.tech_craft.api.util.Util;
import net.minecraft.nbt.CompoundTag;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Folder implements FileSystemItem {

    public String path;
    public List<File> files = new ArrayList();
    public final FileSystem fileSystem;

    Folder(FileSystem fileSystem, String path) {
        this.fileSystem = fileSystem;
        this.path = path;
    }

    Folder(FileSystem fileSystem, String path, CompoundTag data) {
        this.fileSystem = fileSystem;
        this.path = path;

        for (String fileName : data.getAllKeys()) {
            try {
                this.createFile(fileName, true, data.getCompound(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Folder getParentFolder() {
        if (this.getPath().equals("Root")) {
            return null;
        } else {
            int index = this.path.lastIndexOf("\\");
            String parentPath = this.path.substring(0, index);
            return this.fileSystem.getFolder(parentPath);
        }
    }

    public Collection<Folder> getChildFolders() {
        return this.fileSystem.getFolders().stream().filter((folder) -> {
            int num0 = Util.findNumberOfChars('\\', this.getPath());
            int num1 = Util.findNumberOfChars('\\', folder.getPath());
            boolean flag0 = folder.getPath().contains(this.path + "\\");
            boolean flag1 = num0 + 1 == num1;

            return flag0 && flag1;
        }).sorted(Comparator.comparing(Folder::getName)).collect(Collectors.toList());
    }

    void deleteChildFolders() {
        this.getChildFolders().forEach((folder) -> {
            this.fileSystem.deleteFolder(folder.getPath());
        });
    }

    public File getFile(String name) {
        return this.files.stream().filter((file) -> file.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public void createFile(String name) throws IOException {
        this.createFile(name, false);
    }

    public void createFile(String name, boolean override) throws IOException {
        if (!override && this.containsFile(name)) {
            throw new IOException("File already exists");
        } else {
            this.deleteFile(name);
            this.files.add(new File(this, name, new CompoundTag()));
            fileSystem.updateData();
        }
    }

    public void createFile(String name, CompoundTag data) throws IOException {
        this.createFile(name, false, data);
    }

    public void createFile(String name, boolean override, CompoundTag data) throws IOException {
        if (!override && this.containsFile(name)) {
            throw new IOException("File already exists: " + name);
        } else if (!isNameValid(name)) {
            throw new IOException("File name is not valid: " + name);
        } else {
            this.deleteFile(name);
            this.files.add(new File(this, name, data));
            fileSystem.updateData();
        }
    }

    public static boolean isNameValid(String name) {
        String extension = "";
        if (name.lastIndexOf(".") != -1) {
            extension = name.substring(name.lastIndexOf(".") + 1);
            name = name.substring(0, name.lastIndexOf("."));
        }

        if (!Pattern.compile("[<>\":/\\\\?*|]").matcher(name).find() && !name.isEmpty()) {
            return Pattern.compile("[a-z]+").matcher(extension).find() && !extension.isEmpty();
        } else {
            return false;
        }
    }

    public boolean containsFile(String name) {
        return this.getFile(name) != null;
    }

    public void deleteFile(String name) {
        this.files.removeIf((file) -> file.getName().equalsIgnoreCase(name));
        fileSystem.updateData();
    }

    @Override
    public String getName() {
        int index = this.path.lastIndexOf("\\");
        return this.path.substring(index + 1);
    }

    public String getPath() {
        return this.path;
    }

    @Override
    public Icon getIcon() {
        return Icons.FOLDER;
    }

    public List<File> getFiles() {
        return this.files;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Folder folder = (Folder)o;
            return Objects.equals(this.path, folder.path);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.path);
    }

    public String toString() {
        return this.getPath();
    }

}
