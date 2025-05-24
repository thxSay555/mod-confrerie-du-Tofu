package fr.wakfu.common.network;

import fr.wakfu.client.gui.GuiRaceSelection;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestRaceSelection implements IMessage {
    public PacketRequestRaceSelection() {}

    @Override public void fromBytes(ByteBuf buf) {}
    @Override public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketRequestRaceSelection, IMessage> {
        @Override
        public IMessage onMessage(PacketRequestRaceSelection message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Minecraft.getMinecraft().displayGuiScreen(new GuiRaceSelection());
                System.out.println("[Client] GUI ouvert !");
            });
            return null;
        }
    }
}