package fr.wakfu.allies.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import fr.wakfu.allies.network.PacketAllyResponse;
import fr.wakfu.network.WakfuNetwork;

import java.io.IOException;
import java.util.UUID;

public class GuiAllyResponse extends GuiScreen {
    private final UUID requester;
    private final String requesterName;
    private GuiButton yes;
    private GuiButton no;

    public GuiAllyResponse(UUID requester, String requesterName) {
        this.requester = requester;
        this.requesterName = requesterName;
    }

    @Override
    public void initGui() {
        int w = this.width;
        int h = this.height;
        this.buttonList.clear();
        this.yes = new GuiButton(0, w / 2 - 50 - 10, h / 2, 50, 20, "Oui");
        this.no = new GuiButton(1, w / 2 + 10, h / 2, 50, 20, "Non");
        this.buttonList.add(yes);
        this.buttonList.add(no);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == yes) {
            // envoie packet accept
            WakfuNetwork.INSTANCE.sendToServer(new PacketAllyResponse(requester, true));
            this.mc.displayGuiScreen(null);
        } else if (button == no) {
            WakfuNetwork.INSTANCE.sendToServer(new PacketAllyResponse(requester, false));
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(this.fontRenderer, TextFormatting.YELLOW + requesterName + " te propose de devenir allié.", width / 2, height / 2 - 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false; // ne met pas en pause la partie
    }
}
