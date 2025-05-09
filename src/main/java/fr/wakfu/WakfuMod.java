package fr.wakfu;

import fr.wakfu.commands.CommandStat;
import fr.wakfu.commands.CommandWakfuLevel;
import fr.wakfu.items.GoultardItem;
import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.proxy.CommonProxy;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = WakfuMod.MODID, name = WakfuMod.NAME, version = WakfuMod.VERSION)
@Mod.EventBusSubscriber
public class WakfuMod {
    public static final String MODID = "wakfu";
    public static final String NAME = "Wakfu Mod";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "fr.wakfu.proxy.ClientProxy", serverSide = "fr.wakfu.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Item itemGoultardSword;
    public static final ToolMaterial TOFU_MATERIAL = EnumHelper.addToolMaterial(
    	    "TOFU", 
    	    1,       // Niveau de récolte (1 = pierre)
    	    100000,     // Durabilité
    	    0.0F,    // Vitesse de minage (inutile pour une épée)
    	    0.0F,    // Dégâts de base (à 0, car vous gérez tout via les attributs)
    	    10       // Enchantabilité
    	);
    
    

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Capability registration moved to CommonProxy.preInit
        WakfuNetwork.init();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // recipes, compatibility
    }
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        // Vérifiez que l'item existe avant de l'enregistrer
        if(itemGoultardSword != null) {
            ModelLoader.setCustomModelResourceLocation(
                itemGoultardSword,
                0,
                new ModelResourceLocation(itemGoultardSword.getRegistryName(), "inventory")
            );
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.init();
        event.registerServerCommand(new CommandStat());
        event.registerServerCommand(new CommandWakfuLevel());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        itemGoultardSword = new GoultardItem();
        event.getRegistry().register(itemGoultardSword);
    }
}