package fr.wakfu.stats;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CapabilityHandler {

    // Clé pour l’attachement
    public static final ResourceLocation STATS_CAP = new ResourceLocation("wakfu", "player_stats");

    // Garde statique pour empêcher le double-enregistrement
    private static boolean registered = false;

    /** Doit être appelé une seule fois, depuis preInit() */
    public static void register() {
        if (registered) {
            return;  // déjà enregistré, on sort
        }
        registered = true;

        CapabilityManager.INSTANCE.register(
            IPlayerStats.class,
            new PlayerStatsStorage(),
            PlayerStats::new
        );
        System.out.println("[WAKFU] Capability IPlayerStats enregistrée.");
    }

    /** Clonage des stats à la mort */
    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        EntityPlayer oldPlayer = event.getOriginal();
        EntityPlayer newPlayer = event.getEntityPlayer();

        IPlayerStats oldStats = oldPlayer.getCapability(StatsProvider.PLAYER_STATS, null);
        IPlayerStats newStats = newPlayer.getCapability(StatsProvider.PLAYER_STATS, null);

        if (oldStats != null && newStats != null) {
            newStats.setForce(oldStats.getForce());
            newStats.setStamina(oldStats.getStamina());
            newStats.setWakfu(oldStats.getWakfu());
            newStats.setAgility(oldStats.getAgility());
        }
    }

    /** Attache la capability au joueur */
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof EntityPlayer)) return;

        System.out.println("[WAKFU] Attachement de la capability PlayerStats.");
        event.addCapability(STATS_CAP, new StatsProvider());
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;
        IPlayerStats stats = event.player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats == null) return;
        // Applique la regen multipliée
        ((PlayerStats) stats).tickRegen();
        // synchroniser si besoin...
    }
}
