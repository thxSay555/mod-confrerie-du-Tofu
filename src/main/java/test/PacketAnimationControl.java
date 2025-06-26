package test;

import fr.wakfu.WakfuMod;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

public class PacketAnimationControl implements IMessage {
    public enum Type { START, STOP }

    private String playerName;
    private String animationName;
    private Type type;

    // Constructeur vide requis par Forge
    public PacketAnimationControl() {}

    public PacketAnimationControl(String playerName, String animationName, Type type) {
        this.playerName     = playerName;
        this.animationName  = animationName;
        this.type           = type;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerName);
        ByteBufUtils.writeUTF8String(buf, animationName);
        buf.writeByte(type.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerName    = ByteBufUtils.readUTF8String(buf);
        this.animationName = ByteBufUtils.readUTF8String(buf);
        this.type          = Type.values()[buf.readByte()];
    }

    // Getter pour le client
    public String getPlayerName()    { return playerName; }
    public String getAnimationName() { return animationName; }
    public Type   getType()          { return type; }

    /** Handler côté client */
    public static class ClientHandler implements IMessageHandler<PacketAnimationControl, IMessage> {
        @Override
        public IMessage onMessage(PacketAnimationControl msg, MessageContext ctx) {
            // Exécute sur le thread client
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> {
                switch (msg.getType()) {
                    case START:
                        WakfuMod.animationManager
                            .startAnimation(msg.getPlayerName(), msg.getAnimationName());
                        break;
                    case STOP:
                        WakfuMod.animationManager
                            .stopAnimation(msg.getPlayerName());
                        break;
                }
            });
            return null;
        }
    }
}