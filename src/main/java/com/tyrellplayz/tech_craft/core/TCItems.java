package com.tyrellplayz.tech_craft.core;

import com.tyrellplayz.tech_craft.AdvancedComputing;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class TCItems {

    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, AdvancedComputing.MOD_ID);

    public static <T extends Item> RegistryObject<T> register(String registryName, Supplier<T> itemSupplier) {
        return REGISTER.register(registryName,itemSupplier);
    }

}
