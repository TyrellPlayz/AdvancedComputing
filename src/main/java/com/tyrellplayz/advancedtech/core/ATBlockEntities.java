package com.tyrellplayz.advancedtech.core;

import com.tyrellplayz.advancedtech.AdvancedTech;
import com.tyrellplayz.advancedtech.blockentity.ComputerBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ATBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, AdvancedTech.MOD_ID);

    public static final RegistryObject<BlockEntityType<ComputerBlockEntity>> COMPUTER = register("computer", ComputerBlockEntity::new, () -> new Block[]{ATBlocks.COMPUTER.get()});

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String registryName, BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<Block[]> validBlocksSupplier) {
        return REGISTER.register(registryName, () -> BlockEntityType.Builder.of(supplier,validBlocksSupplier.get()).build(null));
    }

}
