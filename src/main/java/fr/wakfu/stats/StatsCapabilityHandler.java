package fr.wakfu.stats;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StatsCapabilityHandler {
    public static final ResourceLocation PLAYER_STATS_ID = new ResourceLocation("wakfu", "player_stats");

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(PLAYER_STATS_ID, new StatsProvider());
        }
    }
}
