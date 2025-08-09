package fr.skill;

import fr.skill.PlayerSkillHelper;
import fr.skill.network.PacketSyncSkills;
import fr.wakfu.network.WakfuNetwork; // adapte si ton network wrapper a un nom différent
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

/**
 * Envoie PacketSyncSkills au player quand il se connecte / respawn / change dimension.
 * Register this handler once on the server common/initialization:
 *    MinecraftForge.EVENT_BUS.register(new SkillSyncHandler());
 */
public class SkillSyncHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP mp = (EntityPlayerMP) event.player;
            WakfuNetwork.INSTANCE.sendTo(new PacketSyncSkills(PlayerSkillHelper.getUnlockedSkillIdsOrdered(mp)), mp);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP mp = (EntityPlayerMP) event.player;
            WakfuNetwork.INSTANCE.sendTo(new PacketSyncSkills(PlayerSkillHelper.getUnlockedSkillIdsOrdered(mp)), mp);
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDim(PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP mp = (EntityPlayerMP) event.player;
            WakfuNetwork.INSTANCE.sendTo(new PacketSyncSkills(PlayerSkillHelper.getUnlockedSkillIdsOrdered(mp)), mp);
        }
    }
}
