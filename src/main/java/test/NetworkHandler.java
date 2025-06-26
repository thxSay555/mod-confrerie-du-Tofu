package test;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber
public class NetworkHandler {
    public static final String CHANNEL = "wakfu_anim";
    public static SimpleNetworkWrapper INSTANCE;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
        // id 0 pour START/STOP
        INSTANCE.registerMessage(
            PacketAnimationControl.ClientHandler.class,
            PacketAnimationControl.class,
            0,
            Side.CLIENT
        );
    }
}