package fr.wakfu.stats;

import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.network.SyncStatsMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LevelHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent ev) {
        if (ev.phase != TickEvent.Phase.END || ev.player.world.isRemote
         || !(ev.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP p = (EntityPlayerMP) ev.player;
        IPlayerStats s = p.getCapability(StatsProvider.PLAYER_STATS, null);
        if (s == null) return;

        int xp    = s.getXp();
        int next  = s.getXpToNextLevel();
        int lvl   = s.getLevel();

        // Boucle sans plafond
        while (xp >= next) {
            xp -= next;
            lvl++;
            s.setLevel(lvl);
            s.addSkillPoints(5);

            next = getXpForNextLevel(lvl);
            s.setXpToNextLevel(next);

            p.sendMessage(new TextComponentString(
                "§aBravo ! Niveau " + lvl + " (+5 SP)"
            ));
        }

        s.setXp(xp);  // on remet la XP restante

        // Sync client
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Level",      s.getLevel());
        tag.setInteger("SkillPoints",s.getSkillPoints());
        tag.setInteger("Xp",         s.getXp());
        tag.setInteger("XpToNext",   s.getXpToNextLevel());
        WakfuNetwork.INSTANCE.sendTo(new SyncStatsMessage(tag), p);
    }

    public static int getXpForNextLevel(int currentLevel) {
        return (int) Math.round(50 * Math.pow(1.1, currentLevel - 1));
    }
}
