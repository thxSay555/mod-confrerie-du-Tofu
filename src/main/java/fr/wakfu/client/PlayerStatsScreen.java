package fr.wakfu.client;

import org.lwjgl.input.Keyboard;
import fr.wakfu.network.UpdateStatsMessage;
import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.stats.IPlayerStats;
import fr.wakfu.stats.StatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class PlayerStatsScreen extends GuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final net.minecraft.client.settings.KeyBinding KEY_STATS =
        new net.minecraft.client.settings.KeyBinding("key.wakfu.stats", Keyboard.KEY_K, "key.categories.wakfu");
    static { ClientRegistry.registerKeyBinding(KEY_STATS); }

    private static final int BUTTON_VALIDATE = 10;
    private int pendingForce, pendingStamina, pendingWakfu, pendingAgility, pendingPoints;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KEY_STATS.isPressed() && Minecraft.getMinecraft().currentScreen == null) {
            Minecraft.getMinecraft().displayGuiScreen(new PlayerStatsScreen());
        }
    }
    public void refreshData() {
        IPlayerStats stats = mc.player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats != null) {
            pendingForce = stats.getForce();
            pendingStamina = stats.getStamina();
            pendingWakfu = stats.getWakfu();
            pendingAgility = stats.getAgility();
            pendingPoints = stats.getSkillPoints();
        }
        initGui(); // Recharge les boutons avec les nouvelles données
    }


    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        // Récupération des stats client
        EntityPlayer player = mc.player;
        IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats != null) {
            pendingForce = stats.getForce();
            pendingStamina = stats.getStamina();
            pendingWakfu = stats.getWakfu();
            pendingAgility = stats.getAgility();
            pendingPoints = stats.getSkillPoints();
        }

        // --- Code pour les boutons ---
        ScaledResolution res = new ScaledResolution(mc);
        int xBase = res.getScaledWidth() / 2 - 100;
        int yBase = res.getScaledHeight() / 2 - 80;
        int lineHeight = 20;

        String[] labels = { "Force", "Stamina", "Wakfu", "Agilité" };
        for (int i = 0; i < labels.length; i++) {
            String txt = labels[i] + ": " + getPendingStatValue(i);
            int textWidth = mc.fontRenderer.getStringWidth(txt);
            int btnX = xBase + textWidth + 5;
            int btnY = yBase + 40 + lineHeight * i;
            buttonList.add(new GuiButton(i, btnX, btnY, 20, 20, "+"));
        }

        // Bouton Valider
        int btnValX = xBase + 200 - 40;
        int btnValY = yBase + 4;
        buttonList.add(new GuiButton(BUTTON_VALIDATE, btnValX, btnValY, 40, 20, "Valider"));
    }
    

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == BUTTON_VALIDATE) {
            // Récupère les stats du joueur
            EntityPlayer player = mc.player;
            IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
            if (stats == null) return;

            NBTTagCompound out = new NBTTagCompound();
            out.setInteger("Force", pendingForce);
            out.setInteger("Stamina", pendingStamina);
            out.setInteger("Wakfu", pendingWakfu);
            out.setInteger("Agility", pendingAgility);
            out.setInteger("SkillPoints", pendingPoints);
            
            // Ajout des valeurs actuelles
            out.setFloat("CurrentWakfu", stats.getCurrentWakfu());
            out.setFloat("CurrentStamina", stats.getCurrentStamina());
            
            IPlayerStats currentStats = mc.player.getCapability(StatsProvider.PLAYER_STATS, null);
            if (currentStats != null) {
                out.setInteger("Level", currentStats.getLevel());
                out.setInteger("Xp", currentStats.getXp());
                out.setInteger("XpToNext", currentStats.getXpToNextLevel());
                out.setInteger("Intensity", currentStats.getIntensity());
            }
            WakfuNetwork.INSTANCE.sendToServer(new UpdateStatsMessage(out));
            mc.player.closeScreen();
            return;
        }

        if (pendingPoints <= 0) {
            mc.player.sendMessage(new TextComponentString("\u00a7cPas assez de points de compétence."));
            return;
        }

        switch (button.id) {
            case 0: pendingForce++; break;
            case 1: pendingStamina++; break;
            case 2: pendingWakfu++; break;
            case 3: pendingAgility++; break;
        }
        pendingPoints--;
        mc.player.sendMessage(new TextComponentString(
            "\u00a7aStat modifiée ! SP restants : \u00a7e" + pendingPoints
        ));
        
    }
    

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        ScaledResolution res = new ScaledResolution(mc);
        int xBase = res.getScaledWidth()  / 2 - 100;
        int yBase = res.getScaledHeight() / 2 - 80;
        int lineHeight = 25;

        EntityPlayer player = mc.player;
        IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats == null) return;

        mc.fontRenderer.drawStringWithShadow("Niveau: " + stats.getLevel(), xBase, yBase, 0xFFFFFF);
        int xp    = stats.getXp(), xpMax = stats.getXpToNextLevel();
        mc.fontRenderer.drawStringWithShadow(
            String.format("XP: %d / %d", xp, xpMax),
            xBase, yBase + 12, 0xFFFFFF
        );
        drawBar(xBase, yBase + 24, 200, 8, xp, xpMax);

        String[] labels = { "Force", "Stamina", "Wakfu", "Agilité" };
        for (int i = 0; i < labels.length; i++) {
            String txt = labels[i] + ": " + getPendingStatValue(i);
            mc.fontRenderer.drawString(txt, xBase, yBase + 40 + lineHeight * i, 0xAAAAAA);
        }

        mc.fontRenderer.drawString("SP: " + pendingPoints,
            xBase, yBase + 40 + lineHeight * labels.length + 4, 0xFFFFAA00);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawBar(int x, int y, int w, int h, float cur, float max) {
        float ratio = Math.min(1f, cur / max);
        int filled = (int) (w * ratio);
        drawRect(x, y, x + w,         y + h, 0x80000000);
        drawRect(x, y, x + filled,    y + h, 0xFF00FF00);
    }

    private int getPendingStatValue(int index) {
        switch (index) {
            case 0: return pendingForce;
            case 1: return pendingStamina;
            case 2: return pendingWakfu;
            case 3: return pendingAgility;
            default: return 0;
        }
    }
}
