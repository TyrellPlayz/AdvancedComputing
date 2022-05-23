package com.tyrellplayz.tech_craft.api.system.filesystem;

import com.tyrellplayz.tech_craft.api.system.System;
import net.minecraft.nbt.CompoundTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileSystem {

    public static final String PATH_SEPARATOR = "\\";
    public static final char PATH_SEPARATOR_CHAR = '\\';
    public static final String ROOT_FOLDER_PATH = "Root";
    public static final String INVALID_CHARACTERS_REGEX = "[<>\":/\\\\?*|]";

    private final String LATEST_VERSION = "0.1.0";

    private final System system;
    private final List<Folder> folders = new ArrayList();
    private String version;

    private FileSystemItem clipboardItem;

    public FileSystem(System system, CompoundTag fileSystemData) {
        this.system = system;
        if (fileSystemData == null | fileSystemData.isEmpty()) {
            this.setupNewFileSystem();
        } else {
            this.setupFileSystem(fileSystemData);
        }

    }

    private void setupNewFileSystem() {
        this.version = "0.1.0";
        this.folders.add(new Folder(this, "Root"));

        try {
            this.createFolder("Root", "Applications");
            this.createFolder("Root", "System");
            this.createFolder("Root", "User");
            this.createFolder("Root\\User", "Documents");
            this.createFolder("Root\\User", "Pictures");
            this.createFolder("Root\\User", "Downloads");
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    private void setupFileSystem(CompoundTag fileSystemData) {
        this.version = fileSystemData.getString("Version");

        for (String folderPath : fileSystemData.getAllKeys()) {
            if (!folderPath.equals("Version")) {
                CompoundTag files = fileSystemData.getCompound(folderPath);
                this.folders.add(new Folder(this, folderPath, files));
            }
        }

    }

    public CompoundTag read() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Version", this.version);

        for (Folder folder : this.folders) {
            CompoundTag fileData = new CompoundTag();

            for (File file : folder.getFiles()) {
                fileData.put(file.getName(), file.getData());
            }

            tag.put(folder.getPath(), fileData);
        }

        return tag;
    }

    public List<Folder> getFolders() {
        return this.folders;
    }

    public Folder getFolder(String path) {
        return this.folders.stream().filter((folder) -> folder.getPath().equalsIgnoreCase(path)).findAny().orElse(null);
    }

    public void createFolder(String path, String name) throws IOException {
        this.createFolder(path + "\\" + name, false);
    }

    public void createFolder(String path, String name, boolean override) throws IOException {
        this.createFolder(path + "\\" + name, override);
    }

    public void createFolder(String path) throws IOException {
        this.createFolder(path, false);
    }

    public void createFolder(String path, boolean override) throws IOException {
        if (!this.validatePath(path)) {
            throw new IOException("Folder path/name not valid " + path);
        } else if (!override && this.containsFolder(path)) {
            throw new IOException("Folder already exists");
        } else {
            this.deleteFolder(path);
            List<String> parentFolders = new ArrayList<>();

            for(String s = path.substring(0, path.lastIndexOf("\\")); s.contains("\\"); s = s.substring(0, s.lastIndexOf("\\"))) {
                parentFolders.add(s);
            }

            for (String parentFolder : parentFolders) {
                if (!this.containsFolder(parentFolder)) {
                    this.folders.add(new Folder(this, parentFolder));
                }
            }

            this.folders.add(new Folder(this, path));
            updateData();
        }
    }

    public boolean containsFolder(String path) {
        return this.getFolder(path) != null;
    }

    public void deleteFolder(String path) {
        if (!path.equalsIgnoreCase("Root")) {
            if (this.containsFolder(path)) {
                Folder folder = this.getFolder(path);
                folder.deleteChildFolders();
                this.folders.remove(folder);
            }

        }
        updateData();
    }

    public boolean copyFolder(String path) {
        return true;
    }

    public boolean cutFolder(String path) {
        return true;
    }

    public boolean pasteFolder(String path) {

        setClipboardItem(null);
        return true;
    }

    public boolean hasClipboard() {
        return clipboardItem != null;
    }

    public void setClipboardItem(FileSystemItem clipboardItem) {
        this.clipboardItem = clipboardItem;
    }

    public FileSystemItem getClipboardItem() {
        return clipboardItem;
    }

    public boolean validatePath(String path) {
        try {
            String text = path.substring(0, path.indexOf("\\"));
            String name = path.substring(path.lastIndexOf("\\") + 1);
            return text.equalsIgnoreCase("Root") && this.validateName(name);
        } catch (Exception var4) {
            return false;
        }
    }

    public boolean validateName(String name) {
        return !Pattern.compile("[<>\":/\\\\?*|]").matcher(name).find() && !name.isEmpty();
    }

    public void updateData() {
        system.updateData();
    }

}
