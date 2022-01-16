package com.tyrellplayz.advancedtech.core;

import com.tyrellplayz.advancedtech.AdvancedTech;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ATItems {

    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, AdvancedTech.MOD_ID);

    public static <T extends Item> RegistryObject<T> register(String registryName, T item) {
        return REGISTER.register(registryName,() -> item);
    }

}
