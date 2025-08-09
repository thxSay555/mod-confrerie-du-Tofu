package fr.skill.input;

import fr.skill.SkillKeybinds;
import fr.skill.network.PacketUseSkill;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

/**
 * Client-side: detect assigned keypresses and send PacketUseSkill to server.
 * Register in client preInit: MinecraftForge.EVENT_BUS.register(new SkillKeyInputHandler());
 */
public class SkillKeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // ignore if typing in gui
        if (Minecraft.getMinecraft().currentScreen != null) return;

        int key = Keyboard.getEventKey();
        boolean pressed = Keyboard.getEventKeyState();
        if (!pressed) return;

        String skillId = SkillKeybinds.getSkillForKey(key);
        if (skillId != null && !skillId.isEmpty()) {
            WakfuNetwork.INSTANCE.sendToServer(new PacketUseSkill(skillId, -1));
        }
    }
}
