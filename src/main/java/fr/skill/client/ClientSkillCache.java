package fr.skill.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stocke la liste d'ids débloqués côté client (pour le GUI).
 * Toujours manipulé sur thread client (handler packet scheduleTask).
 */
public class ClientSkillCache {

    private static volatile List<String> unlocked = new ArrayList<>();

    public static void setUnlockedSkillIds(List<String> list) {
        if (list == null) unlocked = new ArrayList<>();
        else unlocked = new ArrayList<>(list);
    }

    public static List<String> getUnlockedSkillIds() {
        return Collections.unmodifiableList(unlocked);
    }

    public static void clear() { unlocked = new ArrayList<>(); }
}
