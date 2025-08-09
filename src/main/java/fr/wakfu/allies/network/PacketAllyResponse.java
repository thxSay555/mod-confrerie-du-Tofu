package fr.wakfu.allies.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import java.util.UUID;

public class PacketAllyResponse implements IMessage {
    public UUID requester;
    public boolean accepted;

    public PacketAllyResponse() {}

    public PacketAllyResponse(UUID requester, boolean accepted) {
        this.requester = requester;
        this.accepted = accepted;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boolean has = buf.readBoolean();
        if (has) {
            requester = new UUID(buf.readLong(), buf.readLong());
        } else requester = null;
        accepted = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (requester != null) {
            buf.writeBoolean(true);
            buf.writeLong(requester.getMostSignificantBits());
            buf.writeLong(requester.getLeastSignificantBits());
        } else buf.writeBoolean(false);
        buf.writeBoolean(accepted);
    }

    public static class Handler implements IMessageHandler<PacketAllyResponse, IMessage> {
        @Override
        public IMessage onMessage(PacketAllyResponse message, MessageContext ctx) {
            MinecraftServer server = ctx.getServerHandler().player.getServer();
            server.addScheduledTask(() -> {
                EntityPlayerMP target = ctx.getServerHandler().player;
                if (message.requester == null) {
                    target.sendMessage(new TextComponentString(TextFormatting.RED + "Requête invalide."));
                    return;
                }
                EntityPlayerMP requester = server.getPlayerList().getPlayerByUUID(message.requester);
                if (requester == null) {
                    target.sendMessage(new TextComponentString(TextFormatting.RED + "Le joueur n'est plus en ligne."));
                    return;
                }

                if (message.accepted) {
                    // ajouter mutuellement
                    fr.wakfu.allies.AllyUtils.addMutualAllies(requester, target);
                    requester.sendMessage(new TextComponentString(TextFormatting.GREEN + target.getName() + " a accepté ta demande d'allié."));
                    target.sendMessage(new TextComponentString(TextFormatting.GREEN + "Vous êtes désormais alliés avec " + requester.getName() + "."));
                } else {
                    requester.sendMessage(new TextComponentString(TextFormatting.YELLOW + target.getName() + " a refusé ta demande d'allié."));
                    target.sendMessage(new TextComponentString(TextFormatting.GRAY + "Demande refusée."));
                }
            });
            return null;
        }
    }
}
