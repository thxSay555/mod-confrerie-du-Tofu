package fr.wakfu.common.network;

import fr.wakfu.common.capabilities.RaceCapability;
import fr.wakfu.network.SyncStatsMessage;
import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.stats.IPlayerStats;
import fr.wakfu.stats.StatsProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetRace implements IMessage {
    String race;

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

 // PacketSetRace.java (côté serveur)
 // PacketSetRace.java
    public static class Handler implements IMessageHandler<PacketSetRace, IMessage> {
        @Override
        public IMessage onMessage(PacketSetRace message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                RaceCapability.IRace raceCap = player.getCapability(RaceCapability.RACE_CAPABILITY, null);
                if (raceCap != null) {
                    raceCap.setRace(message.race);
                    // Sauvegarde explicite dans les données du joueur
                    NBTTagCompound data = player.getEntityData();
                    data.setTag("RaceData", RaceCapability.RACE_CAPABILITY.getStorage().writeNBT(
                        RaceCapability.RACE_CAPABILITY, 
                        raceCap, 
                        null
                    ));
                    // Applique les stats immédiatement
                    applyRaceStats(player, message.race);
                    WakfuNetwork.INSTANCE.sendTo(new SyncRaceCapability(message.race), player);
                }
            });
            return null;
        }
    }
        

        private static  void applyRaceStats(EntityPlayerMP player, String race) {
            IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
            if (stats != null) {
                switch(race) {
                    case "Cra":
                        stats.setWakfu(9);
                        stats.setStamina(10);
                        stats.setForce(6);
                        stats.setAgility(8);
                        break;
                    case "Iop":
                        stats.setWakfu(6);
                        stats.setStamina(18);
                        stats.setForce(10);
                        stats.setAgility(8);
                        break;
                    
                    case "Eliatrope":
                        stats.setWakfu(14);
                        stats.setStamina(8);
                        stats.setForce(5);
                        stats.setAgility(10);
                        break;
                    case "Sadida":
                        stats.setWakfu(10);
                        stats.setStamina(10);
                        stats.setForce(8);
                        stats.setAgility(6);
                        break;
                    case "Huppermage":
                    	stats.setWakfu(12);
                    	stats.setStamina(10);
                    	stats.setForce(8);
                    	stats.setAgility(9);
                    	break;
                    
                    case "Steamer":
                    	stats.setWakfu(6);
                    	stats.setStamina(8);
                    	stats.setForce(4);
                    	stats.setAgility(6);
                    	break;
                }
               
            
            }
            IPlayerStats stats1 = player.getCapability(StatsProvider.PLAYER_STATS, null);
            if (stats1 != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("Level", stats1.getLevel());
                tag.setInteger("Xp", stats1.getXp());
                tag.setInteger("Force", stats1.getForce());
                tag.setInteger("Stamina", stats1.getStamina());
                tag.setInteger("Wakfu", stats1.getWakfu());
                tag.setInteger("Agility", stats1.getAgility());
                tag.setInteger("SkillPoints", stats1.getSkillPoints());
                tag.setInteger("XpToNext", stats1.getXpToNextLevel());
                tag.setInteger("Intensity", stats1.getIntensity());
                tag.setFloat("CurrentWakfu", stats1.getCurrentWakfu());
                tag.setFloat("CurrentStamina", stats1.getCurrentStamina());
               
               
                // ... autres données ...
                WakfuNetwork.INSTANCE.sendTo(new SyncStatsMessage(tag), (EntityPlayerMP) player);
        }
        }
    }
