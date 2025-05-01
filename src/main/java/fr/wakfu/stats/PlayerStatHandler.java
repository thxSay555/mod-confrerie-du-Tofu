package fr.wakfu.stats;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerStatHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        World world = player.world;

        // Serveur uniquement
        if (world.isRemote) return;

        // Récupère les stats
        IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats == null) return;

        // --- Régénération ---
        float maxWakfu = stats.getWakfu() * stats.getWakfuMultiplier();
        float maxStamina = stats.getStamina() * stats.getStaminaMultiplier();

        float regenMultiplier = 1.0f; // Tu peux plus tard le rendre dynamique
        float wakfuRegen = stats.getWakfuRegeneration() * regenMultiplier;
        float staminaRegen = stats.getStaminaRegeneration() * regenMultiplier;

        // Incrémente jusqu’à la valeur max
        float newWakfu = Math.min(stats.getCurrentWakfu() + wakfuRegen, maxWakfu);
        float newStamina = Math.min(stats.getCurrentStamina() + staminaRegen, maxStamina);

        stats.setCurrentWakfu(newWakfu);
        stats.setCurrentStamina(newStamina);
    }
}
