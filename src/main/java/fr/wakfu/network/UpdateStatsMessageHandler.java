package fr.wakfu.network;

import fr.wakfu.stats.IPlayerStats;
import fr.wakfu.stats.StatsProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class UpdateStatsMessageHandler implements IMessageHandler<UpdateStatsMessage, IMessage> {

    @Override
    public IMessage onMessage(UpdateStatsMessage msg, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            // Planification sur le thread serveur
            player.getServerWorld().addScheduledTask(() -> {
                IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
                if (stats == null) return;

                NBTTagCompound tag = msg.getData();
                stats.setForce(tag.getInteger("Force"));
                stats.setStamina(tag.getInteger("Stamina"));
                stats.setWakfu(tag.getInteger("Wakfu"));
                stats.setAgility(tag.getInteger("Agility"));
                stats.setSkillPoints(tag.getInteger("SkillPoints"));

                // Envoi de la synchronisation au client
                WakfuNetwork.INSTANCE.sendTo(new SyncStatsMessage(tag), player);
            });
        }
        return null;
    }
}
