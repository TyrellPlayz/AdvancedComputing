package com.tyrellplayz.tech_craft.block;

import com.tyrellplayz.tech_craft.blockentity.ComputerBlockEntity;
import com.tyrellplayz.tech_craft.core.TCBlockEntities;
import com.tyrellplayz.zlib.util.ServerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ComputerBlock extends BaseEntityBlock {

    public static final BooleanProperty ON = BooleanProperty.create("on");

    public ComputerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ON, Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ON);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ComputerBlockEntity(pos,state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (ServerUtil.isServerLevel(level)) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof ComputerBlockEntity computerBlockEntity) {
                computerBlockEntity.openScreen(player);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return createTickerHelper(type, TCBlockEntities.COMPUTER.get(),ComputerBlockEntity::clientTick);
        return createTickerHelper(type, TCBlockEntities.COMPUTER.get(), ComputerBlockEntity::serverTick);
    }

}
