package com.tyrellplayz.advancedtech.core;

import com.tyrellplayz.advancedtech.AdvancedTech;
import com.tyrellplayz.advancedtech.api.content.application.Application;
import com.tyrellplayz.advancedtech.api.content.application.ApplicationType;
import com.tyrellplayz.advancedtech.application.NoteApplication;
import com.tyrellplayz.advancedtech.application.system.FileExplorerApplication;
import com.tyrellplayz.advancedtech.application.system.SettingsApplication;
import com.tyrellplayz.advancedtech.application.system.TaskManagerApplication;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = AdvancedTech.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ATApplications {

    protected static final List<ApplicationType<?>> TYPES = new ArrayList<>();

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

    @SubscribeEvent
    public static void registerTypes(RegistryEvent.Register<ApplicationType<?>> event) {
        TYPES.forEach((applicationType) -> event.getRegistry().register(applicationType));
    }

    private static ResourceLocation modLoc(String name) {
        return new ResourceLocation(AdvancedTech.MOD_ID,name);
    }

}
