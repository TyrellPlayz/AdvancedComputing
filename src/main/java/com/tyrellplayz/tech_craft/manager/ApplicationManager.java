package com.tyrellplayz.tech_craft.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tyrellplayz.tech_craft.TechCraft;
import com.tyrellplayz.tech_craft.api.content.application.Application;
import com.tyrellplayz.tech_craft.api.content.application.ApplicationManifest;
import com.tyrellplayz.tech_craft.core.ApplicationType;
import com.tyrellplayz.tech_craft.network.handshake.ClientboundManifestHandshake;
import com.tyrellplayz.tech_craft.network.play.ClientboundUpdateManifestPacket;
import com.tyrellplayz.tech_craft.util.JsonDeserializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ApplicationManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder())
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(ResourceLocation.class, JsonDeserializers.RESOURCE_LOCATION)
            .create();
    private static final Logger LOGGER = LogManager.getLogger();

    private List<ApplicationManifest> applicationManifests = ImmutableList.of();
    private Map<ResourceLocation, ApplicationManifest> byName = ImmutableMap.of();
    private boolean hasErrors;

    public ApplicationManager() {
        super(GSON, "apps");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager manager, ProfilerFiller profiler) {
        Collection<ApplicationType<?>> applications = getApplications();
        TechCraft.LOGGER.debug("Loading applications {}", applications.size());

        this.hasErrors = false;
        ImmutableList.Builder<ApplicationManifest> manifestList = ImmutableList.builder();
        ImmutableMap.Builder<ResourceLocation, ApplicationManifest> byNameBuilder = ImmutableMap.builder();

        for (ApplicationType<?> registeredApplication : applications) {
            TechCraft.LOGGER.debug("Loading application: {}", registeredApplication.getRegistryName());
            try{
                JsonElement element = jsonMap.get(registeredApplication.getRegistryName());
                if(element == null) {
                    throw new FileNotFoundException("No manifest file was found for application: "+registeredApplication.getRegistryName());
                }

                JsonObject manifestJson = GsonHelper.convertToJsonObject(element,"top element");
                ApplicationManifest.Serializer serializer = new ApplicationManifest.Serializer();
                ApplicationManifest manifest = serializer.fromJson(registeredApplication.getRegistryName(),manifestJson);

                manifestList.add(manifest);
                byNameBuilder.put(registeredApplication.getRegistryName(),manifest);
            }catch (Exception exception) {
                LOGGER.error(exception);
                this.hasErrors = true;
            }
        }

        applicationManifests = manifestList.build();
        byName = byNameBuilder.build();
        TechCraft.LOGGER.debug("Loaded {} applications successfully", applicationManifests.size());
    }

    public boolean hadErrorsLoading() {
        return this.hasErrors;
    }

    @Nullable
    public ApplicationManifest getApplicationManifestFor(ResourceLocation name) {
        return byName.get(name);
    }

    public Collection<ApplicationManifest> getApplicationManifests() {
        return applicationManifests;
    }

    public boolean isApplicationLoaded(ResourceLocation name) {
        return this.getApplicationManifestFor(name) != null;
    }

    public Application createApplication(ResourceLocation name) {
        if (isApplicationLoaded(name)) {
            ApplicationType<?> applicationType = getApplicationRegistry().getValue(name);
            Class<? extends Application> applicationClass = applicationType.getApplicationClass();
            try {
                return Application.create(applicationClass,getApplicationManifestFor(name));
            } catch (NoSuchMethodException e) {
                LOGGER.error(String.format("%s does not have an empty parameter constructor",applicationClass.getName()),e);
            } catch (IllegalAccessException e) {
                LOGGER.error(String.format("The constructor of %s cannot be accessed. Ensure it is public",applicationClass.getName()),e);
            } catch (InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Collection<ApplicationType<?>> getApplications() {
        IForgeRegistry<ApplicationType<?>> types = RegistryManager.ACTIVE.getRegistry(ApplicationType.class);
        if(types == null) return new ArrayList<>();
        return types.getValues();
    }

    private static IForgeRegistry<ApplicationType<?>> getApplicationRegistry() {
        return RegistryManager.ACTIVE.getRegistry(ApplicationType.class);
    }

    public static void handleUpdateManifestHandshake(ClientboundManifestHandshake packet) {
        TechCraft.getApplicationManager().applicationManifests.addAll(packet.getApplicationManifests());
    }

    public static void handleUpdateManifestPacket(ClientboundUpdateManifestPacket packet) {
        TechCraft.getApplicationManager().applicationManifests.addAll(packet.getApplicationManifests());
    }

}
