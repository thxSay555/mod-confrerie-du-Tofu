package fr.wakfu;

import fr.wakfu.commands.CommandStat;
import fr.wakfu.commands.CommandWakfuLevel;
import fr.wakfu.common.capabilities.RaceCapability;
import fr.wakfu.items.GoultardItem;
import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import test.AnimationManager;
import test.CommandAnimationTest;

/**
 * Main mod class combining Wakfu core and Wakfu Animation subsystems.
 */
@Mod(modid = WakfuMod.MODID, name = WakfuMod.NAME, version = WakfuMod.VERSION)
@Mod.EventBusSubscriber
public class WakfuMod {
    public static final String MODID   = "wakfu";
    public static final String NAME    = "Wakfu Mod";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "fr.wakfu.proxy.ClientProxy",
                serverSide = "fr.wakfu.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Item itemGoultardSword;
    public static final ToolMaterial TOFU_MATERIAL =
        EnumHelper.addToolMaterial("TOFU", 1, 100, 4.0F, 12/5.0F, 10);

    /** Animation system manager (client + server instances). */
    public static AnimationManager animationManager;

    // ----------------------
    // Pre-initialization
    // ----------------------
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        WakfuNetwork.init();
        RaceCapability.register();
        proxy.preInit(event);
    }

    // ----------------------
    // Initialization
    // ----------------------
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        animationManager = new AnimationManager();
    }

    // ----------------------
    // Server starting: register commands
    // ----------------------
    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.init();
        event.registerServerCommand(new CommandStat());
        event.registerServerCommand(new CommandWakfuLevel());
        event.registerServerCommand(new CommandAnimationTest());
    }

    // ----------------------
    // Item registration
    // ----------------------
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        itemGoultardSword = new GoultardItem();
        event.getRegistry().register(itemGoultardSword);
    }

    /**
     * Utilisée par l’ASM ou Obfuscate pour savoir si un joueur
     * a une animation en cours.
     */
    public static boolean hasActiveAnimation(EntityPlayer player) {
        return animationManager != null
            && animationManager.getInstance(player.getName()) != null;
    }
}
