package fr.skill.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.ResourceLocation;

/**
 * Attach provider and copy capability on clone (respawn).
 */
public class AttachSkillCapabilityHandler {

    private static final ResourceLocation KEY = new ResourceLocation("wakfumod", "player_skills");

    @SubscribeEvent
    public void onAttach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(KEY, new PlayerSkillsProvider());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        IPlayerSkills oldCap = event.getOriginal().getCapability(CapabilityPlayerSkills.PLAYER_SKILLS, null);
        IPlayerSkills newCap = event.getEntityPlayer().getCapability(CapabilityPlayerSkills.PLAYER_SKILLS, null);
        if (oldCap != null && newCap != null) {
            newCap.setUnlocked(oldCap.getUnlocked());
        }
    }
}
