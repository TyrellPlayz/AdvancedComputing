package com.tyrellplayz.advancedtechnology.core;

import com.tyrellplayz.advancedtechnology.AdvancedTechnology;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ATItems {

    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, AdvancedTechnology.MOD_ID);

    public static <T extends Item> RegistryObject<T> register(String registryName, T item) {
        return REGISTER.register(registryName,() -> item);
    }

}
