package fr.wakfu.network;

import fr.skill.PacketRequestSkills;
import fr.skill.network.PacketSelectRadialSkill;
import fr.skill.network.PacketSyncSkills;
import fr.wakfu.allies.network.PacketAllyResponse;
import fr.wakfu.allies.network.PacketOpenAllyGui;
import fr.wakfu.common.network.PacketRequestRaceSelection;
import fr.wakfu.common.network.PacketSetRace;
import fr.wakfu.common.network.SyncRaceCapability;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import test.PacketAnimationControl;

public class WakfuNetwork {
    public static final String CHANNEL = "wakfu_anim";
    public static final SimpleNetworkWrapper INSTANCE =
        NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

    // Compteur partagé pour tous les packets
    private static int packetId = 0;

    /** Appelée en preInit() de ta classe @Mod */
    public static void init() {
        // === Animation packets ===
        INSTANCE.registerMessage(
            PacketAnimationControl.ClientHandler.class,
            PacketAnimationControl.class,
            packetId++,
            Side.CLIENT
        );
        INSTANCE.registerMessage(
        		PacketAnimationControl.ClientHandler.class,
        		PacketAnimationControl.class,
        		packetId++,
        		Side.SERVER
        		);
        WakfuNetwork.INSTANCE.registerMessage(PacketSelectRadialSkill.class,
        		PacketSelectRadialSkill.Message.class, packetId++, Side.SERVER
        );


        // === Stat packets ===
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

        // === Race packets ===
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
        WakfuNetwork.INSTANCE.registerMessage(
        	PacketOpenAllyGui.Handler.class, 
        	PacketOpenAllyGui.class, packetId++, Side.CLIENT
        );
        WakfuNetwork.INSTANCE.registerMessage(
        		PacketAllyResponse.Handler.class, 
        		PacketAllyResponse.class, packetId++, Side.SERVER
        );
        
        INSTANCE.registerMessage(PacketSyncSkills.Handler.class,
        		PacketSyncSkills.class,
        		packetId++, Side.CLIENT
        );
        INSTANCE.registerMessage(PacketRequestSkills.Handler.class,
        		PacketRequestSkills.class,
        		packetId++, Side.SERVER
        );
        INSTANCE.registerMessage(fr.skill.network.PacketSyncSkills.Handler.class,
        		fr.skill.network.PacketSyncSkills.class, packetId++, Side.CLIENT
        );
        INSTANCE.registerMessage(fr.skill.network.PacketRequestSkills.Handler.class,
        		fr.skill.network.PacketRequestSkills.class, packetId++, Side.SERVER
        );
        INSTANCE.registerMessage(fr.skill.network.PacketUseSkill.Handler.class,
        		fr.skill.network.PacketUseSkill.class, packetId++, Side.SERVER
        );
   
   

     

        System.out.println("[Network] Total packets registered: " + packetId);
    }
    /** Helpers d’envoi */
    public static void sendToServer(PacketAnimationControl msg) {
        INSTANCE.sendToServer(msg);
    }

    public static void sendToAll(PacketAnimationControl msg) {
        INSTANCE.sendToAll(msg);
    }

    public static void sendTo(PacketAnimationControl msg, EntityPlayerMP player) {
        INSTANCE.sendTo(msg, player);
    }
}

