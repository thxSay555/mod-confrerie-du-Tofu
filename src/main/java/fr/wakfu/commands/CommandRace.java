package fr.wakfu.commands;

import fr.wakfu.common.network.PacketRequestRaceSelection;
import fr.wakfu.network.WakfuNetwork; // adapte si ton réseau est dans un autre package
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandRace extends CommandBase {

    @Override
    public String getName() {
        return "race";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/race §7- Ouvre l'interface de sélection de race";
    }

    /**
     * On laisse le required permission level à 0 et on gère la validation dans checkPermission/execute
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    /**
     * Autorise uniquement les joueurs en mode créatif à exécuter la commande.
     */
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            return player.capabilities.isCreativeMode;
        }
        return false;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Commande réservée aux joueurs en jeu."));
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;

        if (!player.capabilities.isCreativeMode) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Vous n'avez pas la permition"));
            return;
        }

     
        try {
            

            WakfuNetwork.INSTANCE.sendTo(new PacketRequestRaceSelection(), player);
            return;
        } catch (Throwable ignored) {
            // Si le réseau n'a pas cette méthode/instance, on passe à l'option fallback.
        }

       
    }
}
