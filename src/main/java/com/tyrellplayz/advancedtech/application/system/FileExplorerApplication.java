package com.tyrellplayz.advancedtech.application.system;

import com.tyrellplayz.advancedtech.api.component.*;
import com.tyrellplayz.advancedtech.api.content.Layer;
import com.tyrellplayz.advancedtech.api.content.application.Application;
import com.tyrellplayz.advancedtech.api.content.dialog.ErrorDialog;
import com.tyrellplayz.advancedtech.api.content.dialog.InputDialog;
import com.tyrellplayz.advancedtech.api.icon.Icons;
import com.tyrellplayz.advancedtech.api.system.IFileSystem;
import com.tyrellplayz.advancedtech.api.system.Tooltip;
import com.tyrellplayz.advancedtech.api.system.filesystem.File;
import com.tyrellplayz.advancedtech.api.system.filesystem.FileSystem;
import com.tyrellplayz.advancedtech.api.system.filesystem.FileSystemItem;
import com.tyrellplayz.advancedtech.api.system.filesystem.Folder;

import java.io.IOException;

public class FileExplorerApplication extends Application {

    private Layer mainLayer;
    private Layer settingsLayer;
    private TextField addressField;
    private FileBrowser fileBrowser;
    private Button backButton;
    private Button copyButton;
    private Button cutButton;
    private Button pasteButton;
    private Button deleteButton;
    private Button newFolderButton;
    private Button settingsButton;
    private FileSystem fileSystem;

    public FileExplorerApplication() {
    }

