package fr.skill;

import fr.skill.capability.CapabilityPlayerSkills;
import fr.skill.capability.IPlayerSkills;
import fr.skill.network.PacketSyncSkills;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper : utilise la capability si présente, sinon fallback NBT.
 * Maintenant gère aussi cooldown persistence and sync.
 */
public final class PlayerSkillHelper {

    private PlayerSkillHelper() {}

    public static List<String> getUnlockedSkillIdsOrdered(EntityPlayer player) {
        if (player == null) return new ArrayList<>();
        IPlayerSkills cap = player.hasCapability(CapabilityPlayerSkills.PLAYER_SKILLS, null)
                ? player.getCapability(CapabilityPlayerSkills.PLAYER_SKILLS, null)
                : null;
        if (cap != null) return cap.getUnlocked();

        // fallback to PlayerPersisted NBT
        NBTTagCompound root = player.getEntityData();
        if (!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) return new ArrayList<>();
        NBTTagCompound persisted = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persisted.hasKey("wakfu_skills")) return new ArrayList<>();
        NBTTagList list = persisted.getTagList("wakfu_skills", 8);
        List<String> out = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) out.add(list.getStringTagAt(i));
        return out;
    }

    /**
     * Add skill server-side only. Returns true if actually added.
     */
    public static boolean addSkillToPlayer(EntityPlayer player, String skillId) {
        if (player == null || skillId == null) return false;
        if (player.world.isRemote) return false; // server-side only

        // try capability first
        IPlayerSkills cap = player.getCapability(CapabilityPlayerSkills.PLAYER_SKILLS, null);
        boolean changed = false;
        if (cap != null) {
            changed = cap.addSkill(skillId);
        } else {
            // fallback: write to persisted NBT
            List<String> current = getUnlockedSkillIdsOrdered(player);
            if (!current.contains(skillId)) {
                current.add(skillId);
                writeListToPlayer(player, current);
                changed = true;
            }
        }

        if (changed) {
            // call onUnlock if defined server-side
            Skill s = SkillRegistry.getSkill(skillId);
            try {
                if (s != null && player instanceof EntityPlayerMP) s.onUnlock(player);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // sync to client (include cooldowns)
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP mp = (EntityPlayerMP) player;
                WakfuNetwork.INSTANCE.sendTo(new PacketSyncSkills(getUnlockedSkillIdsOrdered(mp), getCooldownMapForPlayer(mp)), mp);
            }
        }
        return changed;
    }

    public static boolean removeSkillFromPlayer(EntityPlayer player, String skillId) {
        if (player == null || skillId == null) return false;
        if (player.world.isRemote) return false;
        IPlayerSkills cap = player.getCapability(CapabilityPlayerSkills.PLAYER_SKILLS, null);
        boolean removed = false;
        if (cap != null) removed = cap.removeSkill(skillId);
        else {
            List<String> cur = getUnlockedSkillIdsOrdered(player);
            removed = cur.remove(skillId);
            if (removed) writeListToPlayer(player, cur);
        }
        if (removed && player instanceof EntityPlayerMP) {
            WakfuNetwork.INSTANCE.sendTo(new PacketSyncSkills(getUnlockedSkillIdsOrdered(player), getCooldownMapForPlayer((EntityPlayerMP) player)), (EntityPlayerMP) player);
        }
        return removed;
    }

    public static boolean playerHasSkill(EntityPlayer player, String skillId) {
        if (player == null || skillId == null) return false;
        IPlayerSkills cap = player.getCapability(CapabilityPlayerSkills.PLAYER_SKILLS, null);
        if (cap != null) return cap.hasSkill(skillId);
        return getUnlockedSkillIdsOrdered(player).contains(skillId);
    }

    /**
     * Fallback writer to PlayerPersisted NBT if capability not present.
     */
    public static void writeListToPlayer(EntityPlayer player, List<String> list) {
        if (player == null) return;
        if (player.world.isRemote) {
            System.out.println("[WAKFU-SKILLS] WARNING: writeListToPlayer called on client for " + player.getName());
            return;
        }
        NBTTagCompound root = player.getEntityData();
        NBTTagCompound persisted = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) root.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
        NBTTagList nlist = new NBTTagList();
        for (String id : list) nlist.appendTag(new NBTTagString(id));
        persisted.setTag("wakfu_skills", nlist);
        root.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
        System.out.println("[WAKFU-SKILLS] writeListToPlayer wrote " + list.size() + " ids for " + player.getName());
    }

    /**
     * Set cooldown on server and sync to client immediately for that player.
     * durationMs: milliseconds duration.
     */
    public static void setCooldown(EntityPlayer player, String skillId, long durationMs) {
        if (player == null || skillId == null) return;
        if (player.world.isRemote) return;
        long expiry = System.currentTimeMillis() + durationMs;

        NBTTagCompound root = player.getEntityData();
        NBTTagCompound persisted = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) root.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);

        NBTTagCompound cds = persisted.getCompoundTag("wakfu_cooldowns");
        cds.setLong(skillId, expiry);
        persisted.setTag("wakfu_cooldowns", cds);
        root.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);

        // if server-player, push a PacketSyncSkills (only this player) with updated cooldowns
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP mp = (EntityPlayerMP) player;
            List<String> ids = getUnlockedSkillIdsOrdered(mp);
            Map<String, Long> cooldownMap = getCooldownMapForPlayer(mp);
            WakfuNetwork.INSTANCE.sendTo(new PacketSyncSkills(ids, cooldownMap), mp);
        }
    }

    public static long getRemainingCooldownMs(EntityPlayer player, String skillId) {
        if (player == null || skillId == null) return 0L;
        NBTTagCompound root = player.getEntityData();
        if (!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) return 0L;
        NBTTagCompound persisted = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persisted.hasKey("wakfu_cooldowns")) return 0L;
        NBTTagCompound cds = persisted.getCompoundTag("wakfu_cooldowns");
        if (!cds.hasKey(skillId)) return 0L;
        long expiry = cds.getLong(skillId);
        long rem = expiry - System.currentTimeMillis();
        return rem > 0 ? rem : 0L;
    }

    /**
     * Utility to produce a map <skillId -> expiryMillis> for all known skills on the player.
     */
    public static Map<String, Long> getCooldownMapForPlayer(EntityPlayer player) {
        Map<String, Long> out = new HashMap<>();
        if (player == null) return out;
        NBTTagCompound root = player.getEntityData();
        if (!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) return out;
        NBTTagCompound persisted = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persisted.hasKey("wakfu_cooldowns")) return out;
        NBTTagCompound cds = persisted.getCompoundTag("wakfu_cooldowns");
        for (String key : cds.getKeySet()) {
            out.put(key, cds.getLong(key));
        }
        return out;
    }

	// Exemple placé dans fr.skill.PlayerSkillHelper
