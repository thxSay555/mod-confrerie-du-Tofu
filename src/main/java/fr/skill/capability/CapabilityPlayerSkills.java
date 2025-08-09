package fr.skill.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public final class CapabilityPlayerSkills {

    @CapabilityInject(IPlayerSkills.class)
    public static Capability<IPlayerSkills> PLAYER_SKILLS = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IPlayerSkills.class, new PlayerSkillsStorage(), PlayerSkills::new);
    }

    private CapabilityPlayerSkills() {}
}
