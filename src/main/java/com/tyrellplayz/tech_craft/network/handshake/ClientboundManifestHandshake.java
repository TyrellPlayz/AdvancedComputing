package com.tyrellplayz.tech_craft.network.handshake;

import com.tyrellplayz.tech_craft.AdvancedComputing;
import com.tyrellplayz.tech_craft.api.content.application.ApplicationManifest;
import com.tyrellplayz.tech_craft.manager.ApplicationManager;
import com.tyrellplayz.zlib.network.message.HandshakeMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Sends application manifests from server to client on login.
 */
public class ClientboundManifestHandshake extends HandshakeMessage<ClientboundManifestHandshake> {

    private Collection<ApplicationManifest> applicationManifests;

    public ClientboundManifestHandshake() {
    }

    public ClientboundManifestHandshake(@NotNull Collection<ApplicationManifest> applicationManifests) {
        Validate.notNull(applicationManifests);
        this.applicationManifests = applicationManifests;
    }

    @Override
    public void writePacket(ClientboundManifestHandshake message, FriendlyByteBuf buf) {
        message.applicationManifests = AdvancedComputing.getApplicationManager().getApplicationManifests();
        buf.writeCollection(message.applicationManifests,(buf1, manifest) -> {
            ApplicationManifest.Serializer serializer = new ApplicationManifest.Serializer();
            serializer.toNetwork(buf,manifest);
        });
    }

    @Override
    public ClientboundManifestHandshake readPacket(FriendlyByteBuf buf) {
        this.applicationManifests = buf.readList(buf1 -> {
            ResourceLocation id = buf1.readResourceLocation();
            ApplicationManifest.Serializer serializer = new ApplicationManifest.Serializer();
            return serializer.fromNetwork(id,buf1);
        });
        return this;
    }

    @Override
    public void handlePacket(ClientboundManifestHandshake clientboundManifestHandshake, Supplier<NetworkEvent.Context> supplier) {
        AdvancedComputing.LOGGER.debug("Received application data from server");
        supplier.get().enqueueWork(() -> {
            ApplicationManager.handleUpdateManifestHandshake(this);
        });
        supplier.get().setPacketHandled(true);
        AdvancedComputing.LOGGER.info("Successfully synchronized {} application/s from server", this.applicationManifests.size());
        AdvancedComputing.NETWORK.getHandshakeChannel().reply(new ClientToServerAcknowledge(), supplier.get());
    }

    public Collection<ApplicationManifest> getApplicationManifests() {
        return applicationManifests;
    }

}
