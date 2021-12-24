package com.tyrellplayz.advancedtechnology;

import com.tyrellplayz.advancedtechnology.core.ATBlockEntities;
import com.tyrellplayz.advancedtechnology.core.ATBlocks;
import com.tyrellplayz.advancedtechnology.core.ATItems;
import com.tyrellplayz.advancedtechnology.data.BlockStateGen;
import com.tyrellplayz.advancedtechnology.data.ItemModelGen;
import com.tyrellplayz.advancedtechnology.data.LootTableGen;
import com.tyrellplayz.advancedtechnology.data.RecipeGen;
import com.tyrellplayz.advancedtechnology.proxy.ClientProxy;
import com.tyrellplayz.advancedtechnology.proxy.CommonProxy;
import com.tyrellplayz.advancedtechnology.proxy.ModProxy;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AdvancedTechnology.MOD_ID)
public class AdvancedTechnology {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "advanced_technology";

    public static ModProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
        public ItemStack makeIcon() { return new ItemStack(Blocks.IRON_BLOCK); }
    };

    public AdvancedTechnology() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::gatherData);

        ATBlocks.REGISTER.register(eventBus);
        ATItems.REGISTER.register(eventBus);
        ATBlockEntities.REGISTER.register(eventBus);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    public void onCommonSetup(final FMLCommonSetupEvent event) {
        proxy.onCommonSetup(event);
    }

    public void onClientSetup(final FMLClientSetupEvent event) {
        proxy.onClientSetup(event);
    }

    public void gatherData(final GatherDataEvent dataEvent) {
        DataGenerator generator = dataEvent.getGenerator();
        if(dataEvent.includeClient()) {
            generator.addProvider(new BlockStateGen(generator,dataEvent.getExistingFileHelper()));
            generator.addProvider(new ItemModelGen(generator,dataEvent.getExistingFileHelper()));
        }
        if(dataEvent.includeServer()) {
            generator.addProvider(new LootTableGen(generator));
            generator.addProvider(new RecipeGen(generator));
        }
    }

    public static Logger getLogger() {
        return LOGGER;
    }

}
