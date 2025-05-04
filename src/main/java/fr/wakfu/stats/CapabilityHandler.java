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

    /** 
     * Enregistre la capability pour les statistiques du joueur.
     * Doit être appelé une seule fois pendant le pré-initialisation.
     */
    public static void register() {
        if (registered) {
            return;  // déjà enregistré, on sort
        }
        registered = true;

        // Enregistrement de la capability
        CapabilityManager.INSTANCE.register(
            IPlayerStats.class,
            new PlayerStatsStorage(),
            PlayerStats::new
        );
        System.out.println("[WAKFU] Capability IPlayerStats enregistrée.");
    }

    /** 
     * Clone les statistiques du joueur lorsqu'il meurt et se recrée.
     * Cela permet de garder les mêmes stats entre la mort et la résurrection.
     */
    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        EntityPlayer oldPlayer = event.getOriginal();
        EntityPlayer newPlayer = event.getEntityPlayer();

        IPlayerStats oldStats = oldPlayer.getCapability(StatsProvider.PLAYER_STATS, null);
        IPlayerStats newStats = newPlayer.getCapability(StatsProvider.PLAYER_STATS, null);

        if (oldStats != null && newStats != null) {
            // Clonage des statistiques de base
            newStats.setForce(oldStats.getForce());
            newStats.setStamina(oldStats.getStamina());
            newStats.setWakfu(oldStats.getWakfu());
            newStats.setAgility(oldStats.getAgility());
            // Ajouter ici d'autres stats si nécessaire
        }
    }

    /** 
     * Attache la capability aux entités de type joueur.
     * Cela permet de lier les statistiques aux entités de type joueur (EntityPlayer).
     */
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof EntityPlayer)) return;

        // Attache la capability
        System.out.println("[WAKFU] Attachement de la capability PlayerStats.");
        event.addCapability(STATS_CAP, new StatsProvider());
    }

    /** 
     * Gère la régénération des stats à chaque tick du joueur.
     * Cela permet d'appliquer des effets de régénération pendant le jeu.
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;

        // Récupère les stats du joueur
        IPlayerStats stats = event.player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats == null) return;

        // Applique la régénération des statistiques
        ((PlayerStats) stats).tickRegen();

        // Si des synchronisations sont nécessaires avec le client, il faut les gérer ici.
    }
}
