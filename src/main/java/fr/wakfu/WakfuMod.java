package fr.wakfu;

import fr.wakfu.commands.CommandStat;
import fr.wakfu.items.GoultardItem;
import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.proxy.CommonProxy;
import net.minecraft.item.Item;
import fr.wakfu.stats.CapabilityHandler;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = WakfuMod.MODID, name = WakfuMod.NAME, version = WakfuMod.VERSION)

@EventBusSubscriber
public class WakfuMod {

    public static final String MODID = "wakfu";
    public static final String NAME = "Wakfu Mod";
    public static final String VERSION = "1.0";

    public static Item itemTofuSword;

    public static final ToolMaterial TOFU_MATERIAL = EnumHelper.addToolMaterial("TOFU", 1, 100, 4.0F, 1.0F, 10);
    @SidedProxy(clientSide="fr.wakfu.proxy.ClientProxy", serverSide="fr.wakfu.proxy.CommonProxy")
    public static CommonProxy proxy;

    // ...
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // (Optionnel) Logs pour confirmer
        System.out.println("[WAKFU] Capabilities enregistrées avec succès.");
        // Préparation de ressources ou de config si besoin
        CapabilityHandler.register();
        MinecraftForge.EVENT_BUS.register(CapabilityHandler.class);
        WakfuNetwork.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	
        // Recettes, compatibilité, proxy...
    }
    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    	proxy.init();
        event.registerServerCommand(new CommandStat());
        event.registerServerCommand(new CommandStat());
        
    }

    
    public static Item itemGoultardSword; // Nom cohérent

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        itemGoultardSword = new GoultardItem();
        event.getRegistry().register(itemGoultardSword);
    }
}
