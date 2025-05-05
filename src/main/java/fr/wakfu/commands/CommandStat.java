package fr.wakfu.commands;

import fr.wakfu.network.SyncStatsMessage;
import fr.wakfu.network.WakfuNetwork;
import fr.wakfu.stats.IPlayerStats;
import fr.wakfu.stats.StatsProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CommandStat extends CommandBase {

    @Override
    public String getName() {
        return "stat";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/stat <joueur> <Force|Stamina|Wakfu|Agility|Intensity> <valeur>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(new TextComponentString("§cUsage: /stat <joueur> <stat> <valeur>"));
            return;
        }

        EntityPlayerMP target;

        try {
            target = getPlayer(server, sender, args[0]);
        } catch (CommandException e) {
            sender.sendMessage(new TextComponentString("§cJoueur introuvable."));
            return;
        }

        String stat = args[1];
        int value;

        try {
            value = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponentString("§cLa valeur doit être un nombre entier."));
            return;
        }

        IPlayerStats stats = target.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats == null) {
            sender.sendMessage(new TextComponentString("§cErreur : joueur sans stats."));
            return;
        }

        switch (stat.toLowerCase()) {
            case "force":
                stats.setForce(value);
                break;
            case "stamina":
                stats.setStamina(value);
                break;
            case "wakfu":
                stats.setWakfu(value);
                break;
            case "agility":
                stats.setAgility(value);
                break;
            case "intensity":
                stats.setIntensity(value);
                break;
            default:
                sender.sendMessage(new TextComponentString("§cStat inconnue. Utilise Force, Stamina, Wakfu, Agility ou Intensity."));
                return;
        }

        // Prépare le NBT
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Force",     stats.getForce());
        tag.setInteger("Stamina",   stats.getStamina());
        tag.setInteger("Wakfu",     stats.getWakfu());
        tag.setInteger("Agility",   stats.getAgility());
        tag.setInteger("Intensity", stats.getIntensity());
        tag.setInteger("Level", stats.getLevel());
        tag.setInteger("Xp", stats.getXp());
        tag.setInteger("XpToNext", stats.getXpToNextLevel());
        tag.setInteger("SkillPoints", stats.getSkillPoints());

        // Envoie au client
        WakfuNetwork.INSTANCE.sendTo(new SyncStatsMessage(tag), target);

        sender.sendMessage(new TextComponentString("§a" + stat + " de " + target.getName() + " mise à " + value));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, Arrays.asList("Force", "Stamina", "Wakfu", "Agility", "Intensity"));
        }
        return super.getTabCompletions(server, sender, args, pos);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // Nécessite d'être OP
    }
}
