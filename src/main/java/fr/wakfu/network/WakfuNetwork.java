// WakfuNetwork.java
package fr.wakfu.network;

import fr.wakfu.common.network.PacketRequestRaceSelection;
import fr.wakfu.common.network.PacketSetRace;
import fr.wakfu.common.network.SyncRaceCapability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class WakfuNetwork {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("wakfu");
    private static int packetId = 0;

    public static void init() {
        // Enregistrement des messages STATS
        INSTANCE.registerMessage(
            SyncStatsMessage.Handler.class,
            SyncStatsMessage.class,
            packetId++,
            Side.CLIENT
        );

        INSTANCE.registerMessage(
            UpdateStatsMessage.Handler.class,
            UpdateStatsMessage.class,
            packetId++,
            Side.SERVER
        );

        // Enregistrement du message RACE
        INSTANCE.registerMessage(
            PacketSetRace.Handler.class,
            PacketSetRace.class,
            packetId++,
            Side.SERVER
        );
        INSTANCE.registerMessage(
                SyncRaceCapability.Handler.class,
                SyncRaceCapability.class,
                packetId++,
                Side.CLIENT
            );
       
     

  

        INSTANCE.registerMessage(
                PacketRequestRaceSelection.Handler.class,
                PacketRequestRaceSelection.class,
                packetId++,
                Side.CLIENT
            );
        

            System.out.println("[Network] Paquet PacketRequestRaceSelection enregistré (ID: " + (packetId - 1) + ")");
        }
}
