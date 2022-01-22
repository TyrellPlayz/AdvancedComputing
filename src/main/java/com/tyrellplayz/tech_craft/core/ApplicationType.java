package com.tyrellplayz.tech_craft.core;

import com.tyrellplayz.tech_craft.TechCraft;
import com.tyrellplayz.tech_craft.api.content.application.Application;
import com.tyrellplayz.tech_craft.application.NoteApplication;
import com.tyrellplayz.tech_craft.application.system.FileExplorerApplication;
import com.tyrellplayz.tech_craft.application.system.SettingsApplication;
import com.tyrellplayz.tech_craft.application.system.TaskManagerApplication;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class ApplicationType<T extends Application> extends ForgeRegistryEntry<ApplicationType<?>> {

    private static final List<ApplicationType<?>> TYPES = new ArrayList<>();

    public static ApplicationType<SettingsApplication> SETTINGS = register("settings", SettingsApplication.class);
    public static ApplicationType<NoteApplication> NOTE = register("note", NoteApplication.class);
    public static ApplicationType<TaskManagerApplication> TASK_MANAGER = register("task_manager", TaskManagerApplication.class);
    public static ApplicationType<FileExplorerApplication> FILE_EXPLORER = register("file_explorer", FileExplorerApplication.class);

    private static <T extends Application> ApplicationType<T> register(String id, Class<T> applicationClass) {
        ApplicationType<T> applicationType = new ApplicationType<>(applicationClass);
        applicationType.setRegistryName(modLoc(id));
        TYPES.add(applicationType);
        return applicationType;
    }

    private static ResourceLocation modLoc(String name) {
        return new ResourceLocation(TechCraft.MOD_ID,name);
    }

    @SubscribeEvent
    public static void registerTypes(RegistryEvent.Register<ApplicationType<?>> event) {
        TYPES.forEach((applicationType) -> event.getRegistry().register(applicationType));
    }

    private final Class<T> applicationClass;

    public ApplicationType(Class<T> applicationClass) {
        this.applicationClass = applicationClass;
    }

    public Class<T> getApplicationClass() {
        return this.applicationClass;
    }

}
