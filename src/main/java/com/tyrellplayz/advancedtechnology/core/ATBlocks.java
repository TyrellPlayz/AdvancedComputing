package com.tyrellplayz.advancedtechnology.core;

import com.tyrellplayz.advancedtechnology.AdvancedTechnology;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class ATBlocks {

    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancedTechnology.MOD_ID);



    private static <T extends Block> RegistryObject<T> register(String registryName, T block, Function<Block, BlockItem> blockItemFunction) {
        if(blockItemFunction != null) ATItems.REGISTER.register(registryName,() -> blockItemFunction.apply(block));
        return REGISTER.register(registryName,() -> block);
    }

    private static BlockItem simpleBlockItem(Block block) {
        return new BlockItem(block,new Item.Properties());
    }

    private static BlockItem tabBlockItem(Block block) {
        return new BlockItem(block,new Item.Properties().tab(AdvancedTechnology.TAB));
    }

}
