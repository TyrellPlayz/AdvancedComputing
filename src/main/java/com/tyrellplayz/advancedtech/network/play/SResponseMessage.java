package com.tyrellplayz.advancedtech.network.play;

import com.tyrellplayz.advancedtech.api.task.Task;
import com.tyrellplayz.advancedtech.manager.TaskManager;
import com.tyrellplayz.zlib.network.message.Message;
import com.tyrellplayz.zlib.network.message.PlayMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SResponseMessage extends PlayMessage<SResponseMessage> {

    private int id;
    private Task request;
    private CompoundTag nbt;

    public SResponseMessage() {
    }

    public SResponseMessage(int id, Task request) {
        this();
        this.id = id;
        this.request = request;
    }

    @Override
    public void writePacket(SResponseMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.id);
        buf.writeBoolean(message.request.isSucessful());
        buf.writeUtf(message.request.getName());
        CompoundTag nbt = new CompoundTag();
        message.request.prepareResponse(nbt);
        buf.writeNbt(nbt);
        message.request.complete();
    }

    @Override
    public SResponseMessage readPacket(FriendlyByteBuf buf) {
        SResponseMessage responseMessage = new SResponseMessage();
        responseMessage.id = buf.readInt();
        boolean successful = buf.readBoolean();
        responseMessage.request = TaskManager.get().getTaskAndRemove(responseMessage.id);
        if (successful) {
            responseMessage.request.setSuccessful();
        }

        String name = buf.readUtf(256);
        responseMessage.nbt = buf.readNbt();
        return responseMessage;
    }

    @Override
    public void handlePacket(SResponseMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            message.request.processResponse(this.nbt);
            message.request.callback(this.nbt);
        });
        supplier.get().setPacketHandled(true);
    }

}
