package fr.skill.gui;

import fr.skill.PlayerSkillHelper;
import fr.skill.Skill;
import fr.skill.SkillKeybinds;
import fr.skill.SkillRegistry;
import fr.skill.network.PacketUseSkill;
import fr.wakfu.WakfuMod;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * RadialMenuGui — version complète :
 * - affichage esthétique en arcs + icons
 * - cooldown radial + countdown text overlay with dynamic color + pulsation
 * - assignation touches (right click -> GuiKeyAssign)
 * - left click -> envoie PacketUseSkill au serveur
 * - onSkillsSynced() pour rafraîchir la liste dynamique
 *
 * Recommandation : utilise une instance unique côté RadialMenuHandler (éviter recreate chaque tick).
 */
@SideOnly(Side.CLIENT)
public class RadialMenuGui extends GuiScreen {

    private static final ResourceLocation BG_DEFAULT = new ResourceLocation(WakfuMod.MODID, "textures/gui/radial_bg_default.png");
    private static final ResourceLocation FRAME_NORMAL = new ResourceLocation(WakfuMod.MODID, "textures/gui/slot_frame.png");
    private static final ResourceLocation FRAME_COOLDOWN = new ResourceLocation(WakfuMod.MODID, "textures/gui/slot_frame_cooldown.png");

    private final Minecraft mc = Minecraft.getMinecraft();
    private final EntityPlayer player;

    // animation / état
    private boolean closing = false;
    private boolean doneClosing = false;
    private double startTime = 0.0;
    private long openTick = 0L;
    private static final long IGNORE_CLICKS_TICKS = 2L;
    private static final float OPEN_LENGTH = 6.0f;

    // skills affichés (ids triées)
    private final List<String> skillIds = new ArrayList<>();

    // layout
    private int centerX, centerY;
    private int radiusInner;
    private int radiusOuter;
    private float midRadius;

    private int hoveredIndex = -1;
    private int selectedIndex = -1;

    // options
    private boolean clipMouseToCircle = true;
    private float maxMouseDist = 120f; // pixels approx

    public RadialMenuGui() {
        this.player = mc.player;
        this.startTime = mc.world != null ? mc.world.getTotalWorldTime() + mc.getRenderPartialTicks() : 0.0;
        this.openTick = mc.world != null ? mc.world.getTotalWorldTime() : 0L;
        refreshSkillList();
    }

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(mc);
        centerX = sr.getScaledWidth() / 2;
        centerY = sr.getScaledHeight() / 2;

        int min = Math.min(sr.getScaledWidth(), sr.getScaledHeight());
        radiusInner = Math.max(30, min / 10);
        radiusOuter = Math.max(80, min / 6);
        midRadius = (radiusInner + radiusOuter) * 0.5f;

