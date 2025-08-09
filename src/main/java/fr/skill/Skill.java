package fr.skill;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Classe de base pour un Skill.
 *
 * Conception :
 * - id : identifiant unique, **écrit en dur** par l'auteur du skill (ex: "wakfu:fireball")
 * - name : nom lisible
 * - unlockLevel : niveau requis pour débloquer
 * - type : PASSIVE / ACTIVE / SELECTABLE
 * - categories : set de catégories (ex: "common" ou "race:iop"). Permet d'indiquer si commun ou lié à une race.
 * - icon : ResourceLocation pointant vers l'asset de l'icône
 * - active-specific : cooldown (s), wakfuCost, staminaCost, damage, flags pour radial/keybind/usableByEntities
 * - callbacks : interfaces fonctionnelles pour exécuter le code propre au skill (onUnlock, onPassiveTick, onActiveUse, onSelect)
 *
 * Usage recommandé :
 * - créer via Skill.Builder pour la plupart des skills
 * - pour des comportements complexes, on peut extends Skill et override les méthodes callback
 */
public class Skill {

    public enum SkillType { PASSIVE, ACTIVE, SELECTABLE }

    // Identifiant unique du skill (doit être codé en dur lors de la création)
    private final String id;

    // Nom lisible
    private final String name;

    // Niveau requis pour le débloquer
    private final int unlockLevel;

    // Type du skill
    private final SkillType type;

    // Catégories : ex "common", ou "race:iop", "race:eca"...
    private final Set<String> categories;

    // Chemin vers l'icône (assets)
    private final ResourceLocation icon;

    /* --- Active-only parameters (peuvent être nuls/0 si non applicable) --- */
    private final int cooldownSeconds;
    private final int wakfuCost;
    private final int staminaCost;
    private final float damage;
    private final boolean appearsInRadial;
    private final boolean keybindAssignable; // si true, l'utilisateur peut assigner une touche depuis UI
    private final boolean usableByEntities;  // si true, une autre entité peut déclencher ce skill (ex : mobs/pets)

    /* --- Callbacks : implémentent le "code à exécuter" du skill --- */
    @FunctionalInterface
    public interface UnlockAction { void onUnlock(EntityPlayer player); }

    @FunctionalInterface
    public interface PassiveAction { /** appelé régulièrement (ex: tick), implémentation choisie par le système */ void onTick(EntityPlayer player); }

    @FunctionalInterface
    public interface ActiveAction { /** exécution du skill actif : caster = celui qui l'utilise, target = cible possible (nullable) */ void onUse(EntityLivingBase caster, @Nullable Entity target); }

    @FunctionalInterface
    public interface SelectAction { void onSelect(EntityPlayer player); }

    private final UnlockAction unlockAction;
    private final PassiveAction passiveAction;
    private final ActiveAction activeAction;
    private final SelectAction selectAction;

    /* ----------------- Constructeur privé (utiliser le Builder) ----------------- */
    protected Skill(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id ne peut pas être null");
        this.name = Objects.requireNonNull(builder.name, "name ne peut pas être null");
        this.unlockLevel = Math.max(0, builder.unlockLevel);
        this.type = Objects.requireNonNull(builder.type, "type ne peut pas être null");
        this.categories = Collections.unmodifiableSet(new HashSet<>(builder.categories));
        this.icon = builder.icon;

        this.cooldownSeconds = builder.cooldownSeconds;
        this.wakfuCost = builder.wakfuCost;
        this.staminaCost = builder.staminaCost;
        this.damage = builder.damage;
        this.appearsInRadial = builder.appearsInRadial;
        this.keybindAssignable = builder.keybindAssignable;
        this.usableByEntities = builder.usableByEntities;

        this.unlockAction = builder.unlockAction;
        this.passiveAction = builder.passiveAction;
        this.activeAction = builder.activeAction;
        this.selectAction = builder.selectAction;
    }

    /* ----------------- Getters ----------------- */
    public String getId() { return id; }
    public String getName() { return name; }
    public int getUnlockLevel() { return unlockLevel; }
    public SkillType getType() { return type; }
    public Set<String> getCategories() { return categories; }
    public ResourceLocation getIcon() { return icon; }

    public int getCooldownSeconds() { return cooldownSeconds; }
    public int getWakfuCost() { return wakfuCost; }
    public int getStaminaCost() { return staminaCost; }
    public float getDamage() { return damage; }
    public boolean appearsInRadial() { return appearsInRadial; }
    public boolean isKeybindAssignable() { return keybindAssignable; }
    public boolean isUsableByEntities() { return usableByEntities; }

    /* ----------------- Callbacks wrappers (sécurisés null) ----------------- */
    public void onUnlock(EntityPlayer player) {
        if (unlockAction != null) unlockAction.onUnlock(player);
    }

    /**
     * Méthode à appeler par le système de tick si le skill est passif.
     */
    public void onPassiveTick(EntityPlayer player) {
        if (passiveAction != null) passiveAction.onTick(player);
    }

    /**
     * Méthode à appeler quand on active un skill actif.
     * @param caster entité déclenchante
     * @param target cible optionnelle
     */
    public void onActiveUse(EntityLivingBase caster, @Nullable Entity target) {
        if (activeAction != null) activeAction.onUse(caster, target);
    }

    public void onSelect(EntityPlayer player) {
        if (selectAction != null) selectAction.onSelect(player);
    }

    /* ----------------- Helpers pour catégories ----------------- */
    public boolean isCommon() {
        return categories.contains("common");
    }

    public boolean belongsToRace(String raceId) {
        return categories.contains("race:" + raceId.toLowerCase(Locale.ROOT));
    }

