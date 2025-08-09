package fr.skill.handlers;

import fr.skill.PlayerSkillHelper;
import fr.skill.network.PacketSyncSkills;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

public class SkillSyncHandler {

    // Register this handler on server/common init:
    // MinecraftForge.EVENT_BUS.register(new SkillSyncHandler());

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP mp = (EntityPlayerMP) event.player;
            WakfuNetwork.INSTANCE.sendTo(new PacketSyncSkills(PlayerSkillHelper.getUnlockedSkillIdsOrdered(mp)), mp);
            System.out.println("[WAKFU-SKILLS] Sent skills to player on login: " + mp.getName());
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP mp = (EntityPlayerMP) event.player;
            WakfuNetwork.INSTANCE.sendTo(new PacketSyncSkills(PlayerSkillHelper.getUnlockedSkillIdsOrdered(mp)), mp);
            System.out.println("[WAKFU-SKILLS] Sent skills to player on respawn: " + mp.getName());
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDim(PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP mp = (EntityPlayerMP) event.player;
            WakfuNetwork.INSTANCE.sendTo(new PacketSyncSkills(PlayerSkillHelper.getUnlockedSkillIdsOrdered(mp)), mp);
            System.out.println("[WAKFU-SKILLS] Sent skills to player on change-dim: " + mp.getName());
        }
    }
}
