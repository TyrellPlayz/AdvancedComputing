package com.tyrellplayz.tech_craft.data;

import com.tyrellplayz.tech_craft.TechCraft;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateGen extends BlockStateProvider {

    public BlockStateGen(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, TechCraft.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }
}
