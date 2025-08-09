package fr.skill.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import fr.skill.capability.CapabilityPlayerSkills;
import fr.skill.capability.IPlayerSkills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Server -> Client packet: sends unlocked skill ids + map of cooldown expiry timestamps (millis).
 */
public class PacketSyncSkills implements IMessage {

    public List<String> ids = new ArrayList<>();
    public Map<String, Long> cooldownExpiry = new HashMap<>();

    public PacketSyncSkills() {}

    // convenience ctor (no cooldowns)
    public PacketSyncSkills(List<String> ids) {
        this(ids, null);
    }

    // full ctor
    public PacketSyncSkills(List<String> ids, Map<String, Long> cooldownExpiry) {
        this.ids = ids == null ? new ArrayList<>() : new ArrayList<>(ids);
        this.cooldownExpiry = cooldownExpiry == null ? new HashMap<>() : new HashMap<>(cooldownExpiry);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // write ids
        buf.writeInt(ids.size());
        for (String s : ids) ByteBufUtils.writeUTF8String(buf, s);
        // write cooldown map
        buf.writeInt(cooldownExpiry.size());
        for (Map.Entry<String, Long> e : cooldownExpiry.entrySet()) {
            ByteBufUtils.writeUTF8String(buf, e.getKey());
            buf.writeLong(e.getValue());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        ids = new ArrayList<>(size);
        for (int i = 0; i < size; i++) ids.add(ByteBufUtils.readUTF8String(buf));

        int mapSize = buf.readInt();
        cooldownExpiry = new HashMap<>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            String k = ByteBufUtils.readUTF8String(buf);
            long v = buf.readLong();
            cooldownExpiry.put(k, v);
        }
    }

    public static class Handler implements IMessageHandler<PacketSyncSkills, IMessage> {
        @Override
        public IMessage onMessage(PacketSyncSkills message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayerSP player = Minecraft.getMinecraft().player;
                if (player == null) return;

                // 1) write skills into client capability if present, else into PlayerPersisted NBT
                IPlayerSkills cap = player.getCapability(CapabilityPlayerSkills.PLAYER_SKILLS, null);
                if (cap != null) {
                    cap.setUnlocked(message.ids);
                } else {
                    NBTTagCompound root = player.getEntityData();
                    NBTTagCompound persisted = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
                    if (!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) root.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
                    NBTTagList list = new NBTTagList();
                    if (message.ids != null) {
                        for (String id : message.ids) list.appendTag(new NBTTagString(id));
                    }
                    persisted.setTag("wakfu_skills", list);
                    root.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
                }

                // 2) write cooldowns into PlayerPersisted NBT (client-side copy)
                if (message.cooldownExpiry != null && !message.cooldownExpiry.isEmpty()) {
                    NBTTagCompound root = player.getEntityData();
                    NBTTagCompound persisted = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
                    if (!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) root.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
                    NBTTagCompound cds = new NBTTagCompound();
                    for (Map.Entry<String, Long> e : message.cooldownExpiry.entrySet()) {
                        cds.setLong(e.getKey(), e.getValue());
                    }
                    persisted.setTag("wakfu_cooldowns", cds);
                    root.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
                }

               

                if (Minecraft.getMinecraft().currentScreen instanceof fr.skill.gui.RadialMenuGui) {
                    ((fr.skill.gui.RadialMenuGui) Minecraft.getMinecraft().currentScreen).onSkillsSynced();
                }
            });
            return null;
        }
    }
}