    /* ----------------- Serialization NBT (stocke les données "statique" du skill) ----------------- */
    public NBTTagCompound serializeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", id);
        tag.setString("name", name);
        tag.setInteger("unlockLevel", unlockLevel);
        tag.setString("type", type.name());
        if (icon != null) tag.setString("icon", icon.toString());

        tag.setInteger("cooldownSeconds", cooldownSeconds);
        tag.setInteger("wakfuCost", wakfuCost);
        tag.setInteger("staminaCost", staminaCost);
        tag.setFloat("damage", damage);
        tag.setBoolean("appearsInRadial", appearsInRadial);
        tag.setBoolean("keybindAssignable", keybindAssignable);
        tag.setBoolean("usableByEntities", usableByEntities);

        NBTTagList list = new NBTTagList();
        for (String c : categories) {
            NBTTagCompound el = new NBTTagCompound();
            el.setString("cat", c);
            list.appendTag(el);
        }
        tag.setTag("categories", list);
        return tag;
    }

    /**
     * Note : les callbacks ne sont pas sérialisés (ils sont du code). Cette méthode
     * permet de reconstruire un objet Skill "statique" depuis NBT si nécessaire.
     *
     * @param tag NBTTagCompound produit par serializeToNBT()
     * @param loaderCallback factory pour recréer les callbacks (géré côté mod)
     */
    public static Builder fromNBT(NBTTagCompound tag, CallbackLoader loaderCallback) {
        Builder b = new Builder(tag.getString("id"), tag.getString("name"))
                .unlockLevel(tag.getInteger("unlockLevel"))
                .type(SkillType.valueOf(tag.getString("type")));

        if (tag.hasKey("icon")) b.icon(new ResourceLocation(tag.getString("icon")));
        b.cooldownSeconds(tag.getInteger("cooldownSeconds"))
                .wakfuCost(tag.getInteger("wakfuCost"))
                .staminaCost(tag.getInteger("staminaCost"))
                .damage(tag.getFloat("damage"))
                .appearsInRadial(tag.getBoolean("appearsInRadial"))
                .keybindAssignable(tag.getBoolean("keybindAssignable"))
                .usableByEntities(tag.getBoolean("usableByEntities"));

        if (tag.hasKey("categories")) {
            NBTTagList list = tag.getTagList("categories", 10); // 10 = compound
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound el = list.getCompoundTagAt(i);
                b.addCategory(el.getString("cat"));
            }
        }

        // loaderCallback est responsable de fournir les callbacks corrects pour cet id
        if (loaderCallback != null) loaderCallback.loadCallbacksIntoBuilder(b);

        return b;
    }

    public interface CallbackLoader {
        /**
         * Implémenter pour remplir le builder avec les callbacks corrects pour le skill (par id).
         * Exemple: si id == "wakfu:fireball" on setActiveAction(...).
         * @param builder builder à compléter
         */
        void loadCallbacksIntoBuilder(Builder builder);
    }

    /* ----------------- Builder ----------------- */
    public static class Builder {
        private final String id;
        private final String name;
        private int unlockLevel = 0;
        private SkillType type = SkillType.PASSIVE;
        private final Set<String> categories = new HashSet<>();
        private ResourceLocation icon;

        private int cooldownSeconds = 0;
        private int wakfuCost = 0;
        private int staminaCost = 0;
        private float damage = 0f;
        private boolean appearsInRadial = false;
        private boolean keybindAssignable = false;
        private boolean usableByEntities = false;

        private UnlockAction unlockAction;
        private PassiveAction passiveAction;
        private ActiveAction activeAction;
        private SelectAction selectAction;

        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
    
        }

        public Builder unlockLevel(int lvl) { this.unlockLevel = Math.max(0, lvl); return this; }
        public Builder type(SkillType t) { this.type = t; return this; }
        public Builder addCategory(String cat) { this.categories.add(cat); return this; }
        public Builder categories(Collection<String> cats) { this.categories.addAll(cats); return this; }
        public Builder icon(ResourceLocation icon) { this.icon = icon; return this; }

        public Builder cooldownSeconds(int s) { this.cooldownSeconds = Math.max(0, s); return this; }
        public Builder wakfuCost(int c) { this.wakfuCost = Math.max(0, c); return this; }
        public Builder staminaCost(int c) { this.staminaCost = Math.max(0, c); return this; }
        public Builder damage(float d) { this.damage = d; return this; }
        public Builder appearsInRadial(boolean b) { this.appearsInRadial = b; return this; }
        public Builder keybindAssignable(boolean b) { this.keybindAssignable = b; return this; }
        public Builder usableByEntities(boolean b) { this.usableByEntities = b; return this; }

        public Builder onUnlock(UnlockAction a) { this.unlockAction = a; return this; }
        public Builder onPassiveTick(PassiveAction a) { this.passiveAction = a; return this; }
        public Builder onActiveUse(ActiveAction a) { this.activeAction = a; return this; }
        public Builder onSelect(SelectAction a) { this.selectAction = a; return this; }

        public Skill build() {
            // validation basique
            if (type == SkillType.ACTIVE && activeAction == null) {
                // autoriser mais log utile (ou lever une exception si vous préférez)
                // throw new IllegalStateException("Active skill doit fournir un onActiveUse()");
            }
            // par défaut, si pas de catégorie, marquer comme common
            if (categories.isEmpty()) categories.add("common");
            return new Skill(this);
        }
    }

    /* ----------------- equals/hashCode (basés sur id) ----------------- */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill)) return false;
        Skill skill = (Skill) o;
        return id.equals(skill.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Skill{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", type=" + type + '}';
    }
}
