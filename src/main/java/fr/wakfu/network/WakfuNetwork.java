package fr.wakfu.network;

import fr.wakfu.network.WakfuNetwork;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class WakfuNetwork {
    public static final String PROTOCOL_VERSION = "1";
    public static SimpleNetworkWrapper INSTANCE;

 // dans WakfuNetwork.java

    public static void init() {
        INSTANCE = NetworkRegistry.INSTANCE
            .newSimpleChannel("wakfu_main");  // juste un nom de canal unique

        INSTANCE.registerMessage(
            SyncStatsMessage.Handler.class,
            SyncStatsMessage.class,
            0,
            Side.CLIENT
        );
    }

    }

