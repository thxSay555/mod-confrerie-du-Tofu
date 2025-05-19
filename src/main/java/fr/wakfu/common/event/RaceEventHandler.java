package fr.wakfu.common.event;

import fr.wakfu.WakfuMod;
import fr.wakfu.client.gui.GuiRaceSelection;
import fr.wakfu.common.capabilities.RaceCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = WakfuMod.MODID, value = Side.CLIENT)
public class RaceEventHandler {

    private static boolean raceGuiOpened = false;

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent event) {
        if (event.getObject() instanceof EntityPlayer) {
            System.out.println("[AttachCapabilities] Attaching RaceCapability to " + event.getObject().getClass().getSimpleName());
            event.addCapability(RaceCapability.RACE_CAPABILITY_ID, new RaceCapability.Provider());
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null || mc.currentScreen != null || raceGuiOpened) return;

        RaceCapability.IRace race = mc.player.getCapability(RaceCapability.RACE_CAPABILITY, null);
        if (race != null && !race.hasRace()) {
            System.out.println("[ClientTick] Opening RaceSelection GUI");
            mc.displayGuiScreen(new GuiRaceSelection());
            raceGuiOpened = true;
        }
    }
}
