package test;

import fr.wakfu.WakfuMod;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import test.PacketAnimationControl.Type;

/**
 * Commande serveur pour tester une animation.
 */
public class CommandAnimationTest extends CommandBase {
    @Override
    public String getName() {
        return "animation_test";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/animation_test <animationName>";
    }

   
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.sendMessage(new TextComponentString("Cette commande est uniquement pour un joueur."));
            return;
        }
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString("Usage: " + getUsage(sender)));
            return;
        }

        String animName = args[0];
        // Vérification préalable
        Animation anim = AnimationLoader.getAnimation(animName);
        if (anim == null) {
            sender.sendMessage(new TextComponentString("§cAnimation inconnue : " + animName));
            sender.sendMessage(new TextComponentString("§7Animations disponibles : " +
                String.join(", ", AnimationLoader.listAnimations())));
            return;
        }

        EntityPlayerMP playerMP = (EntityPlayerMP) sender;
        String playerName = playerMP.getName();

        // Démarre côté serveur (optionnel)
        WakfuMod.animationManager.startAnimation(playerName, animName);

        // Envoi du packet au client
        WakfuNetwork.INSTANCE.sendTo(
            new PacketAnimationControl(playerName, animName, Type.START),
            playerMP
        );

        sender.sendMessage(new TextComponentString("§aPlaying animation: " + animName));
    }
}
