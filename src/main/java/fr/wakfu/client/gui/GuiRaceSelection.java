package fr.wakfu.client.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import fr.wakfu.WakfuMod;
import fr.wakfu.common.network.PacketSetRace;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiRaceSelection extends GuiScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(WakfuMod.MODID, "textures/gui/class_select.png");
    private static final ResourceLocation CLASS_ICONS = new ResourceLocation(WakfuMod.MODID, "textures/gui/class_icons.png");
    
    // Configuration UI
    private static final int ICON_SIZE = 32;
    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 32;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_RADIUS = 120;
    private static final int CONFIRM_BUTTON_WIDTH = 120;
    
    // Données des races
    private static final String[] RACE_NAMES = {"Eliatrope","Sadida","Cra", "Iop",  };
    private static final String[] RACE_DESCRIPTIONS = {
    	"Contrôleur de portails, manieur du wakfu\n§6Force: §f5\n§bWakfu: §f12\n§aStamina: §f8\n§eAgilité: §f10",
    	"Protecteur de la nature, invocateur rusé\n§6Force: §f8\n§bWakfu: §f10\n§aStamina: §f10\n§eAgilité: §f6",
        "Tireur d'élite, domination à distance\n§6Force: §f6\n§bWakfu: §f9\n§aStamina: §f10\n§eAgilité: §f8",
        "Guerrier fonceur, maître du corps à corps\n§6Force: §f10\n§bWakfu: §f6\n§aStamina: §f18\n§eAgilité: §f8",
      
        
    };
    
    private int hoveredRace = -1;
    private long initTime;
    private boolean isClosing = false;

    @Override
    public void initGui() {
        this.initTime = System.currentTimeMillis();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.buttonList.clear();
        
        // Boutons des races en cercle
        for (int i = 0; i < RACE_NAMES.length; i++) {
            double angle = Math.toRadians(i * (360f / RACE_NAMES.length));
            int x = (int) (centerX + BUTTON_RADIUS * Math.cos(angle) - BUTTON_WIDTH/2);
            int y = (int) (centerY + BUTTON_RADIUS * Math.sin(angle) - BUTTON_HEIGHT/2);
            
            this.buttonList.add(new RaceButton(
                i, x, y, 
                BUTTON_WIDTH, BUTTON_HEIGHT, 
                RACE_NAMES[i], i
            ));
        }
        
        // Bouton de confirmation
        GuiButton confirmButton = new GuiButton(
            RACE_NAMES.length,
            centerX - CONFIRM_BUTTON_WIDTH/2,
            centerY + BUTTON_RADIUS + 20,
            CONFIRM_BUTTON_WIDTH, 20,
            "Confirmer la race"
        );
        
        confirmButton.visible = false;
        this.buttonList.add(confirmButton);
        
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Animation d'entrée
        float fadeIn = Math.min(1, (System.currentTimeMillis() - initTime) / 500f);
        GlStateManager.color(1, 1, 1, fadeIn);
        
        // Fond personnalisé
        drawCustomBackground();
        
        // Titre
        drawCenteredString(
            fontRenderer, 
            "§l§nChoisis ta classe§r §e✧", 
            width/2, 0, 
            0xFFFFFF
        );
        
        // Description au survol
        drawHoverText(mouseX, mouseY);
        
        // Boutons
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCustomBackground() {
        this.drawDefaultBackground();
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(
            0, 0, 0, 0, 
            this.width, this.height, 
            this.width, this.height
        );
        GlStateManager.disableBlend();
    }

    private void drawHoverText(int mouseX, int mouseY) {
        if (hoveredRace >= 0 && hoveredRace < RACE_DESCRIPTIONS.length) {
            List<String> lines = Arrays.asList(RACE_DESCRIPTIONS[hoveredRace].split("\n"));
            GuiUtils.drawHoveringText(
                lines, mouseX, mouseY, 
                width, height, -1, 
                fontRenderer
            );
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id < RACE_NAMES.length) {
            handleRaceSelection(button.id);
        } else if (button.id == RACE_NAMES.length) {
            confirmSelection();
        }
    }

    private void handleRaceSelection(int raceId) {
        hoveredRace = raceId;
        playSound(SoundEvents.BLOCK_ANVIL_LAND);
        
        // Active le bouton de confirmation
        GuiButton confirmButton = buttonList.get(buttonList.size() - 1);
        confirmButton.visible = true;
    }

    private void confirmSelection() {
        if (hoveredRace == -1 || isClosing) return;
        
        isClosing = true;
        playSound(SoundEvents.ENTITY_PLAYER_LEVELUP);
        
        // Envoi au serveur
        WakfuNetwork.INSTANCE.sendToServer(
            new PacketSetRace(RACE_NAMES[hoveredRace])
        );
        
        // Fermeture
        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    private void playSound(SoundEvent sound) {
        Minecraft.getMinecraft().getSoundHandler().playSound(
            new PositionedSoundRecord(
                sound, SoundCategory.MASTER,
                1.0f, 1.0f,
                Minecraft.getMinecraft().player.getPosition()
            )
        );
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    private class RaceButton extends GuiButton {
        private final int raceIndex;
        
        public RaceButton(int buttonId, int x, int y, int width, int height, String text, int raceIndex) {
            super(buttonId, x, y, width, height, text);
            this.raceIndex = raceIndex;
        }
        
        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) return;
            
            this.hovered = mouseX >= this.x && mouseX < this.x + this.width 
                         && mouseY >= this.y && mouseY < this.y + this.height;
            
            if (this.hovered) {
                hoveredRace = raceIndex;
            }
            
            // Fond du bouton
            drawButtonBackground();
            
            // Icône
            drawRaceIcon(mc);
            
            // Texte
            drawButtonText(mc);
        }
        
        private void drawButtonBackground() {
            int color = hovered ? 0x80FFFFFF : 0x40FFFFFF;
            if (hoveredRace == raceIndex) color = 0xA0FFFF00;
            
            drawRect(x, y, x + width, y + height, color);
            
            // Bordure
            int borderColor = hovered ? 0xFFD700 : 0x80FFFFFF;
            if (hoveredRace == raceIndex) borderColor = 0xFFFFA500;
            
            drawRect(x, y, x + width, y + 1, borderColor);
            drawRect(x, y + height - 1, x + width, y + height, borderColor);
            drawRect(x, y, x + 1, y + height, borderColor);
            drawRect(x + width - 1, y, x + width, y + height, borderColor);
        }
        
        private void drawRaceIcon(Minecraft mc) {
            mc.getTextureManager().bindTexture(CLASS_ICONS);
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int u = raceIndex * ICON_SIZE;
            int v = 0;

            float texU = u / (float) TEXTURE_WIDTH;
            float texV = v / (float) TEXTURE_HEIGHT;
            float texUSize = ICON_SIZE / (float) TEXTURE_WIDTH;
            float texVSize = ICON_SIZE / (float) TEXTURE_HEIGHT;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x + 10, y + (height - ICON_SIZE) / 2 + ICON_SIZE, zLevel).tex(texU, texV + texVSize).endVertex();
            buffer.pos(x + 10 + ICON_SIZE, y + (height - ICON_SIZE) / 2 + ICON_SIZE, zLevel).tex(texU + texUSize, texV + texVSize).endVertex();
            buffer.pos(x + 10 + ICON_SIZE, y + (height - ICON_SIZE) / 2, zLevel).tex(texU + texUSize, texV).endVertex();
            buffer.pos(x + 10, y + (height - ICON_SIZE) / 2, zLevel).tex(texU, texV).endVertex();
            tessellator.draw();

            GlStateManager.disableBlend();
        }
        
        private void drawButtonText(Minecraft mc) {
            int textColor = hovered ? 0xFFD700 : 0xFFFFFF;
            if (hoveredRace == raceIndex) textColor = 0xFFFFA500;
            
            drawCenteredString(
                mc.fontRenderer,
                displayString,
                x + width/2 + ICON_SIZE/2 + 5,
                y + (height - 8)/2,
                textColor
            );
        }
    }
}