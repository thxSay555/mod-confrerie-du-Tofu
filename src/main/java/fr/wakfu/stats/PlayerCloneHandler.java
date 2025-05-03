package fr.wakfu.stats;

import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerCloneHandler {
    @SubscribeEvent
    public void onPlayerClone(Clone evt) {
        if (!evt.isWasDeath()) return;

        IPlayerStats oldS = evt.getOriginal().getCapability(StatsProvider.PLAYER_STATS, null);
        IPlayerStats newS = evt.getEntityPlayer().getCapability(StatsProvider.PLAYER_STATS, null);
        if (oldS == null || newS == null) return;

        // Copie intégrale
        newS.setLevel           (oldS.getLevel());
        newS.setSkillPoints     (oldS.getSkillPoints());
        newS.setXp              (oldS.getXp());
        newS.setXpToNextLevel   (oldS.getXpToNextLevel());
        newS.setForce           (oldS.getForce());
        newS.setStamina         (oldS.getStamina());
        newS.setWakfu           (oldS.getWakfu());
        newS.setAgility         (oldS.getAgility());
        newS.setIntensity       (oldS.getIntensity());
        newS.setCurrentWakfu    (oldS.getCurrentWakfu());
        newS.setCurrentStamina  (oldS.getCurrentStamina());
    }
}