public static boolean useSkillServer(net.minecraft.entity.player.EntityPlayerMP player, String skillId) {
    if (player == null || skillId == null) return false;

    // basic validation : joueur possède-t-il le skill ?
    java.util.List<String> unlocked = getUnlockedSkillIdsOrdered(player);
    if (unlocked == null || !unlocked.contains(skillId)) return false;

    // récupère la Skill
    fr.skill.Skill skill = fr.skill.SkillRegistry.getSkill(skillId);
    if (skill == null) return false;

    // Vérifications serveur : cooldown, énergie, etc. (implémente selon ton système)
    long remaining = getRemainingCooldownMs(player, skillId); // à implémenter si tu as cooldowns
    if (remaining > 0) {
        player.sendStatusMessage(new net.minecraft.util.text.TextComponentString("En cooldown : " + (int)Math.ceil(remaining/1000.0) + "s"), true);
        return false;
    }

    // Exécute l'effet serveur-side :
    try {
        // Tentative d'appel via API : si tu as un callback stocké dans Skill, invoque-le
        // Exemple si Skill expose une méthode "useOnServer(EntityPlayerMP)"
        try {
            java.lang.reflect.Method m = skill.getClass().getMethod("useOnServer", net.minecraft.entity.player.EntityPlayerMP.class);
            m.invoke(skill, player);
            return true;
        } catch (NoSuchMethodException ex) {
            // fallback : si tu as PacketUseSkill server handling, tu peux directement exécuter le code
            // ou appeler une méthode générique placée dans Skill (à adapter selon ton code).
            // Pour l'instant on renvoie true pour indiquer que la demande a été traitée.
            // Log pour t'avertir :
            System.out.println("[WAKFU-SKILLS] Skill " + skillId + " n'a pas de useOnServer() accessible. Implémentez PlayerSkillHelper.useSkillServer pour exécuter l'effet.");
            return false;
        }
    } catch (Throwable t) {
        t.printStackTrace();
        return false;
    }
}
}
