package fr.wakfu.common.event;

import fr.wakfu.WakfuMod;
import fr.wakfu.common.capabilities.RaceCapability;
import fr.wakfu.common.network.PacketRequestRaceSelection;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = WakfuMod.MODID)
public class RaceEventHandler {
	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
	    if (event.getObject() instanceof EntityPlayer) {
	        event.addCapability(
	            RaceCapability.RACE_CAPABILITY_ID,
	            new RaceCapability.Provider()
	        );
	        System.out.println("[Capability] Attachée au joueur.");
	    }
	}
	// RaceEventHandler.java
	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
	    if (event.player instanceof EntityPlayerMP) {
	        EntityPlayerMP player = (EntityPlayerMP) event.player;
	        RaceCapability.IRace race = player.getCapability(RaceCapability.RACE_CAPABILITY, null);
	        if (race != null && !race.hasRace()) {
	            WakfuNetwork.INSTANCE.sendTo(new PacketRequestRaceSelection(), player);
	        }
	    }
	}
}