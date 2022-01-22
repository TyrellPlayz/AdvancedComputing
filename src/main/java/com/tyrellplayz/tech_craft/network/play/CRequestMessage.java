package com.tyrellplayz.tech_craft.network.play;

import com.tyrellplayz.tech_craft.api.task.Task;
import com.tyrellplayz.tech_craft.manager.TaskManager;
import com.tyrellplayz.zlib.network.message.PlayMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CRequestMessage extends PlayMessage<CRequestMessage> {

    private int id;
    private Task request;
    private CompoundTag nbt;

    public CRequestMessage() {
    }

    public CRequestMessage(int id, Task request) {
        this();
        this.id = id;
        this.request = request;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void writePacket(CRequestMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.id);
        buf.writeUtf(message.request.getName());
        CompoundTag nbt = new CompoundTag();
        message.request.prepareRequest(nbt);
        buf.writeNbt(nbt);
    }

    @Override
    public CRequestMessage readPacket(FriendlyByteBuf buf) {
        CRequestMessage requestMessage = new CRequestMessage();
        requestMessage.id = buf.readInt();
        String name = buf.readUtf(256);
        requestMessage.request = TaskManager.get().getTask(name);
        requestMessage.nbt = buf.readNbt();
        return requestMessage;
    }

    @Override
    public void handlePacket(CRequestMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> message.request.processRequest(message.nbt, supplier.get().getSender().level, supplier.get().getSender()));
        supplier.get().setPacketHandled(true);
    }

}
