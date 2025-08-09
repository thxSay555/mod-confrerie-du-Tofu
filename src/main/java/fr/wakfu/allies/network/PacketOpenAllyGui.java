package fr.wakfu.allies.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketOpenAllyGui implements IMessage {
    public UUID requester;
    public String requesterName;

    public PacketOpenAllyGui() {}

    public PacketOpenAllyGui(UUID requester, String name) {
        this.requester = requester;
        this.requesterName = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boolean has = buf.readBoolean();
        if (has) {
            long most = buf.readLong();
            long least = buf.readLong();
            requester = new UUID(most, least);
            int len = buf.readInt();
            requesterName = "";
            if (len > 0) {
                byte[] b = new byte[len];
                buf.readBytes(b);
                requesterName = new String(b);
            }
        } else {
            requester = null;
            requesterName = "";
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (requester != null) {
            buf.writeBoolean(true);
            buf.writeLong(requester.getMostSignificantBits());
            buf.writeLong(requester.getLeastSignificantBits());
            byte[] b = requesterName == null ? new byte[0] : requesterName.getBytes();
            buf.writeInt(b.length);
            buf.writeBytes(b);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static class Handler implements IMessageHandler<PacketOpenAllyGui, IMessage> {
        @Override
        public IMessage onMessage(PacketOpenAllyGui message, MessageContext ctx) {
            // côté CLIENT
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
                net.minecraft.client.gui.GuiScreen gui = new fr.wakfu.allies.client.GuiAllyResponse(message.requester, message.requesterName);
                mc.displayGuiScreen(gui);
            });
            return null;
        }
    }
}
