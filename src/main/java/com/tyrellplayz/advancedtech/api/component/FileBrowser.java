package com.tyrellplayz.advancedtech.api.component;

import com.tyrellplayz.advancedtech.api.system.filesystem.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FileBrowser extends ItemList<FileSystemItem> {

    private Consumer<Folder> onFolderOpened;
    private Consumer<File> onFileOpened;
    private Consumer<Folder> onFolderSelected;
    private Consumer<File> onFileSelected;
    private int lastItemIndex = -1;
    boolean clicked = false;
    private final FileSystem fileSystem;
    private Folder currentFolder;
    private boolean showFileExtensions = false;

    public FileBrowser(int left, int top, int width, int height, FileSystem fileSystem) {
        super(left, top, width, height);
        this.fileSystem = fileSystem;
        this.setGetName(FileSystemItem::getName);
        this.setGetIcon(FileSystemItem::getIcon);
        this.currentFolder = fileSystem.getFolder("Root");
        List<FileSystemItem> fileSystemItemList = new ArrayList<>();
        fileSystemItemList.addAll(this.currentFolder.getChildFolders());
        fileSystemItemList.addAll(this.currentFolder.getFiles());
        this.setItems(fileSystemItemList);
    }

    public void setOnFolderOpened(Consumer<Folder> onFolderOpened) {
        this.onFolderOpened = onFolderOpened;
    }

    public void setOnFileOpened(Consumer<File> onFileOpened) {
        this.onFileOpened = onFileOpened;
    }

    public void setOnFolderSelected(Consumer<Folder> onFolderSelected) {
        this.onFolderSelected = onFolderSelected;
    }

    public void setOnFileSelected(Consumer<File> onFileSelected) {
        this.onFileSelected = onFileSelected;
    }

    public void setItems(List<FileSystemItem> items) {
        if (!this.currentFolder.getPath().equals("Root")) {
            items.add(0, new BackFolder());
        }

        super.setItems(items);
        this.lastItemIndex = -1;
    }

    public void updateItems(List<FileSystemItem> items) {
        if (!this.currentFolder.getPath().equals("Root")) {
            items.add(0, new BackFolder());
        }

        super.updateItems(items);
        this.lastItemIndex = -1;
    }

    public void clear() {
        super.clear();
        this.lastItemIndex = -1;
    }

    public void setCurrentFolder(Folder folder) {
        this.currentFolder = folder;
        this.update();
    }

    public void onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.onMouseClicked(mouseX, mouseY, mouseButton);
        if (this.isItemSelected()) {
            FileSystemItem fileSystemItem = this.getSelectedItem();
            if (this.clicked && this.lastItemIndex == this.selectedIndex) {
                ArrayList<FileSystemItem> fileSystemItemList;
                if (fileSystemItem instanceof Folder) {
                    this.currentFolder = (Folder)fileSystemItem;
                    fileSystemItemList = new ArrayList<>();
                    fileSystemItemList.addAll(this.currentFolder.getChildFolders());
                    fileSystemItemList.addAll(this.currentFolder.getFiles());
                    this.setItems(fileSystemItemList);
                    if (this.onFolderOpened != null) {
                        this.onFolderOpened.accept(this.currentFolder);
                        this.deselect();
                        this.hoverIndex = -1;
                    }
                } else if (fileSystemItem instanceof File) {
                    if (this.onFileOpened != null) {
                        this.onFileOpened.accept((File)fileSystemItem);
                        this.deselect();
                        this.hoverIndex = -1;
                    }
                } else if (fileSystemItem instanceof BackFolder && !this.currentFolder.getPath().equals("Root")) {
                    this.currentFolder = this.currentFolder.getParentFolder();
                    fileSystemItemList = new ArrayList<>();
                    fileSystemItemList.addAll(this.currentFolder.getChildFolders());
                    fileSystemItemList.addAll(this.currentFolder.getFiles());
                    this.setItems(fileSystemItemList);
                    if (this.onFolderOpened != null) {
                        this.onFolderOpened.accept(this.currentFolder);
                    }
                }

                this.lastItemIndex = -1;
                this.clicked = false;
            } else if (this.clicked) {
                this.clicked = false;
                this.lastItemIndex = -1;
            } else {
                this.lastItemIndex = this.selectedIndex;
                if (fileSystemItem instanceof Folder) {
                    if (this.onFolderSelected != null) {
                        this.onFolderSelected.accept((Folder)fileSystemItem);
                    }
                } else if (fileSystemItem instanceof File) {
                    if (this.onFileSelected != null) {
                        this.onFileSelected.accept((File)fileSystemItem);
                    }
                } else if (fileSystemItem instanceof BackFolder && this.onFolderSelected != null) {
                    this.onFolderSelected.accept(null);
                }

                this.clicked = true;
            }
        } else {
            this.lastItemIndex = -1;
            this.clicked = false;
            if (this.onFolderSelected != null) {
                this.onFolderSelected.accept(null);
            }

            if (this.onFileSelected != null) {
                this.onFileSelected.accept(null);
            }
        }

    }

    public void back() {
        Folder parentFolder = this.currentFolder.getParentFolder();
        if (parentFolder != null) {
            this.currentFolder = parentFolder;
            List<FileSystemItem> fileSystemItemList = new ArrayList<>();
            fileSystemItemList.addAll(this.currentFolder.getChildFolders());
            fileSystemItemList.addAll(this.currentFolder.getFiles());
            this.setItems(fileSystemItemList);
            if (this.onFolderOpened != null) {
                this.onFolderOpened.accept(this.currentFolder);
            }

        }
    }

    public void update() {
        List<FileSystemItem> fileSystemItems = new ArrayList<>();
        fileSystemItems.addAll(this.currentFolder.getChildFolders());
        fileSystemItems.addAll(this.currentFolder.getFiles());
        this.setItems(fileSystemItems);
    }

    public void showFileExtensions(boolean show) {
        this.showFileExtensions = show;
        if (show) {
            this.setGetName(FileSystemItem::getName);
        } else {
            this.setGetName((fileSystemItem) -> fileSystemItem.getName().contains(".") ? fileSystemItem.getName().substring(0, fileSystemItem.getName().lastIndexOf(".")) : fileSystemItem.getName());
        }

    }

    public boolean isShowingFileExtensions() {
        return this.showFileExtensions;
    }

    public Folder getCurrentFolder() {
        return this.currentFolder;
    }

}
