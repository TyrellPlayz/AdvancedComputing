package com.tyrellplayz.tech_craft.network.play;

import com.tyrellplayz.tech_craft.AdvancedComputing;
import com.tyrellplayz.tech_craft.api.content.application.ApplicationManifest;
import com.tyrellplayz.tech_craft.manager.ApplicationManager;
import com.tyrellplayz.zlib.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

public class ClientboundUpdateManifestPacket extends PlayMessage<ClientboundUpdateManifestPacket> {

    private Collection<ApplicationManifest> applicationManifests;

    public ClientboundUpdateManifestPacket() { }

    public ClientboundUpdateManifestPacket(@NotNull Collection<ApplicationManifest> applicationManifests) {
        Validate.notNull(applicationManifests);
        this.applicationManifests = applicationManifests;
    }

    @Override
    public void writePacket(ClientboundUpdateManifestPacket message, FriendlyByteBuf buf) {
        message.applicationManifests = AdvancedComputing.getApplicationManager().getApplicationManifests();
        buf.writeCollection(applicationManifests,(buf1, manifest) -> manifest.getSerializer().toNetwork(buf,manifest));
    }

    @Override
    public ClientboundUpdateManifestPacket readPacket(FriendlyByteBuf buf) {
        this.applicationManifests = buf.readList(buf1 -> {
            ResourceLocation id = buf1.readResourceLocation();
            ApplicationManifest.Serializer serializer = new ApplicationManifest.Serializer();
            return serializer.fromNetwork(id,buf1);
        });
        return this;
    }

    @Override
    public void handlePacket(ClientboundUpdateManifestPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> ApplicationManager.handleUpdateManifestPacket(this));
        supplier.get().setPacketHandled(true);
    }

    public Collection<ApplicationManifest> getApplicationManifests() {
        return applicationManifests;
    }
}
