package fr.wakfu.commands;

import fr.wakfu.allies.AllyRegistry;
import fr.wakfu.allies.IAllyCapability;
import fr.wakfu.allies.network.PacketOpenAllyGui;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import com.mojang.authlib.GameProfile;

import javax.annotation.Nullable;
import java.util.*;

public class CommandAlly extends CommandBase {

    private static final List<String> SUBCOMMANDS = Arrays.asList("request", "respond", "remove", "list");

    @Override
    public String getName() {
        return "ally";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ally <request|respond|remove|list> [player ...]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
    /**
     * Exécution de la commande.
     * /ally request <player1> <player2> ...
     * /ally respond <player>
     * /ally remove <player>
     * /ally list
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.sendMessage(new TextComponentString("Commande pour joueurs seulement."));
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if (args.length == 0) {
            player.sendMessage(new TextComponentString("Usage: " + getUsage(sender)));
            return;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "request":
                handleRequest(server, player, args);
                return;

            case "respond":
                handleRespond(server, player, args);
                return;

            case "remove":
                handleRemove(server, player, args);
                return;

            case "list":
                handleList(server, player);
                return;

            default:
                player.sendMessage(new TextComponentString("Sous-commande inconnue. Usage: " + getUsage(sender)));
        }
    }

    // ----- Handlers -----

    private void handleRequest(MinecraftServer server, EntityPlayerMP sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString("Usage: /ally request <joueur1> <joueur2> ..."));
            return;
        }

        // Collecte des noms fournis (dédupliquer et ignorer le nom du sender)
        Set<String> targets = new LinkedHashSet<>();
        for (int i = 1; i < args.length; i++) {
            String name = args[i].trim();
            if (name.isEmpty()) continue;
            if (name.equalsIgnoreCase(sender.getName())) {
                sender.sendMessage(new TextComponentString("Tu ne peux pas t'ajouter toi-même, cervelle de Iop !!!"));
                continue;
            }
            targets.add(name);
        }

        if (targets.isEmpty()) {
            sender.sendMessage(new TextComponentString("Aucun joueur valide fourni."));
            return;
        }

        List<String> notFound = new ArrayList<>();
        List<String> sentTo = new ArrayList<>();

        for (String targetName : targets) {
            EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(targetName);
            if (target == null) {
                notFound.add(targetName);
                continue;
            }

            // Envoi du message cliquable à chaque cible
            ITextComponent msg = new TextComponentString(sender.getName() + " te demande en allié. ");
            TextComponentString clickable = new TextComponentString("[Répondre]");
            clickable.getStyle()
                    .setColor(TextFormatting.GREEN)
                    .setUnderlined(true)
                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ally respond " + sender.getName()))
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Cliquer pour ouvrir la demande d'allié")));
            msg.appendSibling(clickable);
            target.sendMessage(msg);
            sentTo.add(target.getName());
        }

        // Feedback à l'expéditeur
        if (!sentTo.isEmpty()) {
            sender.sendMessage(new TextComponentString("Demande envoyée à : " + String.join(", ", sentTo)));
        }
        if (!notFound.isEmpty()) {
            sender.sendMessage(new TextComponentString("Joueurs introuvables : " + String.join(", ", notFound)));
        }
    }

    private void handleRespond(MinecraftServer server, EntityPlayerMP player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(new TextComponentString("Usage: /ally respond <player>"));
            return;
        }
        String requesterName = args[1];
        EntityPlayerMP requester = server.getPlayerList().getPlayerByUsername(requesterName);
        if (requester == null) {
            player.sendMessage(new TextComponentString("Le joueur " + requesterName +  " n'a pas été trouvé."));
            return;
        }
        // Packet pour demander au client d'ouvrir la GUI (le destinataire est 'player')
        PacketOpenAllyGui packet = new PacketOpenAllyGui(requester.getUniqueID(), requester.getName());
        WakfuNetwork.INSTANCE.sendTo(packet, player);
    }

    private void handleRemove(MinecraftServer server, EntityPlayerMP player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(new TextComponentString("Usage: /ally remove <player>"));
            return;
        }
        String targetName = args[1];
        EntityPlayerMP rem = server.getPlayerList().getPlayerByUsername(targetName);

        IAllyCapability cap = player.getCapability(AllyRegistry.ALLY_CAPABILITY, null);
        if (cap == null) {
            player.sendMessage(new TextComponentString("Aucune capability d'alliés trouvée."));
            return;
        }

        if (rem != null) {
            // joueur en ligne -> suppression par UUID
            if (cap.isAlly(rem.getUniqueID())) {
                cap.removeAlly(rem.getUniqueID());
                player.sendMessage(new TextComponentString("Vous n'êtes plus alliés avec " + rem.getName() + "."));
            } else {
                player.sendMessage(new TextComponentString("Ce joueur n'est pas dans ta liste d'alliés."));
            }
        } else {
            // tentative de résolution via GameProfileCache pour suppression hors-ligne (pratique)
            UUID resolved = resolveUUIDFromName(server, targetName);
            if (resolved == null) {
                player.sendMessage(new TextComponentString("Impossible de trouver ce joueur (en ligne ou en cache)."));
                return;
            }
            if (cap.isAlly(resolved)) {
                cap.removeAlly(resolved);
                player.sendMessage(new TextComponentString("Allié hors-ligne supprimé (UUID: " + resolved.toString().substring(0, 8) + "...)"));
            } else {
                player.sendMessage(new TextComponentString("Ce joueur (hors-ligne) n'est pas dans ta liste d'alliés."));
            }
        }
    }

    private void handleList(MinecraftServer server, EntityPlayerMP player) {
        IAllyCapability mycap = player.getCapability(AllyRegistry.ALLY_CAPABILITY, null);
        if (mycap == null) {
            player.sendMessage(new TextComponentString("Aucune capability d'alliés trouvée."));
            return;
        }
        if (mycap.getAllies().isEmpty()) {
            player.sendMessage(new TextComponentString("Tu n'as pas d'alliés."));
            return;
        }

        player.sendMessage(new TextComponentString("===== Liste d'alliés ====="));
        for (UUID u : mycap.getAllies()) {
            String resolvedName = resolvePlayerName(server, u);
            boolean online = (server.getPlayerList().getPlayerByUUID(u) != null);

            TextComponentString line = new TextComponentString("- " + resolvedName + " ");
            if (online) {
                TextComponentString onlineTag = new TextComponentString("[En ligne]");
                onlineTag.getStyle().setColor(TextFormatting.GREEN)
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Cliquer pour supprimer cet allié")))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ally remove " + resolvedName));
                line.appendSibling(onlineTag);
            } else {
                TextComponentString offlineTag = new TextComponentString("[Hors-ligne]");
                offlineTag.getStyle().setColor(TextFormatting.GRAY)
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Le joueur est hors-ligne; suppression possible via /ally remove <nom> si le nom est connu.)")));
                line.appendSibling(offlineTag);
            }

            player.sendMessage(line);
        }
    }

    // ----- Tab completion -----

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, SUBCOMMANDS.toArray(new String[0]));
        }

        String sub = args[0].toLowerCase();

        // Pour request : autocomplétion de noms (dernier argument) - supporte plusieurs noms
        if ("request".equals(sub)) {
            String[] online = server.getPlayerList().getOnlinePlayerNames();
            return getListOfStringsMatchingLastWord(args, online);
        }

        // respond/remove -> proposer noms en ligne (1 seul argument attendu)
        if ("respond".equals(sub) || "remove".equals(sub)) {
            String[] online = server.getPlayerList().getOnlinePlayerNames();
            return getListOfStringsMatchingLastWord(args, online);
        }

        // fallback : aucune suggestion
        return Collections.emptyList();
    }

    /**
     * Indique pour quelles positions d'args l'auto-complétion doit proposer des noms d'utilisateur.
     * (utile pour certaines implémentations / outils)
     */
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        if (args.length == 0) return false;
        String sub = args[0].toLowerCase();
        if ("request".equals(sub)) {
            return index >= 1; // request <player1> <player2> ...
        }
        return ("respond".equals(sub) && index == 1) || ("remove".equals(sub) && index == 1);
    }

