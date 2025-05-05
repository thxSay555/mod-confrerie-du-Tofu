package fr.wakfu.stats;

import fr.wakfu.network.SyncStatsMessage;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class StatsCapabilityHandler {

    // L'identifiant unique de la capability du joueur
    public static final ResourceLocation PLAYER_STATS_ID = new ResourceLocation("wakfu", "player_stats");

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        // On vérifie que l'entité est bien un joueur
        if (event.getObject() instanceof EntityPlayer) {
            // On s'assure que la capability n'est pas déjà attachée
            if (!event.getCapabilities().containsKey(PLAYER_STATS_ID)) {
                event.addCapability(PLAYER_STATS_ID, new StatsProvider());
            }
        }
    }
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncStats(event.player);
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncStats(event.player);
    }

    private void syncStats(EntityPlayer player) {
        if (!player.world.isRemote) { // Côté serveur uniquement
            IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
            if (stats != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("Level", stats.getLevel());
                tag.setInteger("Xp", stats.getXp());
                tag.setInteger("Force", stats.getForce());
                tag.setInteger("Stamina", stats.getStamina());
                tag.setInteger("Wakfu", stats.getWakfu());
                tag.setInteger("Agility", stats.getAgility());
                tag.setInteger("SkillPoints", stats.getSkillPoints());
                tag.setInteger("XpToNext", stats.getXpToNextLevel());
                tag.setInteger("Intensity", stats.getIntensity());
                tag.setFloat("CurrentWakfu", stats.getCurrentWakfu());
                tag.setFloat("CurrentStamina", stats.getCurrentStamina());
               
               
                // ... autres données ...
                WakfuNetwork.INSTANCE.sendTo(new SyncStatsMessage(tag), (EntityPlayerMP) player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(Clone event) {

        IPlayerStats oldStats = event.getOriginal().getCapability(StatsProvider.PLAYER_STATS, null);
        IPlayerStats newStats = event.getEntityPlayer().getCapability(StatsProvider.PLAYER_STATS, null);
        if (oldStats == null || newStats == null) return;

        // Copie des valeurs de stats de l'ancien joueur vers le nouveau
        newStats.setLevel(oldStats.getLevel());
        newStats.setSkillPoints(oldStats.getSkillPoints());
        newStats.setXp(oldStats.getXp());
        newStats.setXpToNextLevel(oldStats.getXpToNextLevel());
        newStats.setForce(oldStats.getForce());
        newStats.setStamina(oldStats.getStamina());
        newStats.setWakfu(oldStats.getWakfu());
        newStats.setAgility(oldStats.getAgility());
        newStats.setIntensity(oldStats.getIntensity());
        newStats.setCurrentWakfu(oldStats.getCurrentWakfu());
        newStats.setCurrentStamina(oldStats.getCurrentStamina());
           // AJOUT du deserializeNBT
        newStats.deserializeNBT(oldStats.serializeNBT());
    }
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(
            IPlayerStats.class, 
            new StatsStorage(), 
            PlayerStats::new
        );
    }

    // Récupérer la logique de tick
 // StatsCapabilityHandler.java
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;
        IPlayerStats stats = event.player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats != null) stats.tickRegen(); // Utilise la méthode de l'interface
    }
}
