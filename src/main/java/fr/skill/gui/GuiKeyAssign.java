package fr.skill.gui;

import fr.skill.SkillKeybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Simple GUI modal : "Press any key to assign to this skill" - capture la touche et l'assigne.
 */
public class GuiKeyAssign extends GuiScreen {

    private final String skillId;
    private final Minecraft mc = Minecraft.getMinecraft();

    public GuiKeyAssign(String skillId) {
        this.skillId = skillId;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        String title = "Assign a key for: " + skillId;
        drawCenteredString(fontRenderer, title, width / 2, height / 2 - 10, 0xFFFFFF);
        drawCenteredString(fontRenderer, "(press ESC to cancel)", width / 2, height / 2 + 10, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }
        // assign and close
        SkillKeybinds.assignKey(skillId, keyCode);
        mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
