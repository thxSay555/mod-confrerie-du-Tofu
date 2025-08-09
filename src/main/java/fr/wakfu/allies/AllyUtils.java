package fr.wakfu.allies;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityTameable;
import java.util.UUID;

public class AllyUtils {

    public static boolean arePlayersAllied(EntityPlayer a, EntityPlayer b) {
        if (a == null || b == null) return false;
        IAllyCapability capA = a.getCapability(AllyRegistry.ALLY_CAPABILITY, null);
        IAllyCapability capB = b.getCapability(AllyRegistry.ALLY_CAPABILITY, null);
        UUID idA = a.getUniqueID();
        UUID idB = b.getUniqueID();
        // check both directions to be safe
        return (capA != null && capA.isAlly(idB)) || (capB != null && capB.isAlly(idA));
    }

    /**
     * Vérifie si une entité est alliée au joueur : si c'est un joueur, compare la liste,
     * si c'est un EntityTameable, regarde le propriétaire et vérifie s'il est soit le joueur soit un allié du joueur.
     */
    public static boolean isEntityAlliedToPlayer(Entity entity, EntityPlayer player) {
        if (entity == null || player == null) return false;

        if (entity instanceof EntityPlayer) {
            return arePlayersAllied(player, (EntityPlayer) entity) || entity.getUniqueID().equals(player.getUniqueID());
        }

        if (entity instanceof EntityTameable) {
            EntityTameable t = (EntityTameable) entity;
            net.minecraft.entity.Entity owner = t.getOwner();
            if (owner instanceof EntityPlayer) {
                EntityPlayer ownerPlayer = (EntityPlayer) owner;
                if (ownerPlayer.getUniqueID().equals(player.getUniqueID())) return true;
                return arePlayersAllied(player, ownerPlayer);
            }
        }

        // TODO: si tu as d'autres entités apprivoisables (chevaux, etc.), adapte ici (getOwnerUniqueId/getOwnerId)
        return false;
    }

    public static void addMutualAllies(EntityPlayer a, EntityPlayer b) {
        if (a == null || b == null) return;
        IAllyCapability capA = a.getCapability(AllyRegistry.ALLY_CAPABILITY, null);
        IAllyCapability capB = b.getCapability(AllyRegistry.ALLY_CAPABILITY, null);
        if (capA != null) capA.addAlly(b.getUniqueID());
        if (capB != null) capB.addAlly(a.getUniqueID());
    }
}
