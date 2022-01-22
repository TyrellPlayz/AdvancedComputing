package com.tyrellplayz.tech_craft.core;

import com.tyrellplayz.tech_craft.TechCraft;
import com.tyrellplayz.tech_craft.blockentity.ComputerBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class TCBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, TechCraft.MOD_ID);

    public static final RegistryObject<BlockEntityType<ComputerBlockEntity>> COMPUTER = register("computer", ComputerBlockEntity::new, () -> new Block[]{TCBlocks.COMPUTER.get()});

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String registryName, BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<Block[]> validBlocksSupplier) {
        return REGISTER.register(registryName, () -> BlockEntityType.Builder.of(supplier,validBlocksSupplier.get()).build(null));
    }

}
