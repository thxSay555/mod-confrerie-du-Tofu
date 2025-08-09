package fr.wakfu.allies;

import fr.wakfu.WakfuMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.ResourceLocation;

public class AllyEventHandler {
    public static final ResourceLocation ID = new ResourceLocation(WakfuMod.MODID, "ally_provider");

    @SuppressWarnings("unlikely-arg-type")
	@SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            if (event.getCapabilities().containsKey(ID.toString())) return;

            EntityPlayer player = (EntityPlayer) event.getObject();
            event.addCapability(ID, new AllyProvider());

            // Log sûr : n'utilise pas getName() (peut être null pendant la construction)
            java.util.UUID uuid = player.getUniqueID();
            System.out.println("[WakfuMod] AllyProvider attaché (player UUID=" + uuid + ", class=" 
                                + player.getClass().getSimpleName() + ")");
        }
    }


    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer original = event.getOriginal();
        EntityPlayer clone = event.getEntityPlayer();

        if (original == null || clone == null) return;

        if (original.hasCapability(AllyRegistry.ALLY_CAPABILITY, null) && clone.hasCapability(AllyRegistry.ALLY_CAPABILITY, null)) {
            IAllyCapability orig = original.getCapability(AllyRegistry.ALLY_CAPABILITY, null);
            IAllyCapability dest = clone.getCapability(AllyRegistry.ALLY_CAPABILITY, null);
            if (orig != null && dest != null) {
                dest.clear();
                for (java.util.UUID u : orig.getAllies()) dest.addAlly(u);
            }
        }
    }
}
