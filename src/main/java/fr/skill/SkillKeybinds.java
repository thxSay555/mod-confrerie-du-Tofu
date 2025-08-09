package fr.skill;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestion simple (mémoire) des assignations touches pour les skills.
 * stock: skillId -> keycode (LWJGL Keyboard key code)
 *
 * TODO : persist / sync via capability ou config
 */
public class SkillKeybinds {

    private static final Map<String, Integer> skillToKey = new HashMap<>();

    public static void assignKey(String skillId, int keyCode) {
        if (keyCode <= 0) skillToKey.remove(skillId);
        else skillToKey.put(skillId, keyCode);
        // TODO: persist/save to player capability or config
    }

    public static int getAssignedKeyCode(String skillId) {
        Integer i = skillToKey.get(skillId);
        return i == null ? 0 : i;
    }

    public static void clearAssignment(String skillId) {
        skillToKey.remove(skillId);
    }

    public static String getSkillForKey(int keyCode) {
        for (Map.Entry<String, Integer> e : skillToKey.entrySet()) {
            if (e.getValue() != null && e.getValue() == keyCode) return e.getKey();
        }
        return null;
    }
}