    // ----- Résolution utilitaire -----

 // Remplace l'ancienne méthode resolvePlayerName par celle-ci
    private String resolvePlayerName(MinecraftServer server, UUID uuid) {
        if (uuid == null) return "<inconnu>";

        // 1) Joueur en ligne ?
        EntityPlayerMP online = server.getPlayerList().getPlayerByUUID(uuid);
        if (online != null) return online.getName();

        // 2) Essayer le cache de profils (dernier nom connu)
        try {
            GameProfile profile = server.getPlayerProfileCache().getProfileByUUID(uuid);
            if (profile != null && profile.getName() != null && !profile.getName().isEmpty()) {
                return profile.getName();
            }
        } catch (Throwable ignored) {}

        // 3) Fallback lisible (UUID abrégé)
        String s = uuid.toString();
        return s.length() > 8 ? s.substring(0, 8) + "..." : s;
    }

    // Remplace l'ancienne méthode resolveUUIDFromName par celle-ci
    private UUID resolveUUIDFromName(MinecraftServer server, String name) {
        if (name == null || name.isEmpty()) return null;

        // Si le joueur est en ligne, retourne son UUID directement
        EntityPlayerMP online = server.getPlayerList().getPlayerByUsername(name);
        if (online != null) return online.getUniqueID();

        // Sinon, tenter via PlayerProfileCache : getGameProfileForUsername(name)
        try {
            GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(name);
            if (profile != null) return profile.getId();
        } catch (Throwable ignored) {}

        // Impossible de résoudre
        return null;
    }
}
