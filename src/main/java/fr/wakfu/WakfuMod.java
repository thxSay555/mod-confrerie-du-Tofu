package fr.wakfu;

import fr.wakfu.commands.CommandStat;
import fr.wakfu.commands.CommandWakfuLevel;
import fr.wakfu.common.capabilities.RaceCapability;
import fr.wakfu.items.GoultardItem;
import fr.wakfu.items.IopShieldItem;
import fr.wakfu.items.IopSword;
import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.proxy.CommonProxy;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
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
import test.AnimationManager;
import test.CommandAnimationTest;

@Mod(modid = WakfuMod.MODID, name = WakfuMod.NAME, version = WakfuMod.VERSION)
@Mod.EventBusSubscriber
public class WakfuMod {
    public static final String MODID   = "wakfu";
    public static final String NAME    = "Wakfu Mod";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "fr.wakfu.proxy.ClientProxy", serverSide = "fr.wakfu.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Item itemGoultardSword;
    public static Item itemiopshield;
    public static Item itemiopsword;
    public static final ToolMaterial goultard_MATERIAL = EnumHelper.addToolMaterial(
    	    "goultard", 
    	    1,       // Niveau de récolte (1 = pierre)
    	    2000,     // Durabilité
    	    0.0F,    // Vitesse de minage (inutile pour une épée)
    	    8.0F,    // Dégâts de base (à 0, car vous gérez tout via les attributs)
    	    10       // Enchantabilité
    	);
    public static final ToolMaterial iop_sword_MATERIAL = EnumHelper.addToolMaterial(
    		"iop_sword", 
    		1,       // Niveau de récolte (1 = pierre)
    		2000,     // Durabilité
    		0.0F,    // Vitesse de minage (inutile pour une épée)
    		5.0F,    // Dégâts de base (à 0, car vous gérez tout via les attributs)
    		10       // Enchantabilité
    		);

    public static AnimationManager animationManager;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        WakfuNetwork.init();
        RaceCapability.register();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        animationManager = new AnimationManager();
        // inject manager into proxy
        proxy.setAnimationManager(animationManager);
    }


    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandStat());
        event.registerServerCommand(new CommandWakfuLevel());
        event.registerServerCommand(new CommandAnimationTest());
    }
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        if(itemGoultardSword != null) {
            ModelLoader.setCustomModelResourceLocation(
                itemGoultardSword,
                0,
                new ModelResourceLocation(itemGoultardSword.getRegistryName(), "inventory")
            );
        }
        if(itemiopshield != null) {
        	ModelLoader.setCustomModelResourceLocation(
        			itemiopshield,
        			0,
        			new ModelResourceLocation(itemiopshield.getRegistryName(), "inventory")
        			);
        }
        if(itemiopsword != null) {
        	ModelLoader.setCustomModelResourceLocation(
        			itemiopsword,
        			0,
        			new ModelResourceLocation(itemiopsword.getRegistryName(), "inventory")
        			);
        }
    }


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        itemGoultardSword = new GoultardItem();
        event.getRegistry().register(itemGoultardSword);
        
        itemiopshield = new IopShieldItem();
        event.getRegistry().register(itemiopshield);
        
        itemiopsword = new IopSword();
        event.getRegistry().register(itemiopsword);
    }

    public static boolean hasActiveAnimation(EntityPlayer player) {
        return animationManager != null
            && animationManager.getInstance(player.getName()) != null;
    }
}