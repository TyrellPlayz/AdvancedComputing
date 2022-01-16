package com.tyrellplayz.advancedtech.manager;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tyrellplayz.advancedtech.AdvancedTech;
import com.tyrellplayz.advancedtech.api.content.application.Application;
import com.tyrellplayz.advancedtech.api.content.application.ApplicationManifest;
import com.tyrellplayz.advancedtech.api.content.application.ApplicationType;
import com.tyrellplayz.advancedtech.util.JsonDeserializers;
import com.tyrellplayz.advancedtech.util.validator.Validator;
import com.tyrellplayz.zlib.util.JsonUtil;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ApplicationManager extends SimplePreparableReloadListener<List<ApplicationManager.ApplicationData>> {

    protected static final Gson GSON = Util.make(() -> {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ResourceLocation.class, JsonDeserializers.RESOURCE_LOCATION);
        return builder.create();
    });
    private ImmutableList<ApplicationManager.ApplicationData> applicationDataList = (new ImmutableList.Builder()).build();


    @Override
    @NotNull
    protected List<ApplicationManager.ApplicationData> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        System.out.println("prepare");
        List<ApplicationManager.ApplicationData> applicationDataList = new ArrayList<>();
        System.out.println(getApplicationTypes().size());
        AdvancedTech.LOGGER.debug("Loading applications {}", getApplicationTypes().size());

        for (ApplicationType<?> applicationType : getApplicationTypes()) {
            ResourceLocation registryName = applicationType.getRegistryName();
            if (registryName != null) {
                AdvancedTech.LOGGER.debug("Loading application {}", registryName.toString());
                ResourceLocation manifestLocation = new ResourceLocation(registryName.getNamespace(), String.format("app/%s/manifest.json", registryName.getPath()));

                ApplicationManifest manifest = null;
                try {
                    Resource resource = resourceManager.getResource(manifestLocation);

                    InputStream inputStream = resource.getInputStream();
                    JsonObject manifestObject = JsonUtil.loadJson(resource.getInputStream());
                    manifestObject.addProperty("id", registryName.toString());

                    manifest = JsonUtil.deserialize(GSON, manifestObject, ApplicationManifest.class);
                    ObfuscationReflectionHelper.setPrivateValue(ApplicationManifest.class, manifest, manifestObject, "jsonObject");
                    inputStream.close();
                    //TODO: Validator
                    //if (Validator.isValidObject(manifest)) {
                    //    AdvancedTech.LOGGER.error("Could not load application {} as its manifest file is missing or malformed", registryName);
                    //    continue;
                    //}

                } catch (IOException e) {
                    AdvancedTech.LOGGER.error("Could not parse manifest file {}", registryName, e);
                }

                applicationDataList.add(new ApplicationData(manifest));
                AdvancedTech.LOGGER.debug("Loaded application {}", registryName.toString());
            }
        }
        return applicationDataList;
    }

    protected void apply(@NotNull List<ApplicationManager.ApplicationData> list, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        System.out.println("apply");
        ImmutableList.Builder<ApplicationManager.ApplicationData> builder = new ImmutableList.Builder<>();
        builder.addAll(list);
        this.applicationDataList = builder.build();
        AdvancedTech.LOGGER.debug("Loaded {} applications.", this.applicationDataList.size());
    }

    @Nullable
    public Application createApplication(ResourceLocation id) {
        if (!this.isApplicationLoaded(id)) {
            return null;
        } else {
            try {
                IForgeRegistry<ApplicationType<?>> types = RegistryManager.ACTIVE.getRegistry(ApplicationType.class);
                ApplicationType<?> type = types.getValue(id);

                assert type != null;

                Class<? extends Application> applicationClass = type.getApplicationClass();
                Application application = applicationClass.getDeclaredConstructor().newInstance();
                ApplicationManager.ApplicationData data = this.getApplication(id);
                ObfuscationReflectionHelper.setPrivateValue(Application.class, application, data.getManifest(), "applicationManifest");
                return application;
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException var7) {
                AdvancedTech.LOGGER.error("Could not create application " + id, var7);
                return null;
            }
        }
    }

    @Nullable
    public ApplicationManager.ApplicationData getApplication(ResourceLocation id) {
        return this.applicationDataList.stream().filter((applicationData) -> applicationData.manifest.getId().equals(id)).findFirst().orElse(null);
    }

    public ImmutableList<ApplicationManager.ApplicationData> getApplications() {
        return this.applicationDataList;
    }

    public Collection<ResourceLocation> getRegisteredApplications() {
        List<ResourceLocation> registeredApplications = new ArrayList<>();
        getApplicationTypes().forEach((applicationType) -> {
            registeredApplications.add(applicationType.getRegistryName());
        });
        return registeredApplications;
    }

    public boolean isApplicationLoaded(ResourceLocation id) {
        return this.getApplication(id) != null;
    }

    public boolean isApplicationRegistered(ResourceLocation id) {
        return this.getRegisteredApplications().contains(id);
    }

    private static Collection<ApplicationType<?>> getApplicationTypes() {
        IForgeRegistry<ApplicationType<?>> types = RegistryManager.ACTIVE.getRegistry(ApplicationType.class);
        if(types == null) {
            return new ArrayList<>();
        }
        return types.getValues();
    }

    @OnlyIn(Dist.CLIENT)
    public void handleUpdateMessage(ApplicationManager.IApplicationProvider message) {
        this.applicationDataList = message.getApplicationsData();
    }

    public interface IApplicationProvider {
        ImmutableList<ApplicationManager.ApplicationData> getApplicationsData();
    }

    public static class ApplicationData {
        private final ApplicationManifest manifest;

        public ApplicationData(ApplicationManifest manifest) {
            this.manifest = manifest;
        }

        public ApplicationManifest getManifest() {
            return this.manifest;
        }

    }

}