        refreshSkillList();
    }

    private void refreshSkillList() {
        skillIds.clear();
        if (player == null) return;

        // Tentative (si PlayerSkillHelper côté client supporte la lecture locale)
        List<String> fetched = fr.skill.PlayerSkillHelper.getUnlockedSkillIdsOrdered(player);
        if (fetched == null || fetched.isEmpty()) {
            // Fallback : utiliser le cache client rempli par PacketSyncSkills
            fetched = fr.skill.client.ClientSkillCache.getUnlockedSkillIds();
        }
        if (fetched == null) return;
        for (String id : fetched) {
            Skill s = SkillRegistry.getSkill(id);
            if (s != null && s.getType() == Skill.SkillType.ACTIVE && s.appearsInRadial()) {
                skillIds.add(id);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (closing && doneClosing) mc.displayGuiScreen(null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        double worldTime = mc.world != null ? mc.world.getTotalWorldTime() + partialTicks : startTime;
        float t = (float) ((worldTime - startTime) / OPEN_LENGTH);
        float progress = closing ? MathHelper.clamp(1.0f - t, 0f, 1f) : MathHelper.clamp(t, 0f, 1f);
        if (closing && progress <= 0f) doneClosing = true;

        float radiusIn = Math.max(8f, radiusInner * progress);
        float radiusOut = Math.max(12f, radiusOuter * progress);
        midRadius = (radiusIn + radiusOut) * 0.5f;

        // mouse relative (do NOT change OS cursor here)
        double relX = mouseX - centerX;
        double relY = mouseY - centerY;
        double mouseDist = Math.sqrt(relX * relX + relY * relY);

        double effectiveRelX = relX;
        double effectiveRelY = relY;
        if (clipMouseToCircle && mouseDist > maxMouseDist) {
            double scale = maxMouseDist / mouseDist;
            effectiveRelX = relX * scale;
            effectiveRelY = relY * scale;
            mouseDist = Math.sqrt(effectiveRelX * effectiveRelX + effectiveRelY * effectiveRelY);
        }

        double angleDeg = Math.toDegrees(Math.atan2(effectiveRelY, effectiveRelX));
        if (angleDeg < 0) angleDeg += 360.0;

        int n = skillIds.size();
        if (n == 0) {
            if (!closing) animateClose();
            return;
        }

        // draw background arcs
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        hoveredIndex = -1;

        for (int i = 0; i < n; i++) {
            float start = ((i / (float) n) + 0.25f) * 360f - (180f / n);
            float end = (((i + 1) / (float) n) + 0.25f) * 360f - (180f / n);
            float s = start % 360f; if (s < 0) s += 360f;
            float e = end % 360f; if (e < 0) e += 360f;

            boolean inArc;
            if (s < e) inArc = (angleDeg >= s && angleDeg < e);
            else inArc = (angleDeg >= s || angleDeg < e);

            boolean inRadius = (mouseDist >= radiusIn && mouseDist < radiusOut);

            int alpha;
            int r, g, b;
            if (inArc && inRadius && progress > 0.5f) {
                hoveredIndex = i;
                r = 190; g = 190; b = 190; alpha = 190;
            } else {
                r = 25; g = 25; b = 25; alpha = 110;
            }

            drawPieArc(buf, centerX, centerY, 0, radiusIn, radiusOut, s, e, r, g, b, alpha);
        }
        tess.draw();

        // restore basic state before texture rendering
        GlStateManager.enableTexture2D();
        // Ensure global color and blend are in a sane default for textures
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // Make sure GUI lighting doesn't tint textures
        RenderHelper.disableStandardItemLighting();

        // draw icons + frames + cooldown overlay + key tag
        for (int i = 0; i < n; i++) {
            float angleRad = ((i / (float) n) + 0.25f) * 2f * (float) Math.PI;
            float px = centerX + midRadius * (float) Math.cos(angleRad);
            float py = centerY + midRadius * (float) Math.sin(angleRad);

            String id = skillIds.get(i);
            Skill sk = SkillRegistry.getSkill(id);
            int size = (int) (32 * (0.6f + 0.4f * progress));

            // ----- Begin: reset GL state for safe icon rendering -----
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableStandardItemLighting();
            // ----- End state reset -----

            if (sk != null && sk.getIcon() != null) {
                ResourceLocation icon = sk.getIcon();
                mc.getTextureManager().bindTexture(icon);
                drawModalRectWithCustomSizedTexture((int) (px - size / 2f), (int) (py - size / 2f), 0, 0, size, size, size, size);
            } else {
                drawRect((int) (px - 8), (int) (py - 8), (int) (px + 8), (int) (py + 8), 0xFF444444);
            }

            // Restaurations après rendu de l'icône
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();

            // cooldown overlay (if any)
            long remaining = getRemainingCooldownMs(player, id);
            if (remaining > 0) {
                int cdTotalMs = Math.max(1, sk != null ? sk.getCooldownSeconds() * 1000 : 1000);
                float percent = MathHelper.clamp((float) remaining / (float) cdTotalMs, 0f, 1f);
                drawCooldownArc((int) (px), (int) (py), size / 2 + 2, percent);

                // dynamic color + pulsation when <= 3s
                float fractionRemain = percent; // 1 = full cd, 0 = ready
                int baseColor = getCooldownColor(fractionRemain);

                int colorToUse = baseColor;
                if (remaining <= 3000) {
                    // pulsate faster when near end
                    double ms = System.currentTimeMillis();
                    // period ~500ms
                    double pulse = 0.5 + 0.5 * Math.sin(ms / 250.0 * Math.PI * 2.0);
                    // pulse factor in [0.0..1.0], map to small blend toward white
                    double blendT = 0.25 + 0.75 * pulse; // vary between 0.25..1.0
                    colorToUse = blendColor(baseColor, 0xFFFFFFFF, blendT);
                }

                int seconds = (int) Math.ceil(remaining / 1000.0);
                String sSec = seconds + "s";
                int tx = (int) (px - mc.fontRenderer.getStringWidth(sSec) / 2f);
                int ty = (int) (py - mc.fontRenderer.FONT_HEIGHT / 2f);
                drawString(mc.fontRenderer, sSec, tx, ty, colorToUse);
            }

            // key tag corner
            int assigned = SkillKeybinds.getAssignedKeyCode(id);
            if (assigned != 0) {
                String kb = Keyboard.getKeyName(assigned);
                int tx = (int) (px + size / 2f) - mc.fontRenderer.getStringWidth(kb) - 4;
                int ty = (int) (py + size / 2f) - 8;
                drawRect(tx - 2, ty - 1, tx + mc.fontRenderer.getStringWidth(kb) + 2, ty + 9, 0xAA000000);
                drawString(mc.fontRenderer, kb, tx, ty, 0xFFFFFF);
            }
        }

        // hovered text
        if (hoveredIndex >= 0 && hoveredIndex < skillIds.size()) {
            String id = skillIds.get(hoveredIndex);
            Skill s = SkillRegistry.getSkill(id);
            if (s != null) {
                drawCenteredString(fontRenderer, s.getName(), centerX, centerY + radiusOuter + 6, 0xFFFFFF);
            }
        }

        GlStateManager.popMatrix();
    }

    // draw pie-arc by adding quads to buffer
    private static final float PRECISION_DEG = 6f;
    private void drawPieArc(BufferBuilder buffer, float x, float y, float z, float inR, float outR,
                            float startDeg, float endDeg, int r, int g, int b, int a) {

        float s = startDeg % 360f; if (s < 0) s += 360f;
        float e = endDeg % 360f; if (e < 0) e += 360f;

        float angle = e - s;
        if (angle <= 0) angle += 360f;

        int sections = Math.max(1, MathHelper.ceil(angle / PRECISION_DEG));
        float startRad = (float) Math.toRadians(s);
        float totalRad = (float) Math.toRadians(angle);

        for (int i = 0; i < sections; i++) {
            float a0 = startRad + (i / (float) sections) * totalRad;
            float a1 = startRad + ((i + 1) / (float) sections) * totalRad;

            float p1x = x + outR * (float) Math.cos(a0);
            float p1y = y + outR * (float) Math.sin(a0);
            float p2x = x + inR  * (float) Math.cos(a0);
            float p2y = y + inR  * (float) Math.sin(a0);
            float p3x = x + inR  * (float) Math.cos(a1);
            float p3y = y + inR  * (float) Math.sin(a1);
            float p4x = x + outR * (float) Math.cos(a1);
            float p4y = y + outR * (float) Math.sin(a1);

            buffer.pos(p1x, p1y, z).color(r, g, b, a).endVertex();
            buffer.pos(p2x, p2y, z).color(r, g, b, a).endVertex();
            buffer.pos(p3x, p3y, z).color(r, g, b, a).endVertex();
            buffer.pos(p4x, p4y, z).color(r, g, b, a).endVertex();
        }
    }

    /**
     * Draw a cooldown arc over a slot center. percent in [0..1] where 1 = full cooldown remaining.
     * We'll draw an arc from top (270deg) clockwise representing percent.
     */
    private void drawCooldownArc(int cx, int cy, int radius, float percent) {
        if (percent <= 0f) return;
        float start = -90f;
        float sweep = 360f * percent;

        Tessellator t = Tessellator.getInstance();
        BufferBuilder b = t.getBuffer();
        b.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        b.pos(cx, cy, 0).color(0, 0, 0, 160).endVertex();
        int steps = Math.max(6, MathHelper.ceil(sweep / 6f));
        for (int i = 0; i <= steps; i++) {
            float angle = (float) Math.toRadians(start + (sweep * i / (float) steps));
            float px = cx + radius * (float) Math.cos(angle);
            float py = cy + radius * (float) Math.sin(angle);
            b.pos(px, py, 0).color(0, 0, 0, 160).endVertex();
        }
        t.draw();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        long now = mc.world != null ? mc.world.getTotalWorldTime() : 0L;
        if (now - openTick < IGNORE_CLICKS_TICKS) return;
        processClick(mouseX, mouseY, state);
    }

    private void processClick(int mouseX, int mouseY, int state) {
        if (closing) return;
        if (hoveredIndex >= 0 && hoveredIndex < skillIds.size()) {
            String skillId = skillIds.get(hoveredIndex);
            if (state == 1) { // right click -> assign key
                mc.displayGuiScreen(new GuiKeyAssign(skillId));
                return;
            } else if (state == 0) { // left click -> send use
                long rem = getRemainingCooldownMs(player, skillId);
                if (rem > 0) {
                    player.sendStatusMessage(new net.minecraft.util.text.TextComponentString("En cooldown : " + (int)Math.ceil(rem/1000.0)+ "s"), true);
                } else {
                    // send packet to server
                    sendUseSkillPacket(skillId, null);
                }
                animateClose();
                return;
            }
        } else {
            animateClose();
        }
    }

    private void animateClose() {
        closing = true;
        doneClosing = false;
        startTime = mc.world != null ? mc.world.getTotalWorldTime() + mc.getRenderPartialTicks() : startTime;
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    /**
     * Appelé côté client quand on reçoit la nouvelle liste de skills du serveur.
     * Met à jour la liste affichée et réinitialise l'état du menu.
     */
    public void onSkillsSynced() {
        refreshSkillList();
        hoveredIndex = -1;
        selectedIndex = -1;
        this.startTime = mc.world != null ? mc.world.getTotalWorldTime() + mc.getRenderPartialTicks() : this.startTime;
        this.initGui(); // recalc layout
    }

    /**
     * Fallback: récupère le temps restant via PlayerSkillHelper si disponible.
     * Utilise reflection pour ne pas casser la compilation si la méthode n'existe pas.
     */
    private long getRemainingCooldownMs(EntityPlayer player, String skillId) {
        try {
            Method m = PlayerSkillHelper.class.getMethod("getRemainingCooldownMs", EntityPlayer.class, String.class);
            Object res = m.invoke(null, player, skillId);
            if (res instanceof Number) return ((Number) res).longValue();
        } catch (NoSuchMethodException ignored) {
            // method not present in PlayerSkillHelper -> fallback 0
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return 0L;
    }

    /**
     * Envoie du packet d'usage au serveur.
     * Utilise ton WakfuNetwork / PacketUseSkill implémenté précédemment.
     */
    private void sendUseSkillPacket(String skillId, @Nullable Entity target) {
        int targetId = (target == null) ? -1 : target.getEntityId();
        // assure-toi que WakfuNetwork et PacketUseSkill existent et sont enregistrés
        WakfuNetwork.INSTANCE.sendToServer(new PacketUseSkill(skillId, targetId));
    }

    /**
     * Blend two ARGB ints by t in [0..1]
     */
    private int blendColor(int colorFrom, int colorTo, double t) {
        t = Math.max(0.0, Math.min(1.0, t));
        int a1 = (colorFrom >> 24) & 0xFF;
        int r1 = (colorFrom >> 16) & 0xFF;
        int g1 = (colorFrom >> 8) & 0xFF;
        int b1 = colorFrom & 0xFF;
        int a2 = (colorTo >> 24) & 0xFF;
        int r2 = (colorTo >> 16) & 0xFF;
        int g2 = (colorTo >> 8) & 0xFF;
        int b2 = colorTo & 0xFF;
        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    /**
     * Retourne une couleur ARGB adaptée au pourcentage de cooldown restant.
     * fraction in [0..1] où 1 = cooldown complet (rouge), 0 = prêt (blanc).
     * On fait un gradient blanc -> jaune -> rouge pour une lecture visuelle simple.
     */
    private int getCooldownColor(float fraction) {
        fraction = MathHelper.clamp(fraction, 0f, 1f);

        // easing pour un rendu plus doux
        float t = (float) (1.0 - Math.pow(1.0 - fraction, 1.2));

        if (t < 0.5f) {
            // blanc -> jaune
            float sub = t / 0.5f;
            return blendColor(0xFFFFFFFF, 0xFFFFFF66, sub);
        } else {
            // jaune -> rouge
            float sub = (t - 0.5f) / 0.5f;
            return blendColor(0xFFFFFF66, 0xFFFF4444, sub);
        }
    }
}
