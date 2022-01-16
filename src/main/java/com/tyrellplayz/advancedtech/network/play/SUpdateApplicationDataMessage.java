package com.tyrellplayz.advancedtech.network.play;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tyrellplayz.advancedtech.AdvancedTech;
import com.tyrellplayz.advancedtech.api.content.application.ApplicationManifest;
import com.tyrellplayz.advancedtech.manager.ApplicationManager;
import com.tyrellplayz.advancedtech.util.JsonDeserializers;
import com.tyrellplayz.zlib.network.message.PlayMessage;
import com.tyrellplayz.zlib.util.JsonUtil;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.function.Supplier;

public class SUpdateApplicationDataMessage extends PlayMessage<SUpdateApplicationDataMessage> implements ApplicationManager.IApplicationProvider {

    protected static final Gson GSON = Util.make(() -> {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ResourceLocation.class, JsonDeserializers.RESOURCE_LOCATION);
        return builder.create();
    });
    private ImmutableList<ApplicationManager.ApplicationData> applicationDataList;

    public SUpdateApplicationDataMessage() {
    }

    public SUpdateApplicationDataMessage(ImmutableList<ApplicationManager.ApplicationData> applicationDataList) {
        this();
        Validate.notNull(applicationDataList);
        this.applicationDataList = applicationDataList;
    }

    @Override
    public void writePacket(SUpdateApplicationDataMessage message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.applicationDataList.size());
        message.applicationDataList.forEach((applicationData) -> buf.writeUtf(applicationData.getManifest().getJsonObject().toString()));
    }

    @Override
    public SUpdateApplicationDataMessage readPacket(FriendlyByteBuf buf) {
        ImmutableList.Builder<ApplicationManager.ApplicationData> applicationDataListBuilder = new ImmutableList.Builder<>();
        int applicationDataListSize = buf.readVarInt();

        for(int i = 0; i < applicationDataListSize; ++i) {
            JsonObject manifestObject = JsonUtil.loadJson(buf.readUtf());
            ApplicationManifest manifest = JsonUtil.deserialize(GSON, manifestObject, ApplicationManifest.class);
            ObfuscationReflectionHelper.setPrivateValue(ApplicationManifest.class, manifest, manifestObject, "jsonObject");

            applicationDataListBuilder.add(new ApplicationManager.ApplicationData(manifest));
        }

        this.applicationDataList = applicationDataListBuilder.build();
        return this;
    }

    @Override
    public void handlePacket(SUpdateApplicationDataMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            AdvancedTech.getApplicationManager().handleUpdateMessage(this);
        });
        supplier.get().setPacketHandled(true);
    }

    public ImmutableList<ApplicationManager.ApplicationData> getApplicationsData() {
        return this.applicationDataList;
    }

}
