package fr.skill.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import fr.skill.PlayerSkillHelper;
import fr.skill.Skill;
import fr.skill.SkillRegistry;

/**
 * Client -> Server packet to request using a skill.
 * targetEntityId = -1 if no target.
 */
public class PacketUseSkill implements IMessage {
    private String skillId;
    private int targetEntityId;

    public PacketUseSkill() {}

    public PacketUseSkill(String skillId, int targetEntityId) {
        this.skillId = skillId;
        this.targetEntityId = targetEntityId;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, skillId == null ? "" : skillId);
        buf.writeInt(targetEntityId);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.skillId = ByteBufUtils.readUTF8String(buf);
        this.targetEntityId = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketUseSkill, IMessage> {
        @Override
        public IMessage onMessage(PacketUseSkill msg, MessageContext ctx) {
            // execute on server thread
            ctx.getServerHandler().player.getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;
                if (player == null || msg.skillId == null || msg.skillId.isEmpty()) return;

                // check player actually has the skill
                if (!PlayerSkillHelper.playerHasSkill(player, msg.skillId)) {
                    player.sendMessage(new TextComponentString("Vous n'avez pas ce skill."));
                    return;
                }

                // check cooldown
                long rem = PlayerSkillHelper.getRemainingCooldownMs(player, msg.skillId);
                if (rem > 0) {
                    player.sendMessage(new TextComponentString("Skill en cooldown : " + (int)Math.ceil(rem/1000.0) + "s"));
                    return;
                }

                // Optional: check costs/stamina - implement as needed

                // find target entity if any
                Entity target = null;
                if (msg.targetEntityId != -1) {
                    target = player.world.getEntityByID(msg.targetEntityId);
                }

                // get skill definition and execute server-side
                Skill skill = SkillRegistry.getSkill(msg.skillId);
                if (skill == null) {
                    player.sendMessage(new TextComponentString("Skill introuvable: " + msg.skillId));
                    return;
                }

                try {
                    // call the skill's server-side active use method
                    // depending on your Skill API the method name may differ; adjust if necessary
                    skill.onActiveUse(player, target);

                    // apply cooldown server-side (skill.getCooldownSeconds() must exist)
                    int cdSec = Math.max(0, skill.getCooldownSeconds());
                    if (cdSec > 0) {
                        PlayerSkillHelper.setCooldown(player, msg.skillId, cdSec * 1000L);
                    }

                    // Optionally deduct costs here (wakfu/stamina)

                } catch (Exception ex) {
                    ex.printStackTrace();
                    player.sendMessage(new TextComponentString("Erreur en exécutant le skill."));
                }
            });
            return null;
        }
    }
}
