package test;

import fr.wakfu.WakfuMod;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import test.PacketAnimationControl.Type;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * /animation_test [<animName> | stop]
 *   - sans argument ou "stop" : arrête toutes les animations pour le joueur
 *   - sinon : lance l'animation spécifiée
 * Auto‑complétion toujours active pour les noms d'animations + "stop".
 */
public class CommandAnimationTest extends CommandBase {
    @Override
    public String getName() {
        return "animation_test";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/animation_test [<animationName> | stop]";
    }

    @Override
    public void execute(MinecraftServer server,
                        ICommandSender sender,
                        String[] args) {
        String player = sender.getName();
        // CAS STOP : soit args vide, soit "stop"
        if (args.length == 0 || "stop".equalsIgnoreCase(args[0])) {
            // On arrête l'animation côté serveur
            WakfuMod.proxy.getAnimationManager().stopAnimation(player);
            // On diffuse l'arrêt à tous les clients
            WakfuNetwork.sendToAll(
                new PacketAnimationControl(player, "", Type.STOP)
            );
            sender.sendMessage(new TextComponentString("Animations arrêtées."));
            return;
        }

        // CAS LANCEMENT : doit être un seul argument non‐stop
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString("Usage: " + getUsage(sender)));
            sender.sendMessage(new TextComponentString("Pour arrêter : /animation_test stop"));
            sender.sendMessage(new TextComponentString("Liste animations : " + AnimationLoader.listAnimations()));
            return;
        }

        String name = args[0];
        if (AnimationLoader.getAnimation(name) == null) {
            sender.sendMessage(new TextComponentString("Animation inconnue : " + name));
            sender.sendMessage(new TextComponentString("Liste animations : " + AnimationLoader.listAnimations()));
            return;
        }

        // Démarrage de l'animation
        AnimationInstance inst = WakfuMod.proxy
                                        .getAnimationManager()
                                        .startAnimation(player, name);
        if (inst == null) {
            sender.sendMessage(new TextComponentString("Impossible de lancer : " + name));
        } else {
            WakfuNetwork.sendToAll(
                new PacketAnimationControl(player, name, Type.START)
            );
            sender.sendMessage(new TextComponentString("Playing animation : " + name));
        }
    }

    /**
     * Auto‑complétion (Forge 1.12.2) :
     *   - "stop" 
     *   - noms d'animations
     */
    @Override
    @Nullable
    public List<String> getTabCompletions(MinecraftServer server,
                                          ICommandSender sender,
                                          String[] args,
                                          @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            // Construire la liste : "stop" + toutes les animations
            List<String> candidates = new ArrayList<>();
            candidates.add("stop");
            Set<String> allAnims = AnimationLoader.listAnimations();
            candidates.addAll(allAnims);

            return getListOfStringsMatchingLastWord(args, candidates);
        }
        return Collections.emptyList();
    }
}
