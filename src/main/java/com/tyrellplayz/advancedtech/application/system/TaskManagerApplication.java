package com.tyrellplayz.advancedtech.application.system;

import com.tyrellplayz.advancedtech.api.component.Button;
import com.tyrellplayz.advancedtech.api.component.Component;
import com.tyrellplayz.advancedtech.api.component.ItemList;
import com.tyrellplayz.advancedtech.api.content.Layer;
import com.tyrellplayz.advancedtech.api.content.application.Application;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskManagerApplication extends Application {

    private Button btnEndTask;
    private ItemList<TaskManagerApplication.TaskManagerItem> itemListApplications;
    int tick;

    public void onLoad() {
        Layer layer = new Layer(this, 200, 150);
        this.btnEndTask = new Button(2, 2, "End Task");
        this.btnEndTask.setEnabled(false);
        this.btnEndTask.setAlignment(Component.Alignment.BOTTOM_LEFT);
        this.itemListApplications = new ItemList<TaskManagerApplication.TaskManagerItem>(2, 2, layer.getWidth() - 4, layer.getHeight() - 18 - 6);
        Application[] applications = this.getWindow().getComputer().getRunningApplications();

        for (Application runningApplication : applications) {
            this.itemListApplications.getItems().add(new TaskManagerItem(runningApplication.getApplicationManifest().getId(), runningApplication.getApplicationManifest().getName()));
        }

        Collections.sort(this.itemListApplications.getItems());
        this.itemListApplications.setOnItemSelected((item, itemIndex) -> {
            this.btnEndTask.setEnabled(item != null);

        });
        this.btnEndTask.setClickListener((mouseButton) -> {
            TaskManagerApplication.TaskManagerItem item = this.itemListApplications.getSelectedItem();
            if (item != null) {
                this.getWindow().getComputer().closeApplication(item.getId());
                this.itemListApplications.removeSelectedItem();
            }

        });
        layer.addComponent(this.btnEndTask);
        layer.addComponent(this.itemListApplications);
        this.setActiveLayer(layer);
    }

    public void onTick() {
        ++this.tick;
        if (this.tick >= 20) {
            List<TaskManagerApplication.TaskManagerItem> taskManagerItems = new ArrayList<>();
            Application[] applications = this.getWindow().getComputer().getRunningApplications();

            for (Application runningApplication : applications) {
                taskManagerItems.add(new TaskManagerItem(runningApplication.getApplicationManifest().getId(), runningApplication.getApplicationManifest().getName()));
            }

            Collections.sort(taskManagerItems);
            this.itemListApplications.updateItems(taskManagerItems);
            this.tick = 0;
        }

    }

    public static class TaskManagerItem implements Comparable<TaskManagerApplication.TaskManagerItem> {
        private final ResourceLocation id;
        private final String name;

        public TaskManagerItem(ResourceLocation id, String name) {
            this.id = id;
            this.name = name;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name + " - " + this.id;
        }

        public int compareTo(TaskManagerApplication.TaskManagerItem taskManagerItem) {
            return this.name.compareTo(taskManagerItem.name);
        }
    }

}
