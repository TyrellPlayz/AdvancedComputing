package com.tyrellplayz.tech_craft.core;

import com.tyrellplayz.tech_craft.AdvancedComputing;
import com.tyrellplayz.tech_craft.block.ComputerBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class TCBlocks {

    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancedComputing.MOD_ID);

    public static final RegistryObject<Block> COMPUTER = register("computer", () -> new ComputerBlock(BlockBehaviour.Properties.of(Material.METAL)), TCBlocks::tabBlockItem);

    public static <T extends Block> RegistryObject<T> register(String registryName, Supplier<T> blockSupplier, Function<T,BlockItem> blockItemFunction) {
        RegistryObject<T> registryObject = REGISTER.register(registryName,blockSupplier);
        if(blockItemFunction != null) {
            TCItems.REGISTER.register(registryName,() -> blockItemFunction.apply(registryObject.get()));
        }
        return registryObject;
    }

    private static BlockItem simpleBlockItem(Block block) {
        return new BlockItem(block,new Item.Properties());
    }

    private static BlockItem tabBlockItem(Block block) {
        return new BlockItem(block,new Item.Properties().tab(AdvancedComputing.TAB));
    }

}
