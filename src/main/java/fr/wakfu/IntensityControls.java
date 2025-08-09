package fr.wakfu;

import fr.wakfu.network.UpdateStatsMessage;
import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.stats.IPlayerStats;
import fr.wakfu.stats.StatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class IntensityControls {
    public static final KeyBinding INCREASE_INTENSITY = 
        new KeyBinding("key.wakfu.increase_intensity", Keyboard.KEY_I, "key.category.wakfu");

    private static final Minecraft mc = Minecraft.getMinecraft();
    private long lastChangeTime;
    private int changeDirection = 0; // 0 = aucun, 1 = augmentation, -1 = diminution

    public static void register() {
        ClientRegistry.registerKeyBinding(INCREASE_INTENSITY);
        MinecraftForge.EVENT_BUS.register(new IntensityControls());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        boolean ctrlPressed = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        
        if (INCREASE_INTENSITY.isPressed()) {
            changeIntensity(ctrlPressed ? -1 : 1);
        }// Ctrl+Inverse = diminue
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.player == null) return;

        boolean ctrlPressed = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        boolean increaseHeld = Keyboard.isKeyDown(INCREASE_INTENSITY.getKeyCode());

        int newDirection = 0;
        if (increaseHeld) newDirection = ctrlPressed ? -1 : 1;

        if (newDirection != 0 && newDirection == changeDirection) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastChangeTime > 500) { // Toutes les 0.5 secondes
                changeIntensity(newDirection * 2); // Changement par pas de 2
                lastChangeTime = currentTime;
            }
        } else {
            changeDirection = newDirection;
            lastChangeTime = System.currentTimeMillis();
        }
    }

    private void changeIntensity(int delta) {
        if (mc.player == null) return;

        IPlayerStats stats = mc.player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats == null) return;

        int newValue = Math.max(0, Math.min(100, stats.getIntensity() + delta));
        if (newValue != stats.getIntensity()) {
       
            
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("Force",     stats.getForce());
            tag.setInteger("Stamina",   stats.getStamina());
            tag.setInteger("Wakfu",     stats.getWakfu());
            tag.setInteger("Agility",   stats.getAgility());
            tag.setInteger("Intensity", newValue);
            stats.setIntensity(newValue);
            tag.setInteger("Level", stats.getLevel());
            tag.setInteger("Xp", stats.getXp());
            tag.setInteger("XpToNext", stats.getXpToNextLevel());
            tag.setInteger("SkillPoints", stats.getSkillPoints());
            tag.setFloat("CurrentWakfu", stats.getCurrentWakfu());
            tag.setFloat("CurrentStamina", stats.getCurrentStamina());
            WakfuNetwork.INSTANCE.sendToServer(new UpdateStatsMessage(tag));
        }
    }
}