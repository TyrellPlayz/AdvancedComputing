package com.tyrellplayz.advancedtech.application;

import com.tyrellplayz.advancedtech.api.component.Button;
import com.tyrellplayz.advancedtech.api.component.ItemList;
import com.tyrellplayz.advancedtech.api.component.TextArea;
import com.tyrellplayz.advancedtech.api.component.TextField;
import com.tyrellplayz.advancedtech.api.content.Layer;
import com.tyrellplayz.advancedtech.api.content.application.Application;
import com.tyrellplayz.advancedtech.api.content.dialog.FileDialog;
import com.tyrellplayz.advancedtech.api.system.IFileSystem;
import com.tyrellplayz.advancedtech.api.system.Tooltip;
import com.tyrellplayz.advancedtech.api.system.filesystem.File;
import com.tyrellplayz.advancedtech.api.system.filesystem.FileSystem;
import com.tyrellplayz.advancedtech.api.system.filesystem.Folder;
import com.tyrellplayz.advancedtech.api.system.filesystem.IOpenFile;
import net.minecraft.nbt.CompoundTag;

public class NoteApplication extends Application implements IOpenFile {

    private Layer mainLayer;
    private Layer noteLayer;
    private ItemList<File> fileItemList;
    private Button loadButton;
    private Button openButton;
    private Button newButton;
    private TextField titleField;
    private TextArea textArea;
    private Button backButton;
    private Button editButton;
    private Button saveButton;
    private Button saveAsButton;

    public NoteApplication() {
    }

    @Override
    public void onLoad() {
        super.onLoad();
        mainLayer = new Layer(this,124,132);

        this.fileItemList = new ItemList<>(2,2,84,128);
        this.fileItemList.setGetName(File::getNameWithoutExtension);
        this.updateFiles();
        this.fileItemList.setOnItemSelected((file, integer) -> {
            this.loadButton.setEnabled(integer != -1);
        });
        this.loadButton = new Button(88,2,"Load");
        loadButton.setTooltip(new Tooltip("Open selected file from list."));
        loadButton.setClickListener(this::onLoadButtonClicked);
        loadButton.setEnabled(false);

        openButton = new Button(88,22,"Open");
        openButton.setTooltip(new Tooltip("Open a note."));
        openButton.setClickListener(this::onOpenButtonClicked);

        newButton = new Button(88,42,"New");
        newButton.setTooltip(new Tooltip("Create a new note."));
        newButton.setClickListener(this::onNewButtonClicked);

        mainLayer.addComponent(fileItemList);
        mainLayer.addComponent(loadButton);
        mainLayer.addComponent(openButton);
        mainLayer.addComponent(newButton);

        this.noteLayer = new Layer(this, 165, 150);
        this.titleField = new TextField(2, 2, this.noteLayer.getWidth() - 4);
        this.titleField.setPlaceholder("Title");
        this.titleField.setScrollBarVisible(true);
        this.noteLayer.addComponent(this.titleField);
        this.textArea = new TextArea(2, 22, this.noteLayer.getWidth() - 4, this.noteLayer.getHeight() - 45);
        this.textArea.setPlaceholder("Text");
        this.noteLayer.addComponent(this.textArea);
        this.backButton = new Button(4, this.noteLayer.getHeight() - 20, "Back");
        this.backButton.setClickListener((mouseButton) -> {
            this.titleField.setText("");
            this.textArea.setText("");
            this.setActiveLayer(mainLayer);
        });
        this.noteLayer.addComponent(this.backButton);
        this.editButton = new Button(39, this.noteLayer.getHeight() - 20, "Edit");
        this.editButton.setClickListener((mouseButton) -> {
            this.editNote();
        });
        this.noteLayer.addComponent(this.editButton);
        this.saveButton = new Button(this.noteLayer.getWidth() - 90, this.noteLayer.getHeight() - 20, "Save");
        this.saveButton.setClickListener((mouseButton) -> {
            this.saveNote();
        });
        this.noteLayer.addComponent(this.saveButton);
        this.saveAsButton = new Button(this.noteLayer.getWidth() - 54, this.noteLayer.getHeight() - 20, "Save As");
        this.saveAsButton.setClickListener((mouseButton) -> {
            this.saveNote();
        });
        this.noteLayer.addComponent(this.saveAsButton);

        setActiveLayer(mainLayer);
    }

    public void onLoadButtonClicked(int mouseButton) {
        File file = this.fileItemList.getSelectedItem();
        if (file == null) {
            this.loadButton.setEnabled(false);
            this.fileItemList.deselect();
        } else {
            this.openNote(file);
        }
    }

    public void onOpenButtonClicked(int mouseButton) {
        FileDialog.Open openDialog = new FileDialog.Open(this.getApplicationFolder().fileSystem);
        openDialog.setOnFileOpen(this::openNote);
        this.openDialog(openDialog);
    }

    public void onNewButtonClicked(int mouseButton) {
        this.openNote(null);
    }

    public void updateFiles() {
        Folder folder = this.getApplicationFolder();
        if (folder != null) {
            this.fileItemList.setItems(folder.getFiles());
        }
    }

    public void openNote(File file) {
        if (file == null) {
            this.titleField.setEditable(true);
            this.textArea.setEditable(true);
            this.editButton.setEnabled(false);
            this.saveButton.setEnabled(true);
            this.saveAsButton.setEnabled(true);
            this.setActiveLayer(this.noteLayer);
        } else {
            CompoundTag data = file.getData();
            if (data.contains("Title") && data.contains("Text")) {
                this.titleField.setText(data.getString("Title"));
                this.textArea.setText(data.getString("Text"));
            } else {
                this.titleField.setText(file.getName());
                this.textArea.setText(data.toString());
            }

            this.titleField.setEditable(false);
            this.textArea.setEditable(false);
            this.editButton.setEnabled(true);
            this.saveButton.setEnabled(false);
            this.saveAsButton.setEnabled(false);
            this.setActiveLayer(this.noteLayer);
        }
    }

    public void saveNote() {
        CompoundTag data = new CompoundTag();
        data.putString("Title", this.titleField.getText());
        data.putString("Text", this.textArea.getText());
        FileSystem fileSystem = ((IFileSystem)this.getWindow().getComputer()).getFileSystem();
        FileDialog.Save saveDialog = new FileDialog.Save(fileSystem, this.getApplicationFolder(), data, this.titleField.getText(), ".note");
        saveDialog.setOnFileSaved((file) -> {
            this.titleField.setEditable(false);
            this.textArea.setEditable(false);
            this.editButton.setEnabled(true);
            this.saveButton.setEnabled(false);
            this.saveAsButton.setEnabled(false);
        });
        this.openDialog(saveDialog);
    }

    public void editNote() {
        this.titleField.setEditable(true);
        this.textArea.setEditable(true);
        this.editButton.setEnabled(false);
        this.saveButton.setEnabled(true);
        this.saveAsButton.setEnabled(true);
    }

    @Override
    public void openFile(File file) {
        this.openNote(file);
    }
}
