package com.tyrellplayz.tech_craft.core.computer;

import com.tyrellplayz.tech_craft.TechCraft;
import com.tyrellplayz.tech_craft.api.task.Task;
import com.tyrellplayz.tech_craft.blockentity.ComputerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class UpdateSystemDataTask extends Task {

    private CompoundTag systemData;
    private BlockPos computerPos;

    public UpdateSystemDataTask() {
        super("update_system_data");
    }

    public UpdateSystemDataTask(CompoundTag systemData, BlockPos computerPos) {
        this();
        this.systemData = systemData;
        this.computerPos = computerPos;
    }

    public void prepareRequest(CompoundTag nbt) {
        nbt.putLong("ComputerPos", this.computerPos.asLong());
        nbt.put("SystemData", this.systemData);
        TechCraft.LOGGER.info("prepareRequest "+nbt);
    }

    public void processRequest(CompoundTag nbt, Level level, Player player) {
        TechCraft.LOGGER.info("processRequest "+nbt);

        BlockPos computerPos = BlockPos.of(nbt.getLong("ComputerPos"));
        BlockEntity tile = level.getBlockEntity(computerPos);
        if (tile instanceof ComputerBlockEntity computerTile) {
            computerTile.setSystemData(nbt.getCompound("SystemData"));
            this.setSuccessful();
        }

    }

    public void prepareResponse(CompoundTag nbt) {
    }

    public void processResponse(CompoundTag nbt) {
    }

}
