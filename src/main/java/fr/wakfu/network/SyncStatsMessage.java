package fr.wakfu.network;

import fr.wakfu.stats.IPlayerStats;
import fr.wakfu.stats.StatsProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncStatsMessage implements IMessage {
    private NBTTagCompound nbt;

    // Constructeur vide requis
    public SyncStatsMessage() { }

    public SyncStatsMessage(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

 // dans SyncStatsMessage.java

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);  // sérialise le NBT dans le buffer
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.nbt = ByteBufUtils.readTag(buf);  // lit le NBT depuis le buffer
    }


    public static class Handler implements IMessageHandler<SyncStatsMessage, IMessage> {
        @Override
        public IMessage onMessage(SyncStatsMessage message, MessageContext ctx) {
            // exécution côté client
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(() -> {
                IPlayerStats stats = Minecraft.getMinecraft()
                    .player.getCapability(StatsProvider.PLAYER_STATS, null);
                if (stats != null) {
                    stats.setForce(message.nbt.getInteger("Force"));
                    stats.setStamina(message.nbt.getInteger("Stamina"));
                    stats.setWakfu(message.nbt.getInteger("Wakfu"));
                    stats.setAgility(message.nbt.getInteger("Agility"));
                }
            });
            return null; // pas de réponse
        }
    }
}
