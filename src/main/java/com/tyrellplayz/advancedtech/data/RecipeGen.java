package com.tyrellplayz.advancedtech.data;

import com.tyrellplayz.advancedtech.AdvancedTech;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class RecipeGen extends RecipeProvider {

    public RecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

    }

    private ResourceLocation modLoc(String path) {
        return new ResourceLocation(AdvancedTech.MOD_ID,path);
    }

    private String simpleItemName(ItemLike item) {
        return item.asItem().getRegistryName().getPath();
    }

}
