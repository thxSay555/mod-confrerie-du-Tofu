// SyncStatsMessage.java
package fr.wakfu.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SyncStatsMessage implements IMessage {
    private NBTTagCompound nbt;

    // Constructeur vide requis par Forge
    public SyncStatsMessage() { }

    public SyncStatsMessage(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // Sérialisation du NBT dans le buffer
        ByteBufUtils.writeTag(buf, nbt);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Désérialisation du NBT depuis le buffer
        this.nbt = ByteBufUtils.readTag(buf);
    }

    public NBTTagCompound getTag() {
        return nbt;
    }

    // Handler statique
    public static class Handler implements IMessageHandler<SyncStatsMessage, IMessage> {
        @Override
        public IMessage onMessage(SyncStatsMessage msg, MessageContext ctx) {
            // Côté client uniquement
            if (ctx.side == Side.CLIENT) {
                // Planifier la tâche sur le thread client
                net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> {
                    // Récupère le joueur client et sa capability
                    net.minecraft.entity.player.EntityPlayer player =
                        net.minecraft.client.Minecraft.getMinecraft().player;
                    fr.wakfu.stats.IPlayerStats stats =
                        player.getCapability(fr.wakfu.stats.StatsProvider.PLAYER_STATS, null);
                    if (stats == null) return;

                    NBTTagCompound tag = msg.getTag();
                    stats.setForce(tag.getInteger("Force"));
                    stats.setStamina(tag.getInteger("Stamina"));
                    stats.setWakfu(tag.getInteger("Wakfu"));
                    stats.setAgility(tag.getInteger("Agility"));
                    stats.setLevel(tag.getInteger("Level"));
                    stats.setSkillPoints(tag.getInteger("SkillPoints"));
                    stats.setXp(tag.getInteger("Xp"));
                    stats.setXpToNextLevel(tag.getInteger("XpToNext"));
                    stats.setIntensity(tag.getInteger("Intensity")); // Ajoutez cette ligne
                });
            }
            return null; // pas de réponse
        }
    }
}
