package com.tyrellplayz.advancedtech.network.handshake;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tyrellplayz.advancedtech.AdvancedTech;
import com.tyrellplayz.advancedtech.api.content.application.ApplicationManifest;
import com.tyrellplayz.advancedtech.manager.ApplicationManager;
import com.tyrellplayz.advancedtech.util.JsonDeserializers;
import com.tyrellplayz.zlib.network.message.HandshakeMessage;
import com.tyrellplayz.zlib.util.JsonUtil;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SUpdateApplicationDataHandshake extends HandshakeMessage<SUpdateApplicationDataHandshake> implements ApplicationManager.IApplicationProvider {

    protected static final Gson GSON = Util.make(() -> {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ResourceLocation.class, JsonDeserializers.RESOURCE_LOCATION);
        return builder.create();
    });
    private ImmutableList<ApplicationManager.ApplicationData> applicationDataList;

    public SUpdateApplicationDataHandshake() {
    }

    @Override
    public void writePacket(SUpdateApplicationDataHandshake message, FriendlyByteBuf buf) {
        message.applicationDataList = AdvancedTech.getApplicationManager().getApplications();
        buf.writeVarInt(message.applicationDataList.size());
        message.applicationDataList.forEach((applicationData) -> {
            buf.writeUtf(applicationData.getManifest().getJsonObject().toString());
        });
    }

    @Override
    public SUpdateApplicationDataHandshake readPacket(FriendlyByteBuf buf) {
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
    public void handlePacket(SUpdateApplicationDataHandshake sUpdateApplicationDataHandshake, Supplier<NetworkEvent.Context> supplier) {
        AdvancedTech.LOGGER.debug("Received application data from server");
        supplier.get().enqueueWork(() -> {
            AdvancedTech.getApplicationManager().handleUpdateMessage(this);
        });
        supplier.get().setPacketHandled(true);
        AdvancedTech.LOGGER.info("Successfully synchronized {} application/s from server", this.applicationDataList.size());
        AdvancedTech.NETWORK.getHandshakeChannel().reply(new ClientToServerAcknowledge(), supplier.get());
    }

    @Override
    public ImmutableList<ApplicationManager.ApplicationData> getApplicationsData() {
        return this.applicationDataList;
    }
}