    public void onLoad() {
        if (!(this.getWindow().getComputer() instanceof IFileSystem)) {
            ErrorDialog errorDialog = new ErrorDialog(this.getApplicationManifest().getName(), "System does not support a file system.");
            this.getWindow().getComputer().openDialog(errorDialog);
            errorDialog.getWindow().setPosition(this.getWindow().getFromLeft(), this.getWindow().getFromTop());
            this.getWindow().close();
        } else {
            this.fileSystem = ((IFileSystem)this.getWindow().getComputer()).getFileSystem();
            this.mainLayer = new Layer(this, 200, 150);
            this.fileBrowser = new FileBrowser(20, 21, this.mainLayer.getWidth() - 21, this.mainLayer.getHeight() - 21, this.fileSystem);
            this.fileBrowser.showFileExtensions(this.getPreferences().getOrDefault("ShowFileExtensions", false));
            this.addressField = new TextField(20, 2, this.mainLayer.getWidth() - 21);
            this.addressField.setEditable(false);
            this.addressField.setText(this.fileBrowser.getCurrentFolder().getPath());
            this.mainLayer.addComponent(this.addressField);
            this.fileBrowser.setOnFolderOpened((folder) -> {
                this.addressField.setText(folder.getPath());
                this.deleteButton.setEnabled(false);
            });
            this.fileBrowser.setOnFileOpened((file) -> {
                if (file.getFileExtension().getClickListener() != null) {
                    file.getFileExtension().getClickListener().onClick(this.getWindow().getComputer(), file);
                    this.deleteButton.setEnabled(false);
                }
            });
            this.fileBrowser.setOnFolderSelected((folder) -> {
                this.deleteButton.setEnabled(folder != null);
            });
            this.fileBrowser.setOnFileSelected((file) -> {
                this.deleteButton.setEnabled(file != null);
            });
            this.mainLayer.addComponent(this.fileBrowser);
            this.backButton = new Button(1, 1, Icons.LEFT_ARROW);
            this.backButton.setTooltip(new Tooltip("Back"));
            this.backButton.setClickListener((mouseButton) -> {
                this.fileBrowser.back();
            });
            this.mainLayer.addComponent(this.backButton);
            this.copyButton = new Button(1, 20, Icons.COPY);
            this.copyButton.setTooltip(new Tooltip("Copy"));
            this.copyButton.setEnabled(false);
            this.copyButton.setClickListener((mouseButton) -> {
            });
            this.mainLayer.addComponent(this.copyButton);
            this.cutButton = new Button(1, 39, Icons.CUT);
            this.cutButton.setTooltip(new Tooltip("Cut"));
            this.cutButton.setEnabled(false);
            this.cutButton.setClickListener((mouseButton) -> {
            });
            this.mainLayer.addComponent(this.cutButton);
            this.pasteButton = new Button(1, 58, Icons.CLIPBOARD);
            this.pasteButton.setTooltip(new Tooltip("Paste"));
            this.pasteButton.setEnabled(false);
            this.pasteButton.setClickListener((mouseButton) -> {
            });
            this.mainLayer.addComponent(this.pasteButton);
            this.newFolderButton = new Button(1, 77, Icons.NEW_FOLDER);
            this.newFolderButton.setTooltip(new Tooltip("New Folder"));
            this.newFolderButton.setClickListener((mouseButton) -> {
                InputDialog newFolderDialog = new InputDialog(this.getApplicationManifest().getIcon(), this.getApplicationManifest().getName(), "Create a new folder", "Folder Name");
                newFolderDialog.setInputListener((input) -> {
                    try {
                        this.fileSystem.createFolder(this.fileBrowser.getCurrentFolder().getPath(), input);
                    } catch (IOException var3) {
                        var3.printStackTrace();
                    }

                    this.fileBrowser.update();
                });
                newFolderDialog.setValidation((s) -> this.fileSystem.validateName(s));
                this.getWindow().getComputer().openDialog(newFolderDialog);
                newFolderDialog.getWindow().setPosition(this.getWindow().getFromLeft(), this.getWindow().getFromTop());
            });
            this.mainLayer.addComponent(this.newFolderButton);
            this.deleteButton = new Button(1, 96, Icons.CROSS);
            this.deleteButton.setTooltip(new Tooltip("Delete"));
            this.deleteButton.setEnabled(false);
            this.deleteButton.setClickListener((mouseButton) -> {
                if (this.fileBrowser.isItemSelected()) {
                    FileSystemItem fileSystemItem = this.fileBrowser.getSelectedItem();
                    if (fileSystemItem instanceof Folder) {
                        this.fileSystem.deleteFolder(((Folder)fileSystemItem).getPath());
                    } else if (fileSystemItem instanceof File) {
                        ((File)fileSystemItem).getFolder().deleteFile(fileSystemItem.getName());
                    }

                    this.fileBrowser.update();
                    this.deleteButton.setEnabled(false);
                }

            });
            this.mainLayer.addComponent(this.deleteButton);
            this.settingsButton = new Button(1, 115, Icons.HOME);
            this.settingsButton.setTooltip(new Tooltip("Settings"));
            this.settingsButton.setClickListener((mouseButton) -> {
                this.setActiveLayer(this.settingsLayer);
            });
            this.mainLayer.addComponent(this.settingsButton);
            this.settingsLayer = new Layer(this, 200, 150);
            Label settingsLabel = new Label(2, 2, "Settings");
            this.settingsLayer.addComponent(settingsLabel);
            Button settingsBackButton = new Button(2, 15, "Back");
            settingsBackButton.setClickListener((mouseButton) -> {
                this.setActiveLayer(this.mainLayer);
            });
            this.settingsLayer.addComponent(settingsBackButton);
            CheckBox settingsShowFileExtensionsBox = new CheckBox(2, 35, "Show file extensions");
            settingsShowFileExtensionsBox.setChecked(this.fileBrowser.isShowingFileExtensions());
            settingsShowFileExtensionsBox.setOnChecked(this::showFileExtensions);
            this.settingsLayer.addComponent(settingsShowFileExtensionsBox);
            this.setActiveLayer(this.mainLayer);
        }
    }

    public void showFileExtensions(boolean show) {
        this.fileBrowser.showFileExtensions(show);
        this.getPreferences().set("ShowFileExtensions", this.fileBrowser.isShowingFileExtensions());
    }

    public Folder getApplicationFolder() {
        FileSystem fileSystem = ((IFileSystem)this.getWindow().getComputer()).getFileSystem();
        String path = "Root\\System\\" + this.getApplicationManifest().getId().getPath();
        if (!fileSystem.containsFolder(path)) {
            try {
                fileSystem.createFolder(path);
            } catch (IOException var4) {
                var4.printStackTrace();
                return null;
            }
        }

        return fileSystem.getFolder(path);
    }

}
