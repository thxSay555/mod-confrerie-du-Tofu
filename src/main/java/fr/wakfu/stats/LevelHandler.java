package fr.wakfu.stats;

import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.network.SyncStatsMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LevelHandler {

    // Cache pour éviter les synchronisations inutiles
    private final Map<UUID, PlayerSyncData> lastSyncedData = new HashMap<>();

    // Classe interne pour stocker les données de synchronisation
    private static class PlayerSyncData {
        int xp;
        int level;
        boolean leveledUp;

        PlayerSyncData(int xp, int level, boolean leveledUp) {
            this.xp = xp;
            this.level = level;
            this.leveledUp = leveledUp;
        }
    }

    /** Calcul du besoin en XP (base 50, +10% par niveau) */
    public static int getXpForNextLevel(int currentLevel) {
        return (int) Math.round(50 * Math.pow(1.1, currentLevel - 1));
    }
 // LevelHandler.java (additions)
    @SubscribeEvent
    public void onExperienceDrop(LivingExperienceDropEvent event) {
        if (!(event.getAttackingPlayer() instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getAttackingPlayer();
        IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats != null) stats.setXp(stats.getXp() + event.getDroppedExperience());
    }

    @SubscribeEvent
    public void onXpPickup(PlayerPickupXpEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats != null) stats.setXp(stats.getXp() + event.getOrb().xpValue);
    }

    @SubscribeEvent
    public void onXpCommand(CommandEvent event) {
        if (!event.getCommand().getName().equalsIgnoreCase("xp")) return;
        if (event.getParameters().length < 1) return;
        
        try {
            int amount = Integer.parseInt(event.getParameters()[0].toString());
            if (event.getSender() instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) event.getSender();
                IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
                if (stats != null) stats.setXp(stats.getXp() + amount);
            }
        } catch (NumberFormatException ignored) {}
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || 
            event.player.world.isRemote || 
            !(event.player instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats == null) return;

        UUID uuid = player.getUniqueID();
        int currentXp = stats.getXp();
        int currentLevel = stats.getLevel();
        boolean leveledUp = false;

        // Gestion de la montée de niveau
        int xpToNext = stats.getXpToNextLevel();
        while (currentXp >= xpToNext) {
            currentXp -= xpToNext;
            currentLevel++;
            stats.setLevel(currentLevel);
            stats.addSkillPoints(5);
            leveledUp = true;
            xpToNext = getXpForNextLevel(currentLevel);
            stats.setXpToNextLevel(xpToNext);
            
            player.sendMessage(new TextComponentString(
                "§aBravo ! Niveau " + currentLevel + " (+5 SP)."
            ));
        }
        
        if (leveledUp || currentXp != stats.getXp()) {
            stats.setXp(currentXp);
        }

        // Vérification des changements nécessitant synchronisation
        PlayerSyncData lastData = lastSyncedData.get(uuid);
        if (needsSync(lastData, currentXp, currentLevel, leveledUp)) {
            syncPlayerData(player, stats, currentXp, currentLevel);
            lastSyncedData.put(uuid, new PlayerSyncData(currentXp, currentLevel, leveledUp));
        }
    }

    private boolean needsSync(PlayerSyncData lastData, int currentXp, int currentLevel, boolean leveledUp) {
        return lastData == null || 
               currentXp != lastData.xp || 
               currentLevel != lastData.level || 
               leveledUp;
    }

    private void syncPlayerData(EntityPlayerMP player, IPlayerStats stats, int xp, int level) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Level", level);
        tag.setInteger("SkillPoints", stats.getSkillPoints());
        tag.setInteger("Xp", xp);
        tag.setInteger("XpToNext", stats.getXpToNextLevel());
        tag.setInteger("Force", stats.getForce());
        tag.setInteger("Stamina", stats.getStamina());
        tag.setInteger("Wakfu", stats.getWakfu());
        tag.setInteger("Agility", stats.getAgility());
        tag.setInteger("Intensity", stats.getIntensity()); // Ajout de l'intensité
        tag.setFloat("CurrentWakfu", stats.getCurrentWakfu());
        tag.setFloat("CurrentStamina", stats.getCurrentStamina());
        
        WakfuNetwork.INSTANCE.sendTo(new SyncStatsMessage(tag), player);
    }
}