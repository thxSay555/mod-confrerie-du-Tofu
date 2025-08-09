package fr.skill.race.xelor;

import fr.skill.Skill;
import fr.skill.SkillRegistry;
import fr.wakfu.WakfuMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * Skill "Phase" (Xelor)
 *
 * Usage :
 *  - portée : 8 blocks
 *  - téléporte le joueur juste au-dessus du block visé (si dans la portée).
 *  - si la colonne X/Z a plusieurs blocks, on téléporte au top (ou posY + 5 si >5 blocks).
 *
 * Important : ce callback doit être exécuté côté serveur (la logique ici vérifie world.isRemote).
 */
public final class PhaseSkill {

	 // ID codé en dur (conforme à ta demande)
    public static final String ID = "wakfu:go";
    public static final String NAME = "Go";


    public static void register() {
        Skill phase = new Skill.Builder(ID, NAME)
                .unlockLevel(1)
                .type(Skill.SkillType.ACTIVE)
                .addCategory("race:xelor")
                .icon(new ResourceLocation(WakfuMod.MODID, "textures/gui/skill_icon/icon_vision_wakfu.png"))
                .cooldownSeconds(12)
                .wakfuCost(10)
                .staminaCost(4)
                .damage(0f)
                .appearsInRadial(true)
                .keybindAssignable(true)
                .usableByEntities(false)
                .onActiveUse((caster, target) -> {
                    // Ne PAS exécuter d'effets critiques côté client.
                    if (caster.world.isRemote) return;

                    // seule l'utilisation par joueur est supportée (on peut étendre pour entités)
                    if (!(caster instanceof EntityPlayerMP)) return;
                    EntityPlayerMP player = (EntityPlayerMP) caster;
                    World world = player.world;

                    final double range = 16.0;

                    // Raytrace pour trouver le block visé dans un range de 8
                    RayTraceResult ray = player.rayTrace(range, 1.0f);
                    if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK) {
                        player.sendMessage(new TextComponentString("Aucun bloc valide dans la portée (" + (int)range + " blocs)."));
                        return;
                    }

                    BlockPos hitPos = ray.getBlockPos();

                    // Vérification distance exacte (au cas où)
                    double dx = player.posX - hitPos.getX();
                    double dy = player.posY - hitPos.getY();
                    double dz = player.posZ - hitPos.getZ();
                    double distSq = dx*dx + dy*dy + dz*dz;
                    if (distSq > range * range) {
                        player.sendMessage(new TextComponentString("Le bloc visé est trop loin."));
                        return;
                    }

                    // Calcul du top de la colonne X/Z
                    BlockPos top = world.getTopSolidOrLiquidBlock(hitPos);
                    // top est la position du block le plus haut non-air (ou proche)
                    int blocksCount = top.getY() - hitPos.getY() + 1;
                    int targetY;
                    if (blocksCount <= 0) {
                        // Pas de block détecté au-dessus : on teleport juste au-dessus du hitPos
                        targetY = hitPos.getY();
                    } else if (blocksCount > 5) {
                        // si plus de 5 blocs dans la colonne, on limite à hitPos.y + 5
                        targetY = hitPos.getY() + 5;
                        if (targetY > world.getHeight() - 2) targetY = world.getHeight() - 2;
                    } else {
                        // sinon, teleport au top
                        targetY = top.getY();
                    }

                    // destination (juste au-dessus du block cible)
                    double destX = hitPos.getX() + 0.5;
                    double destY = targetY + 1.0; // au-dessus du bloc
                    double destZ = hitPos.getZ() + 0.5;

                    // sécurité : ne pas téléporter à l'intérieur d'un bloc solide.
                    if (!world.isAirBlock(new BlockPos(destX, destY, destZ))) {
                        // essayer de pousser d'un bloc plus haut si possible
                        if (destY + 1.0 <= world.getHeight() - 1 && world.isAirBlock(new BlockPos(destX, destY + 1.0, destZ))) {
                            destY += 1.0;
                    
                            return;
                        }
                    }

                    // Effectue la téléportation côté serveur proprement (pour playerMP)
                    // setPositionAndUpdate remet aussi le joueur correctement côté client.
                    player.setPositionAndUpdate(destX, destY, destZ);

                    // Feedback au joueur
                    player.sendMessage(new TextComponentString("Blink !!!"));
                })
                .build();

        SkillRegistry.register(phase);
    }
}
