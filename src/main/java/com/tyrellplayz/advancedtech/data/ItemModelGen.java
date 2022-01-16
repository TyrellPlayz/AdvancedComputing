package com.tyrellplayz.advancedtech.data;

import com.tyrellplayz.advancedtech.AdvancedTech;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGen extends ItemModelProvider {

    public ItemModelGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, AdvancedTech.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerItemModels();
        registerBlockModels();
    }

    private void registerItemModels() {

    }

    private void registerBlockModels() {

    }

    public void simpleItem(Item item) {
        getBuilder(item.getRegistryName().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0",modLoc("item/"+item.getRegistryName().getPath()));
    }

    public void simpleItem(Block block) {
        getBuilder(block.getRegistryName().getPath())
                .parent(new ModelFile.UncheckedModelFile(modLoc("block/"+block.getRegistryName().getPath())));
    }

    public void simpleTool(Item item) {
        getBuilder(item.getRegistryName().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0",modLoc("item/"+item.getRegistryName().getPath()));
    }

}
