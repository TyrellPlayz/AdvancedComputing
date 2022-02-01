package com.tyrellplayz.tech_craft;

import com.tyrellplayz.tech_craft.api.system.filesystem.FileExtension;
import com.tyrellplayz.tech_craft.manager.FileExtensionManager;
import com.tyrellplayz.tech_craft.core.ApplicationType;
import com.tyrellplayz.tech_craft.core.TCBlockEntities;
import com.tyrellplayz.tech_craft.core.TCBlocks;
import com.tyrellplayz.tech_craft.core.TCItems;
import com.tyrellplayz.tech_craft.core.computer.UpdateSystemDataTask;
import com.tyrellplayz.tech_craft.data.BlockStateGen;
import com.tyrellplayz.tech_craft.data.ItemModelGen;
import com.tyrellplayz.tech_craft.data.LootTableGen;
import com.tyrellplayz.tech_craft.data.RecipeGen;
import com.tyrellplayz.tech_craft.manager.ApplicationManager;
import com.tyrellplayz.tech_craft.manager.TaskManager;
import com.tyrellplayz.tech_craft.network.handshake.ClientboundManifestHandshake;
import com.tyrellplayz.tech_craft.network.play.CRequestMessage;
import com.tyrellplayz.tech_craft.network.play.ClientboundUpdateManifestPacket;
import com.tyrellplayz.tech_craft.network.play.SResponseMessage;
import com.tyrellplayz.tech_craft.proxy.ClientProxy;
import com.tyrellplayz.tech_craft.proxy.CommonProxy;
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

@Mod(TechCraft.MOD_ID)
public class TechCraft {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "tech_craft";

    public static ModProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
        public ItemStack makeIcon() { return new ItemStack(Blocks.IRON_BLOCK); }
    };

    public static final NetworkManager NETWORK = new NetworkManager(MOD_ID,"1");
    private static ApplicationManager applicationManager;

    public TechCraft() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::gatherData);
        eventBus.addListener(this::registerRegistries);

        TCBlocks.REGISTER.register(eventBus);
        TCItems.REGISTER.register(eventBus);
        TCBlockEntities.REGISTER.register(eventBus);
        eventBus.register(ApplicationType.class);

        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(this);


        applicationManager = new ApplicationManager();

        NETWORK.registerPlayMessage(CRequestMessage.class, NetworkDirection.PLAY_TO_SERVER);
        NETWORK.registerPlayMessage(SResponseMessage.class, NetworkDirection.PLAY_TO_CLIENT);
        NETWORK.registerPlayMessage(ClientboundUpdateManifestPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        NETWORK.registerHandshakeMessage(ClientboundManifestHandshake.class);

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
        TechCraft.applicationManager = applicationManager;
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
