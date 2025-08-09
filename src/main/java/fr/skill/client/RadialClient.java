package fr.skill.client;

import fr.skill.gui.RadialMenuGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import org.lwjgl.input.Keyboard;


@Mod.EventBusSubscriber(Side.CLIENT)
@SideOnly(Side.CLIENT)
public class RadialClient {

    public static KeyBinding KEY_ACTIVATE_RADIAL_MENU;

    static {
        // register keybinding
        KEY_ACTIVATE_RADIAL_MENU = new KeyBinding("key.wakfu.radial", Keyboard.KEY_LMENU, "key.category.wakfu");
        ClientRegistry.registerKeyBinding(KEY_ACTIVATE_RADIAL_MENU);
    }

    @SubscribeEvent
    public static void handleKeys(InputEvent ev) {
        Minecraft mc = Minecraft.getMinecraft();

        // If radial menu feature disabled somewhere, check your settings here.
        // if (!MyConfig.radialEnabled) return;

        // Use while(KEY.isPressed()) to consume all key-press events (AncientSpellcraft pattern)
        while (KEY_ACTIVATE_RADIAL_MENU.isPressed()) {
            // only open when no other GUI is open
            if (mc.currentScreen == null) {
                // optional: you can check context (e.g. item in hand) before opening,
                // but for skills we want to open unconditionally
                mc.displayGuiScreen(new RadialMenuGui());
            }
        }
    }

    /**
     * Utility to clear pending presses (used if you programmatically want to flush the key state)
     */
    public static void wipeOpen() {
        while (KEY_ACTIVATE_RADIAL_MENU.isPressed()) { /* consume */ }
    }
}
