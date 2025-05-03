package fr.wakfu.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class WakfuNetwork {
    public static final String PROTOCOL = "1";
    public static SimpleNetworkWrapper INSTANCE;

    public static void init() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("wakfu");
        int id = 0;

        // 1) SyncStatsMessage : serveur → client
        INSTANCE.registerMessage(
            SyncStatsMessage.Handler.class,
            SyncStatsMessage.class,
            id++,
            Side.CLIENT
        );

        // 2) UpdateStatsMessage : client → serveur
        INSTANCE.registerMessage(
        	UpdateStatsMessageHandler.class,
            UpdateStatsMessage.class,
            id++,
            Side.SERVER
        );

        // Enregistre ici d'autres messages si besoin
    }
}
