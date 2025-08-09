package fr.wakfu.network;

import fr.wakfu.stats.StatsProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateStatsMessage implements IMessage {

    private NBTTagCompound data;

    public UpdateStatsMessage() {}

    public UpdateStatsMessage(NBTTagCompound data) {
        this.data = data;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, data);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        data = ByteBufUtils.readTag(buf);
    }

    public static class Handler implements IMessageHandler<UpdateStatsMessage, IMessage> {
        @Override
        public IMessage onMessage(UpdateStatsMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player != null) {
                    player.getCapability(StatsProvider.PLAYER_STATS, null)
                        .deserializeNBT(message.data);
                }
            });
            return null;
        }
    }

    public NBTTagCompound getData() {
        return data;
    }
}