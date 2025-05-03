package fr.wakfu.stats;

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

    // Cache pour envoi conditionnel
    private final Map<UUID, Integer> lastSyncedXp = new HashMap<>();
    private final Map<UUID, Integer> lastSyncedLevel = new HashMap<>();

    /** Calcul du besoin en XP (base 50, +10% par niveau) */
    public static int getXpForNextLevel(int currentLevel) {
        double base = 50 * Math.pow(1.1, currentLevel - 1);
        return (int) Math.round(base);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent ev) {
        if (ev.phase != TickEvent.Phase.END || ev.player.world.isRemote
         || !(ev.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) ev.player;
        IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats == null) return;

        UUID uuid = player.getUniqueID();
        int currentXp = stats.getXp();
        int currentLevel = stats.getLevel();

        // Montée de niveau si xp dépasse le palier
        boolean leveled = false;
        int xpToNext = stats.getXpToNextLevel();
        while (currentXp >= xpToNext) {
            currentXp -= xpToNext;
            currentLevel++;
            stats.setLevel(currentLevel);
            stats.addSkillPoints(5);
            leveled = true;
            xpToNext = getXpForNextLevel(currentLevel);
            stats.setXpToNextLevel(xpToNext);
            player.sendMessage(new TextComponentString(
                "§aBravo ! Niveau " + currentLevel + " (+5 SP)."
            ));
        }
        stats.setXp(currentXp);

        // Condition d'envoi: xp ou niveau a changé depuis dernier envoi
        int lastXp = lastSyncedXp.getOrDefault(uuid, -1);
        int lastLevel = lastSyncedLevel.getOrDefault(uuid, -1);
        if (currentXp != lastXp || currentLevel != lastLevel || leveled) {
            // Prépare le NBT complet
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("Level", currentLevel);
            tag.setInteger("SkillPoints", stats.getSkillPoints());
            tag.setInteger("Xp", currentXp);
            tag.setInteger("XpToNext", stats.getXpToNextLevel());
            tag.setInteger("Force", stats.getForce());
            tag.setInteger("Stamina", stats.getStamina());
            tag.setInteger("Wakfu", stats.getWakfu());
            tag.setInteger("Agility", stats.getAgility());
            WakfuNetwork.INSTANCE.sendTo(new SyncStatsMessage(tag), player);

            // Met à jour cache
            lastSyncedXp.put(uuid, currentXp);
            lastSyncedLevel.put(uuid, currentLevel);
        }
    }
}
