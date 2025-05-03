package fr.wakfu.stats;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XpGainHandler {

    // 1) Quand un mob lâche de l’XP
    @SubscribeEvent
    public void onExperienceDrop(LivingExperienceDropEvent ev) {
        if (!(ev.getAttackingPlayer() instanceof EntityPlayerMP)) return;
        EntityPlayerMP p = (EntityPlayerMP) ev.getAttackingPlayer();
        IPlayerStats s = p.getCapability(StatsProvider.PLAYER_STATS, null);
        if (s == null) return;
        s.setXp(s.getXp() + ev.getDroppedExperience());
    }

    // 2) Quand on ramasse une orb d’XP dans le monde
    @SubscribeEvent
    public void onXpPickup(PlayerPickupXpEvent ev) {
        EntityPlayerMP p = (EntityPlayerMP) ev.getEntityPlayer();
        IPlayerStats s = p.getCapability(StatsProvider.PLAYER_STATS, null);
        if (s == null) return;
        s.setXp(s.getXp() + ev.getOrb().xpValue);
    }

    // 3) Quand on tape la commande vanilla /xp <amount>
    @SubscribeEvent
    public void onXpCmd(CommandEvent ev) {
        if (!ev.getCommand().getName().equalsIgnoreCase("xp")) return;
        Object[] pr = ev.getParameters();
        if (pr.length < 1) return;
        // Les args viennent en String, on parse
        try {
            int amt = Integer.parseInt(pr[0].toString());
            if (ev.getSender() instanceof EntityPlayerMP) {
                EntityPlayerMP p = (EntityPlayerMP) ev.getSender();
                IPlayerStats s = p.getCapability(StatsProvider.PLAYER_STATS, null);
                if (s != null) s.setXp(s.getXp() + amt);
            }
        } catch (NumberFormatException ignored) {}
    }
}
