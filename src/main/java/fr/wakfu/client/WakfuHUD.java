package fr.wakfu.client;

import fr.wakfu.stats.IPlayerStats;
import fr.wakfu.stats.StatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.opengl.GL11;

@EventBusSubscriber
public class WakfuHUD {

    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        EntityPlayer player = mc.player;
        if (player == null) return;

        IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats == null) return;

        // Régénération des valeurs actuelles
        float regenWakfu = stats.getWakfuRegeneration() * stats.getRegenMultiplier();
        float regenStam  = stats.getStaminaRegeneration() * stats.getRegenMultiplier();

        float wakfuMax = stats.getWakfu() * stats.getWakfuMultiplier();
        float stamMax  = stats.getStamina() * stats.getStaminaMultiplier();

        stats.setCurrentWakfu(Math.min(wakfuMax, stats.getCurrentWakfu() + regenWakfu));
        stats.setCurrentStamina(Math.min(stamMax, stats.getCurrentStamina() + regenStam));

        // Affichage HUD
        ScaledResolution res = new ScaledResolution(mc);
        int x = 10;
        int y = res.getScaledHeight() - 50;

        // --- Barres principales ---
        drawBar(x, y, 100, 8, stats.getCurrentWakfu(), wakfuMax, 0xFF3BE3FF);
        drawBar(x, y + 12, 100, 8, stats.getCurrentStamina(), stamMax, 0xFFFFD700);

        mc.fontRenderer.drawStringWithShadow("Wakfu: " + (int) stats.getCurrentWakfu() + " / " + (int) wakfuMax, x, y - 10, 0x3BE3FF);
        mc.fontRenderer.drawStringWithShadow("Stamina: " + (int) stats.getCurrentStamina() + " / " + (int) stamMax, x, y + 2, 0xFFD700);

        // --- Barre d'intensité ---
        int intensity = stats.getIntensity(); // entre 0 et 100
        int barWidth = 100;
        int barX = x;
        int barY = y - 22;

        // Dessin du curseur d’intensité sur la barre de Wakfu
        int cursorX = barX + (int) (barWidth * (intensity / 100.0f));
        drawRect(cursorX - 1, y - 2, cursorX + 1, y + 10, 0xFFAA00FF); // curseur violet

        // Texte affiché au-dessus
        mc.fontRenderer.drawStringWithShadow("Intensité : " + intensity, barX, barY, 0xAA00FF);
    }

    private static void drawBar(int x, int y, int width, int height, float current, float max, int color) {
        float ratio = current / max;
        int filled = (int) (width * ratio);

        // Fond
        drawRect(x, y, x + width, y + height, 0x80000000);
        // Remplissage
        drawRect(x, y, x + filled, y + height, color);
    }

    private static void drawRect(int left, int top, int right, int bottom, int color) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(
            ((color >> 16) & 0xFF) / 255f,
            ((color >> 8) & 0xFF) / 255f,
            (color & 0xFF) / 255f,
            ((color >> 24) & 0xFF) / 255f
        );
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(left, top);
        GL11.glVertex2f(left, bottom);
        GL11.glVertex2f(right, bottom);
        GL11.glVertex2f(right, top);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
