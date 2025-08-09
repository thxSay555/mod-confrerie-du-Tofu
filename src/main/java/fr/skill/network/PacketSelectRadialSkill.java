package fr.skill.network;

import fr.skill.PlayerSkillHelper;
import fr.skill.Skill;
import fr.skill.SkillRegistry;
import fr.wakfu.network.WakfuNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client -> Server: demande "j'ai sélectionné l'item/skill index X dans le radial".
 * Serveur : récupère la liste d'ids du joueur et active le skill correspondant.
 */
public class PacketSelectRadialSkill implements IMessageHandler<PacketSelectRadialSkill.Message, IMessage> {

    @Override
    public IMessage onMessage(Message message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            // schedule on server thread
            player.getServerWorld().addScheduledTask(() -> {
                try {
                    // récupère la liste débloquée côté serveur
                    java.util.List<String> unlocked = PlayerSkillHelper.getUnlockedSkillIdsOrdered(player);
                    if (unlocked == null || message.index < 0 || message.index >= unlocked.size()) {
                        // invalid index
                        player.sendStatusMessage(new net.minecraft.util.text.TextComponentString("Selection invalide."), false);
                        return;
                    }

                    String skillId = unlocked.get(message.index);
                    Skill s = SkillRegistry.getSkill(skillId);
                    if (s == null) {
                        player.sendStatusMessage(new net.minecraft.util.text.TextComponentString("Skill introuvable : " + skillId), false);
                        return;
                    }

                    // Call a server-side helper to actually perform validation & use (cooldowns, costs, permissions)
                    boolean used = PlayerSkillHelper.useSkillServer(player, skillId);
                    if (!used) {
                        player.sendStatusMessage(new net.minecraft.util.text.TextComponentString("Impossible d'activer le skill : " + s.getName()), false);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        return null;
    }

    public static class Message implements IMessage {
        public int index;

        public Message() {}

        public Message(int index) { this.index = index; }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.index = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(index);
        }
    }
}
