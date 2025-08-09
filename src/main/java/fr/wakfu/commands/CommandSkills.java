package fr.wakfu.commands;

import com.google.common.collect.Lists;
import fr.skill.PlayerSkillHelper;
import fr.skill.Skill;
import fr.skill.SkillRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Commande /skills avec complétion tab améliorée.
 *
 * Usage:
 *   /skills add <player> <skillId|skillName>
 *   /skills remove <player> <skillId|skillName>
 *   /skills give <skillId|skillName> [player]
 *   /skills list [player]
 *   /skills check <player> <skillId|skillName>
 */
public class CommandSkills extends CommandBase {

    @Override
    public String getName() {
        return "skills";
    }

    @Override
    public String getUsage(net.minecraft.command.ICommandSender sender) {
        return "/skills <add|remove|give|list|check> ...";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // op required
    }

    @Override
    public void execute(MinecraftServer server, net.minecraft.command.ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) throw new WrongUsageException(getUsage(sender));

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "add":
                cmdAdd(server, sender, args);
                break;
            case "remove":
                cmdRemove(server, sender, args);
                break;
            case "give":
                cmdGive(server, sender, args);
                break;
            case "list":
                cmdList(server, sender, args);
                break;
            case "check":
                cmdCheck(server, sender, args);
                break;
            default:
                throw new WrongUsageException(getUsage(sender));
        }
    }

    /* ------- subcommands implementations ------- */

    private void cmdAdd(MinecraftServer server, net.minecraft.command.ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) throw new WrongUsageException("Usage: /skills add <player> <skillId|skillName>");
        EntityPlayerMP target = getPlayer(server, sender, args[1]);
        String skillArg = combineArgs(args, 2);
        String skillId = resolveSkillId(skillArg);
        if (skillId == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Skill introuvable pour: " + skillArg));
            return;
        }
        boolean changed = PlayerSkillHelper.addSkillToPlayer(target, skillId);
        if (changed) {
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Skill '" + skillId + "' ajouté à " + target.getName()));
            target.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Vous avez débloqué le skill : " + skillId));
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Le joueur a déjà ce skill."));
        }
    }

    private void cmdRemove(MinecraftServer server, net.minecraft.command.ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) throw new WrongUsageException("Usage: /skills remove <player> <skillId|skillName>");
        EntityPlayerMP target = getPlayer(server, sender, args[1]);
        String skillArg = combineArgs(args, 2);
        String skillId = resolveSkillId(skillArg);
        if (skillId == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Skill introuvable pour: " + skillArg));
            return;
        }
        boolean removed = PlayerSkillHelper.removeSkillFromPlayer(target, skillId);
        if (removed) {
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Skill '" + skillId + "' retiré de " + target.getName()));
            target.sendMessage(new TextComponentString(TextFormatting.RED + "Le skill " + skillId + " a été retiré."));
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Le joueur ne possédait pas ce skill."));
        }
    }

    private void cmdGive(MinecraftServer server, net.minecraft.command.ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) throw new WrongUsageException("Usage: /skills give <skillId|skillName> [player]");
        String skillArg = combineArgs(args, 1);
        String skillId = resolveSkillId(skillArg);
        if (skillId == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Skill introuvable pour: " + skillArg));
            return;
        }

        EntityPlayerMP target = null;
        if (args.length >= 3) {
            target = getPlayer(server, sender, args[2]);
        } else {
            // no target specified -> send to command sender if player
            if (sender instanceof EntityPlayerMP) target = (EntityPlayerMP) sender;
            else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Vous devez préciser un joueur si vous n'êtes pas joueur."));
                return;
            }
        }

        boolean changed = PlayerSkillHelper.addSkillToPlayer(target, skillId);
        if (changed) {
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Skill '" + skillId + "' donné à " + target.getName()));
            target.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Vous avez reçu le skill : " + skillId));
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Le joueur a déjà ce skill."));
        }
    }

    private void cmdList(MinecraftServer server, net.minecraft.command.ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP target;
        if (args.length >= 2) {
            target = getPlayer(server, sender, args[1]);
        } else {
            if (sender instanceof EntityPlayerMP) target = (EntityPlayerMP) sender;
            else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Syntaxe: /skills list <player>"));
                return;
            }
        }
        List<String> ids = PlayerSkillHelper.getUnlockedSkillIdsOrdered(target);
        if (ids.isEmpty()) {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + target.getName() + " n'a aucun skill."));
            return;
        }
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Skills de " + target.getName() + " :"));
        for (String id : ids) {
            Skill s = SkillRegistry.getSkill(id);
            String display = id + (s != null ? (" (" + s.getName() + ")") : "");
            sender.sendMessage(new TextComponentString(" - " + display));
        }
    }

    private void cmdCheck(MinecraftServer server, net.minecraft.command.ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) throw new WrongUsageException("Usage: /skills check <player> <skillId|skillName>");
        EntityPlayerMP target = getPlayer(server, sender, args[1]);
        String skillArg = combineArgs(args, 2);
        String skillId = resolveSkillId(skillArg);
        if (skillId == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Skill introuvable pour: " + skillArg));
            return;
        }
        boolean has = PlayerSkillHelper.playerHasSkill(target, skillId);
        sender.sendMessage(new TextComponentString((has ? TextFormatting.GREEN : TextFormatting.RED) + target.getName() + (has ? " possède " : " ne possède pas ") + skillId));
    }

    /* ------- utils ------- */

    private static String combineArgs(String[] arr, int start) {
        if (start >= arr.length) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < arr.length; i++) {
            if (i > start) sb.append(' ');
            sb.append(arr[i]);
        }
        return sb.toString().trim();
    }

    /**
     * Résout soit : id existant dans SkillRegistry, soit recherche par nom (equalsIgnoreCase puis containsIgnoreCase).
     * Retourne l'id (ex: "wakfu:fireball") ou null si introuvable.
     */
    @Nullable
    private static String resolveSkillId(String arg) {
        if (arg == null || arg.isEmpty()) return null;
        // 1) direct ID
        Skill direct = SkillRegistry.getSkill(arg);
        if (direct != null) return direct.getId();

        // 2) exact name match
        for (Skill s : SkillRegistry.getAll()) {
            if (s.getName().equalsIgnoreCase(arg)) return s.getId();
        }

        // 3) contains (partial) match
        String lc = arg.toLowerCase(Locale.ROOT);
        for (Skill s : SkillRegistry.getAll()) {
            if (s.getName().toLowerCase(Locale.ROOT).contains(lc)) return s.getId();
        }

        // no match
        return null;
    }

    /* ------- Tab completion améliorée ------- */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, net.minecraft.command.ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "add", "remove", "give", "list", "check");
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        // args[1] suggestions
        if (args.length == 2) {
            switch (sub) {
                case "add":
                case "remove":
                case "check":
                case "list":
                    // these expect a player name as second arg
                    return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                case "give":
                    // give <skill> [player] -> second arg is skill
                    return getSkillIdAndNameSuggestions(args);
                default:
                    return Lists.newArrayList();
            }
        }

        // args[2] suggestions
        if (args.length >= 3) {
            switch (sub) {
                case "add":
                case "remove":
                case "check":
                    // third arg should be a skill id or name
                    return getSkillIdAndNameSuggestions(args);
                case "give":
                    // third arg = player target
                    return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                default:
                    return Lists.newArrayList();
            }
        }

        return Lists.newArrayList();
    }

    /**
     * Construit et retourne des suggestions (ids + noms) filtrées et dédupliquées.
     */
    private List<String> getSkillIdAndNameSuggestions(String[] args) {
        Set<String> candidates = new LinkedHashSet<>();
        String lastWord = args[args.length - 1].toLowerCase(Locale.ROOT);

        for (Skill s : SkillRegistry.getAll()) {
            String id = s.getId();
            String name = s.getName();
            if (id.toLowerCase(Locale.ROOT).startsWith(lastWord) || name.toLowerCase(Locale.ROOT).startsWith(lastWord)
                    || id.toLowerCase(Locale.ROOT).contains(lastWord) || name.toLowerCase(Locale.ROOT).contains(lastWord)) {
                candidates.add(id);
                candidates.add(name);
            }
        }

        // fallback : if nothing matches prefix, suggest all ids/names (best-effort)
        if (candidates.isEmpty()) {
            for (Skill s : SkillRegistry.getAll()) {
                candidates.add(s.getId());
                candidates.add(s.getName());
            }
        }

        return getListOfStringsMatchingLastWord(args, candidates);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        if (args.length == 0) return false;
        String sub = args[0].toLowerCase(Locale.ROOT);
        if ("add".equals(sub) || "remove".equals(sub) || "check".equals(sub) || "list".equals(sub)) {
            return index == 1;
        }
        if ("give".equals(sub)) {
            // give <skill> [player] -> player is index 2
            return index == 2;
        }
        return false;
    }
}

