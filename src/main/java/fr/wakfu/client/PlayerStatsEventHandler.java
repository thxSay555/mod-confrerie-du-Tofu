package fr.wakfu.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class PlayerStatsEventHandler {

    public PlayerStatsEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        PlayerStatsScreen screen = new PlayerStatsScreen();
        screen.onKeyInput(event);
    }
}
