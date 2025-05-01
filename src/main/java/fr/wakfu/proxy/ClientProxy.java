package fr.wakfu.proxy;

import fr.wakfu.client.WakfuHUDOverlay;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(WakfuHUDOverlay.class);
    }
}
