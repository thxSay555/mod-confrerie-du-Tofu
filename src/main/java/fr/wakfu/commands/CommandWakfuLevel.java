package fr.wakfu.commands;

import fr.wakfu.stats.IPlayerStats;
import fr.wakfu.stats.StatsProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandWakfuLevel extends CommandBase {

    @Override
    public String getName() {
        return "wakfu";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/wakfu";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) sender;
        IPlayerStats stats = player.getCapability(StatsProvider.PLAYER_STATS, null);
        if (stats == null) return;

     
            int level = stats.getLevel();
            int points = stats.getSkillPoints();
            int intensity = stats.getIntensity();
            player.sendMessage(new TextComponentString("§b[Niveau Wakfu] §7Tu es niveau §a" + level + "§7, avec §e" + points + " §7points de compétence."));
            player.sendMessage(new TextComponentString("intensité= §a"+intensity));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // tout le monde peut l’utiliser
    }
    
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
