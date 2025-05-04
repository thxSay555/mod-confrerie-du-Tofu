package fr.wakfu.network;

import fr.wakfu.client.PlayerStatsScreen;
import fr.wakfu.stats.IPlayerStats;
import fr.wakfu.stats.StatsProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SyncStatsMessage implements IMessage {
    private NBTTagCompound nbt;

    public SyncStatsMessage() { }

    public SyncStatsMessage(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.nbt = ByteBufUtils.readTag(buf);
    }

    public NBTTagCompound getTag() {
        return nbt;
    }

    public static class Handler implements IMessageHandler<SyncStatsMessage, IMessage> {
        @Override
        public IMessage onMessage(SyncStatsMessage msg, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
                    if (stats == null) return;

                    NBTTagCompound tag = msg.getTag();
                    // Met à jour les stats CLIENT
                    stats.setForce(tag.getInteger("Force"));
                    stats.setStamina(tag.getInteger("Stamina"));
                    stats.setWakfu(tag.getInteger("Wakfu"));
                    stats.setAgility(tag.getInteger("Agility"));
                    stats.setLevel(tag.getInteger("Level"));
                    stats.setSkillPoints(tag.getInteger("SkillPoints"));
                    stats.setXp(tag.getInteger("Xp"));
                    stats.setXpToNextLevel(tag.getInteger("XpToNext"));
                    stats.setIntensity(tag.getInteger("Intensity"));
                    stats.setCurrentWakfu(tag.getFloat("CurrentWakfu"));   // GARANTI
                    stats.setCurrentStamina(tag.getFloat("CurrentStamina")); // GARANTI

                    // Rafraîchit l'interface si elle est ouverte
                    if (Minecraft.getMinecraft().currentScreen instanceof PlayerStatsScreen) {
                        ((PlayerStatsScreen) Minecraft.getMinecraft().currentScreen).initGui();
                    }
                });
            }
            return null;
        }
    }
}