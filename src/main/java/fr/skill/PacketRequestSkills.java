package fr.skill;

import io.netty.buffer.ByteBuf;
import fr.skill.network.PacketSyncSkills;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketRequestSkills implements IMessage {
    public PacketRequestSkills() {}
    @Override public void toBytes(ByteBuf buf) {}
    @Override public void fromBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketRequestSkills, IMessage> {
        @Override
        public IMessage onMessage(PacketRequestSkills msg, MessageContext ctx) {
            // server thread
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                List<String> ids = PlayerSkillHelper.getUnlockedSkillIdsOrdered(player);
                WakfuNetwork.INSTANCE.sendTo(new PacketSyncSkills(ids), player);
            });
            return null;
        }
    }
}
