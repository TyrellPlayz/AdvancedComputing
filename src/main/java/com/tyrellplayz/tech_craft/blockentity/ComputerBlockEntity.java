package com.tyrellplayz.tech_craft.blockentity;

import com.tyrellplayz.tech_craft.TechCraft;
import com.tyrellplayz.tech_craft.api.system.System;
import com.tyrellplayz.tech_craft.core.computer.ClientComputer;
import com.tyrellplayz.tech_craft.core.TCBlockEntities;
import com.tyrellplayz.zlib.blockentity.SyncBlockEntity;
import com.tyrellplayz.zlib.util.PlayerUtil;
import com.tyrellplayz.zlib.util.ServerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ComputerBlockEntity extends SyncBlockEntity {

    private System system;
    public boolean running = false;
    private CompoundTag systemData;

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.COMPUTER.get(), pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean openScreen(Player player) {
        if(ServerUtil.isServerLevel(level)) return false;
        if(system == null) {
            system = new ClientComputer(this);
            start();
        }
        PlayerUtil.openScreen(player,(ClientComputer)system);
        return true;
    }

    public void onLoad() {
        super.onLoad();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void start() {
        if(isRunning()) return;
        setRunning(true);
        if(!ServerUtil.isServerLevel(this.level)) {
            //TODO: Create a new system here.
        }
        system.onStartup();
    }

    public void shutDown() {
        if(!isRunning()) return;
        system.onShutdown();
        system = null;
        setRunning(false);
    }

    public CompoundTag getSystemData() {
        if(systemData == null) this.systemData = new CompoundTag();
        return systemData;
    }

    public void setSystemData(@NotNull CompoundTag systemData) {
        Objects.requireNonNull(systemData, "SystemData cannot be null");
        this.systemData = systemData;
        setChanged();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.shutDown();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        TechCraft.LOGGER.info("load "+tag.getCompound("SystemData"));
        setSystemData(tag.getCompound("SystemData"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        TechCraft.LOGGER.info("saveAdditional "+getSystemData());
        tag.put("SystemData",getSystemData());
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ComputerBlockEntity entity) {
        if (entity.system != null && entity.running) {
            entity.system.tick();
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ComputerBlockEntity entity) {

    }

}
