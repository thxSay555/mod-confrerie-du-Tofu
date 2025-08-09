package fr.skill;

import javax.annotation.Nullable;

import fr.skill.race.xelor.PhaseSkill;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry central des skills.
 *
 * - register(skill): enregistre un skill immutable (doit être fait à l'initialisation du mod).
 * - getSkill(id): récupère le Skill ou null.
 * - getAll(): collection immuable de tous les skills enregistrés.
 * - getSkillsFromIdsOrdered(ids): convertit une liste d'ids (dans l'ordre d'acquisition) en liste de Skills.
 *
 * Important : enregistrer les mêmes définitions côté client et serveur (mêmes ids & callbacks).
 */
public final class SkillRegistry {

    // map id -> Skill
    private static final Map<String, Skill> REGISTRY = new ConcurrentHashMap<>();

    private SkillRegistry() {
    	 PhaseSkill.register();
    }

    /**
     * Enregistre un skill. Si un skill avec le même id existe, lève une exception.
     * Faire les enregistrements pendant preInit/commonInit.
     */
    public static void register(Skill skill) {
        Objects.requireNonNull(skill, "skill ne peut pas être null");
        String id = skill.getId();
        Skill previous = REGISTRY.putIfAbsent(id, skill);
        if (previous != null) {
            throw new IllegalStateException("Skill déjà enregistré pour l'id '" + id + "' (previous: " + previous + ")");
        }
    }

    /**
     * Remplace (force) un skill dans la registry. Utile pour tests, hot-reload local, etc.
     */
    public static void registerOrReplace(Skill skill) {
        Objects.requireNonNull(skill);
        REGISTRY.put(skill.getId(), skill);
    }

    /** Récupère un skill par id, ou null si absent. */
    @Nullable
    public static Skill getSkill(@Nullable String id) {
        if (id == null) return null;
        return REGISTRY.get(id);
    }

    /** Récupère un skill par id, lève si introuvable. */
    public static Skill requireSkill(String id) {
        Skill s = getSkill(id);
        if (s == null) throw new NoSuchElementException("No Skill registered with id: " + id);
        return s;
    }

    /** Retourne toutes les skills enregistrées (collection immuable). */
    public static Collection<Skill> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    /** Return all skill ids registered. */
    public static Set<String> getAllIds() {
        return Collections.unmodifiableSet(REGISTRY.keySet());
    }

    /**
     * Transforme une liste d'ids (par ex. fournie par la capability du joueur, triée par acquisition)
     * en liste de Skills dans le même ordre.
     * Les ids inconnus sont ignorés (mais on garde l'ordre des autres).
     */
    public static List<Skill> getSkillsFromIdsOrdered(List<String> idsOrdered) {
        if (idsOrdered == null || idsOrdered.isEmpty()) return Collections.emptyList();
        List<Skill> out = new ArrayList<>(idsOrdered.size());
        for (String id : idsOrdered) {
            Skill s = getSkill(id);
            if (s != null) out.add(s);
        }
        return out;
    }

    /**
     * Convenience : retourne uniquement les skills de type ACTIVE à partir d'une liste d'ids ordonnée.
     */
    public static List<Skill> getActiveSkillsFromIdsOrdered(List<String> idsOrdered) {
        List<Skill> all = getSkillsFromIdsOrdered(idsOrdered);
        List<Skill> filtered = new ArrayList<>();
        for (Skill s : all) {
            if (s.getType() == Skill.SkillType.ACTIVE) filtered.add(s);
        }
        return filtered;
    }

    /** Clear registry (utile pour tests). */
    public static void clear() {
        REGISTRY.clear();
    }
}
