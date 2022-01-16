package com.tyrellplayz.advancedtech.api.content.dialog;

import com.tyrellplayz.advancedtech.api.component.Button;
import com.tyrellplayz.advancedtech.api.component.FileBrowser;
import com.tyrellplayz.advancedtech.api.component.Label;
import com.tyrellplayz.advancedtech.api.component.TextField;
import com.tyrellplayz.advancedtech.api.content.Layer;
import com.tyrellplayz.advancedtech.api.icon.Icons;
import com.tyrellplayz.advancedtech.api.system.filesystem.File;
import com.tyrellplayz.advancedtech.api.system.filesystem.FileSystem;
import com.tyrellplayz.advancedtech.api.system.filesystem.Folder;
import net.minecraft.nbt.CompoundTag;

import java.io.IOException;
import java.util.function.Consumer;

public class FileDialog {

    public static class Save extends Dialog {

        private FileSystem fileSystem;
        private FileBrowser fileBrowser;
        private TextField addressField;
        private TextField nameField;
        private Button saveButton;
        private Button cancelButton;
        private Folder defaultFolder;
        private final String suggestedName;
        private final String fileExtension;
        private final CompoundTag dataToSave;
        private Consumer<File> onFileSaved;

        public Save(FileSystem fileSystem, CompoundTag dataToSave, String fileExtension) {
            this(fileSystem, dataToSave, "", fileExtension);
        }

        public Save(FileSystem fileSystem, Folder defaultFolder, CompoundTag dataToSave, String fileExtension) {
            this(fileSystem, defaultFolder, dataToSave, "", fileExtension);
        }

        public Save(FileSystem fileSystem, CompoundTag dataToSave, String suggestedName, String fileExtension) {
            this(fileSystem, fileSystem.getFolder("Root"), dataToSave, suggestedName, fileExtension);
        }

        public Save(FileSystem fileSystem, Folder defaultFolder, CompoundTag dataToSave, String suggestedName, String fileExtension) {
            super(Icons.FOLDER, "Save file", "");
            this.fileSystem = fileSystem;
            this.defaultFolder = defaultFolder;
            this.dataToSave = dataToSave;
            this.suggestedName = suggestedName;
            this.fileExtension = fileExtension;
        }

        public void setOnFileSaved(Consumer<File> onFileSaved) {
            this.onFileSaved = onFileSaved;
        }

        public void onLoad() {
            Layer mainLayer = new Layer(this, 150, 139);
            this.fileBrowser = new FileBrowser(2, 20, mainLayer.getWidth() - 3, mainLayer.getHeight() - 60, this.fileSystem);
            this.fileBrowser.setCurrentFolder(this.defaultFolder);
            this.fileBrowser.setOnFolderOpened((folder) -> {
                this.addressField.setText(this.fileBrowser.getCurrentFolder().getPath());
            });
            this.fileBrowser.setOnFileOpened((file) -> {
            });
            mainLayer.addComponent(this.fileBrowser);
            this.addressField = new TextField(2, 2, mainLayer.getWidth() - 4);
            this.addressField.setText(this.fileBrowser.getCurrentFolder().getPath());
            this.addressField.setEditable(false);
            mainLayer.addComponent(this.addressField);
            this.nameField = new TextField(2, 100, mainLayer.getWidth() - 4);
            this.nameField.setText(this.suggestedName);
            this.nameField.setKeyListener((c, code) -> {
                String name = this.nameField.getText();
                if (this.fileSystem.validateName(name)) {
                    this.nameField.clearError();
                    this.saveButton.setEnabled(true);
                } else {
                    this.nameField.setError("Input not valid");
                    this.saveButton.setEnabled(false);
                }

                return true;
            });
            mainLayer.addComponent(this.nameField);
            Label extensionLabel = new Label(90, 122, this.fileExtension);
            mainLayer.addComponent(extensionLabel);
            this.saveButton = new Button(2, 118, "Save");
            this.saveButton.setClickListener((mouseButton) -> {
                String name = this.nameField.getText();
                if (this.fileSystem.validateName(name)) {
                    try {
                        this.fileBrowser.getCurrentFolder().createFile(name + this.fileExtension, this.dataToSave);
                        if (this.onFileSaved != null) {
                            this.onFileSaved.accept(this.fileBrowser.getCurrentFolder().getFile(name + this.fileExtension));
                        }

                        this.getWindow().close();
                    } catch (IOException var4) {
                        this.nameField.setError(var4.getMessage());
                    }

                } else {
                    this.nameField.setError("Name not valid.");
                    this.saveButton.setEnabled(false);
                }
            });
            mainLayer.addComponent(this.saveButton);
            this.cancelButton = new Button(40, 118, "Cancel");
            this.cancelButton.setClickListener((mouseButton) -> {
                this.getWindow().close();
            });
            mainLayer.addComponent(this.cancelButton);
            this.setActiveLayer(mainLayer);
        }

    }

    public static class Open extends Dialog {

        private FileSystem fileSystem;
        private FileBrowser fileBrowser;
        private TextField addressField;
        private Button openButton;
        private Button cancelButton;
        private Folder defaultFolder;
        private Consumer<File> onFileOpen;

        public Open(FileSystem fileSystem) {
            this(fileSystem, fileSystem.getFolder("Root"));
        }

        public Open(FileSystem fileSystem, Folder defaultFolder) {
            super(Icons.FOLDER, "Open file", "");
            this.fileSystem = fileSystem;
            this.defaultFolder = defaultFolder;
        }

        public FileDialog.Open setOnFileOpen(Consumer<File> onFileOpen) {
            this.onFileOpen = onFileOpen;
            return this;
        }

        public void onLoad() {
            Layer mainLayer = new Layer(this, 150, 120);
            this.fileBrowser = new FileBrowser(2, 20, mainLayer.getWidth() - 3, mainLayer.getHeight() - 42, this.fileSystem);
            this.fileBrowser.setCurrentFolder(this.defaultFolder);
            this.fileBrowser.setOnFolderOpened((folder) -> {
                this.addressField.setText(this.fileBrowser.getCurrentFolder().getPath());
            });
            this.fileBrowser.setOnFileSelected((file) -> {
                this.openButton.setEnabled(file != null);
            });
            this.fileBrowser.setOnFileOpened((file) -> {
                if (this.onFileOpen != null) {
                    this.onFileOpen.accept(file);
                }

                this.getWindow().close();
            });
            mainLayer.addComponent(this.fileBrowser);
            this.addressField = new TextField(2, 2, mainLayer.getWidth() - 4);
            this.addressField.setText(this.fileBrowser.getCurrentFolder().getPath());
            this.addressField.setEditable(false);
            mainLayer.addComponent(this.addressField);
            this.openButton = new Button(2, 100, "Open");
            this.openButton.setEnabled(false);
            this.openButton.setClickListener((mouseButton) -> {
                if (this.onFileOpen != null) {
                    this.onFileOpen.accept((File)this.fileBrowser.getSelectedItem());
                }

                this.getWindow().close();
            });
            mainLayer.addComponent(this.openButton);
            this.cancelButton = new Button(40, 100, "Cancel");
            this.cancelButton.setClickListener((mouseButton) -> {
                this.getWindow().close();
            });
            mainLayer.addComponent(this.cancelButton);
            this.setActiveLayer(mainLayer);
        }

    }

}
