package com.tyrellplayz.tech_craft.manager;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tyrellplayz.tech_craft.TechCraft;
import com.tyrellplayz.tech_craft.api.content.application.Application;
import com.tyrellplayz.tech_craft.api.content.application.ApplicationManifest;
import com.tyrellplayz.tech_craft.core.ApplicationType;
import com.tyrellplayz.tech_craft.util.JsonDeserializers;
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

public class OldApplicationManager extends SimplePreparableReloadListener<List<OldApplicationManager.ApplicationData>> {

    protected static final Gson GSON = Util.make(() -> {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ResourceLocation.class, JsonDeserializers.RESOURCE_LOCATION);
        return builder.create();
    });
    private ImmutableList<OldApplicationManager.ApplicationData> applicationDataList = (new ImmutableList.Builder()).build();


    @Override
    @NotNull
    protected List<OldApplicationManager.ApplicationData> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        List<OldApplicationManager.ApplicationData> applicationDataList = new ArrayList<>();
        TechCraft.LOGGER.debug("Loading applications {}", getApplicationTypes().size());

        for (ApplicationType<?> applicationType : getApplicationTypes()) {
            ResourceLocation registryName = applicationType.getRegistryName();
            if (registryName != null) {
                TechCraft.LOGGER.debug("Loading application {}", registryName.toString());
                ResourceLocation manifestLocation = new ResourceLocation(registryName.getNamespace(), String.format("app/%s/file_explorer.json", registryName.getPath()));

                ApplicationManifest manifest = null;
                try {
                    Resource resource = resourceManager.getResource(manifestLocation);

                    InputStream inputStream = resource.getInputStream();
                    JsonObject manifestObject = JsonUtil.loadJson(resource.getInputStream());
                    manifestObject.addProperty("id", registryName.toString());

                    manifest = JsonUtil.deserialize(GSON, manifestObject, ApplicationManifest.class);
                    ObfuscationReflectionHelper.setPrivateValue(ApplicationManifest.class, manifest, manifestObject, "jsonObject");
                    inputStream.close();
                    // TODO: Validator
                    //if (Validator.isValidObject(manifest)) {
                    //    AdvancedTech.LOGGER.error("Could not load application {} as its manifest file is missing or malformed", registryName);
                    //    continue;
                    //}

                } catch (IOException e) {
                    TechCraft.LOGGER.error("Could not parse manifest file {}", registryName, e);
                }

                applicationDataList.add(new ApplicationData(manifest));
                TechCraft.LOGGER.debug("Loaded application {}", registryName.toString());
            }
        }
        return applicationDataList;
    }

    protected void apply(@NotNull List<OldApplicationManager.ApplicationData> list, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        ImmutableList.Builder<OldApplicationManager.ApplicationData> builder = new ImmutableList.Builder<>();
        builder.addAll(list);
        this.applicationDataList = builder.build();
        TechCraft.LOGGER.debug("Loaded {} applications.", this.applicationDataList.size());
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
                OldApplicationManager.ApplicationData data = this.getApplication(id);
                ObfuscationReflectionHelper.setPrivateValue(Application.class, application, data.getManifest(), "applicationManifest");
                return application;
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException var7) {
                TechCraft.LOGGER.error("Could not create application " + id, var7);
                return null;
            }
        }
    }

    @Nullable
    public OldApplicationManager.ApplicationData getApplication(ResourceLocation id) {
        return this.applicationDataList.stream().filter((applicationData) -> applicationData.manifest.getId().equals(id)).findFirst().orElse(null);
    }

    public ImmutableList<OldApplicationManager.ApplicationData> getApplications() {
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
    public void handleUpdateMessage(OldApplicationManager.IApplicationProvider message) {
        this.applicationDataList = message.getApplicationsData();
    }

    public interface IApplicationProvider {
        ImmutableList<OldApplicationManager.ApplicationData> getApplicationsData();
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
