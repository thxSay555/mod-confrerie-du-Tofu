package fr.wakfu.common.network;

import fr.wakfu.common.capabilities.RaceCapability;
import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.stats.IPlayerStats;
import fr.wakfu.stats.StatsProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetRace implements IMessage {
    private String race;

    public PacketSetRace() {}
    
    public PacketSetRace(String race) {
        this.race = race;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        race = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, race);
    }

    public static class Handler implements IMessageHandler<PacketSetRace, IMessage> {
        @Override
        public IMessage onMessage(PacketSetRace message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                RaceCapability.IRace raceCap = player.getCapability(RaceCapability.RACE_CAPABILITY, null);
                if (raceCap != null && !raceCap.hasRace()) {
                    raceCap.setRace(message.race);
                    applyRaceStats(player, message.race);
                    
                    // Envoi au client
                    WakfuNetwork.INSTANCE.sendTo(new SyncRaceCapability(message.race), player);
                    System.out.println("[Server] Race set: " + message.race);
                
                }
            });
            return null;
        }

        private void applyRaceStats(EntityPlayerMP player, String race) {
            IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
            if (stats != null) {
                switch(race) {
                    case "Cra":
                        stats.setWakfu(8);
                        stats.setStamina(12);
                        stats.setForce(4);
                        stats.setAgility(8);
                        break;
                    case "Iop":
                        stats.setWakfu(6);
                        stats.setStamina(18);
                        stats.setForce(10);
                        stats.setAgility(8);
                        break;
                    case "Sadida":
                        stats.setWakfu(10);
                        stats.setStamina(10);
                        stats.setForce(10);
                        stats.setAgility(5);
                        break;
                    case "Eliatrope":
                        stats.setWakfu(12);
                        stats.setStamina(8);
                        stats.setForce(5);
                        stats.setAgility(8);
                        break;
                }
               
            
            }
        }
    }
}