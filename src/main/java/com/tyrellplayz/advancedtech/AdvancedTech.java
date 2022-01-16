package com.tyrellplayz.advancedtech;

import com.tyrellplayz.advancedtech.api.content.application.ApplicationType;
import com.tyrellplayz.advancedtech.api.system.filesystem.FileExtension;
import com.tyrellplayz.advancedtech.api.system.filesystem.FileExtensionManager;
import com.tyrellplayz.advancedtech.core.ATBlockEntities;
import com.tyrellplayz.advancedtech.core.ATBlocks;
import com.tyrellplayz.advancedtech.core.ATItems;
import com.tyrellplayz.advancedtech.core.computer.UpdateSystemDataTask;
import com.tyrellplayz.advancedtech.data.BlockStateGen;
import com.tyrellplayz.advancedtech.data.ItemModelGen;
import com.tyrellplayz.advancedtech.data.LootTableGen;
import com.tyrellplayz.advancedtech.data.RecipeGen;
import com.tyrellplayz.advancedtech.manager.ApplicationManager;
import com.tyrellplayz.advancedtech.manager.TaskManager;
import com.tyrellplayz.advancedtech.network.handshake.SUpdateApplicationDataHandshake;
import com.tyrellplayz.advancedtech.network.play.CRequestMessage;
import com.tyrellplayz.advancedtech.network.play.SResponseMessage;
import com.tyrellplayz.advancedtech.network.play.SUpdateApplicationDataMessage;
import com.tyrellplayz.advancedtech.proxy.ClientProxy;
import com.tyrellplayz.advancedtech.proxy.CommonProxy;
import com.tyrellplayz.zlib.network.NetworkManager;
import com.tyrellplayz.zlib.proxy.ModProxy;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AdvancedTech.MOD_ID)
public class AdvancedTech {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "advanced_tech";

    public static ModProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
        public ItemStack makeIcon() { return new ItemStack(Blocks.IRON_BLOCK); }
    };

    public static final NetworkManager NETWORK = new NetworkManager(MOD_ID,"1");
    private static ApplicationManager applicationManager;

    public AdvancedTech() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::gatherData);
        eventBus.addListener(this::registerRegistries);

        ATBlocks.REGISTER.register(eventBus);
        ATItems.REGISTER.register(eventBus);
        ATBlockEntities.REGISTER.register(eventBus);
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(this);


        applicationManager = new ApplicationManager();

        NETWORK.registerPlayMessage(CRequestMessage.class, NetworkDirection.PLAY_TO_SERVER);
        NETWORK.registerPlayMessage(SResponseMessage.class, NetworkDirection.PLAY_TO_CLIENT);
        NETWORK.registerPlayMessage(SUpdateApplicationDataMessage.class, NetworkDirection.PLAY_TO_CLIENT);
        NETWORK.registerHandshakeMessage(SUpdateApplicationDataHandshake.class);

        TaskManager.get().registerTask(UpdateSystemDataTask.class);

        FileExtensionManager.get().registerExtension(new FileExtension("note",new ResourceLocation(MOD_ID,"note")));
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    public void onCommonSetup(final FMLCommonSetupEvent event) {
        proxy.onCommonSetup(event);
    }

    public void onClientSetup(final FMLClientSetupEvent event) {
        proxy.onClientSetup(event);
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        applicationManager = null;
    }

    @SubscribeEvent
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

    @SubscribeEvent
    public void addReloadListenerEvent(AddReloadListenerEvent event) {
        ApplicationManager applicationManager = new ApplicationManager();
        event.addListener(applicationManager);
        AdvancedTech.applicationManager = applicationManager;
    }

    @SubscribeEvent
    public void registerRegistries(RegistryEvent.NewRegistry event) {
        createRegistry(new ResourceLocation(MOD_ID,"application"), ApplicationType.class);
    }

    public <T extends IForgeRegistryEntry<T>> void createRegistry(ResourceLocation key, Class<T> type) {
        new RegistryBuilder<T>().setName(key).setType(type).setDefaultKey(key).create();
    }

    public static ApplicationManager getApplicationManager() {
        return applicationManager;
    }
}
