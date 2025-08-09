package fr.wakfu.allies;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class AllyRegistry {

    // IMPORTANT : nom clair ALLY_CAPABILITY (évite ALLOY/typo)
    @CapabilityInject(IAllyCapability.class)
    public static Capability<IAllyCapability> ALLY_CAPABILITY = null;

    public static void register() {
        // Enregistre le storage et la factory - appeler ceci en preInit
        CapabilityManager.INSTANCE.register(IAllyCapability.class, new AllyStorage(), AllyCapability::new);
    }
}
