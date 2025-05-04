package fr.wakfu.stats;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerStats implements IPlayerStats {
    // Statistiques de base
    private int force;
    private int stamina;
    private int wakfu;
    private int agility;
    private int intensity;
    
    // Niveau et XP
    private int level = 1;
    private int skillPoints;
    private int xp;
    private int xpToNextLevel = getXpForNextLevel(level);
    
    // Valeurs actuelles
    private float currentWakfu;
    private float currentStamina;
    
    // Multiplicateurs
    private float regenMultiplier = 1.0f;
    private float wakfuMultiplier = 10.0f;
    private float staminaMultiplier = 5.0f;
    
    // Régénération
    private float wakfuRegen = 0.1f;
    private float staminaRegen = 0.2f;
    private float wakfuAccum;
    private float staminaAccum;

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        // Stats de base
        tag.setInteger("Force", force);
        tag.setInteger("Stamina", stamina);
        tag.setInteger("Wakfu", wakfu);
        tag.setInteger("Agility", agility);
        tag.setInteger("Intensity", intensity);
        
        // Leveling
        tag.setInteger("Level", level);
        tag.setInteger("SkillPoints", skillPoints);
        tag.setInteger("Xp", xp);
        tag.setInteger("XpToNext", xpToNextLevel);
        
        // Valeurs actuelles
        tag.setFloat("CurrentWakfu", currentWakfu);
        tag.setFloat("CurrentStamina", currentStamina);
        
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        // Stats de base
        force = nbt.getInteger("Force");
        stamina = nbt.getInteger("Stamina");
        wakfu = nbt.getInteger("Wakfu");
        agility = nbt.getInteger("Agility");
        intensity = nbt.getInteger("Intensity");
        
        // Leveling
        level = nbt.getInteger("Level");
        skillPoints = nbt.getInteger("SkillPoints");
        xp = nbt.getInteger("Xp");
        xpToNextLevel = nbt.getInteger("XpToNext");
        
        // Valeurs actuelles
        currentWakfu = nbt.getFloat("CurrentWakfu");
        currentStamina = nbt.getFloat("CurrentStamina");
    }

    @Override
    public void tickRegen() {
        // Régénération Wakfu
        float wRegen = wakfuRegen * regenMultiplier;
        wakfuAccum += wRegen;
        if (wakfuAccum >= 1.0f) {
            int add = (int) wakfuAccum;
            setCurrentWakfu(currentWakfu + add);
            wakfuAccum -= add;
        }
        
        // Régénération Stamina
        float sRegen = staminaRegen * regenMultiplier;
        staminaAccum += sRegen;
        if (staminaAccum >= 1.0f) {
            int add = (int) staminaAccum;
            setCurrentStamina(currentStamina + add);
            staminaAccum -= add;
        }
    }

    // Getters/Setters
    @Override public int getForce() { return force; }
    @Override public void setForce(int v) { force = v; }
    @Override public void addForce(int a) { force += a; }

    @Override public int getStamina() { return stamina; }
    @Override public void setStamina(int v) { 
        stamina = v;
        currentStamina = Math.min(currentStamina, getStaminaMax());
    }
    @Override public void addStamina(int a) { stamina += a; }

    @Override public int getWakfu() { return wakfu; }
    @Override public void setWakfu(int v) { 
        wakfu = v;
        currentWakfu = Math.min(currentWakfu, getWakfuMax());
    }
    @Override public void addWakfu(int a) { wakfu += a; }

    @Override public int getAgility() { return agility; }
    @Override public void setAgility(int v) { agility = v; }
    @Override public void addAgility(int a) { agility += a; }

    @Override public float getWakfuRegeneration() { return wakfuRegen; }
    @Override public void setWakfuRegeneration(float v) { wakfuRegen = v; }

    @Override public float getStaminaRegeneration() { return staminaRegen; }
    @Override public void setStaminaRegeneration(float v) { staminaRegen = v; }

    @Override public float getWakfuMultiplier() { return wakfuMultiplier; }
    @Override public void setWakfuMultiplier(float v) { wakfuMultiplier = v; }

    @Override public float getStaminaMultiplier() { return staminaMultiplier; }
    @Override public void setStaminaMultiplier(float v) { staminaMultiplier = v; }

    @Override public float getRegenMultiplier() { return regenMultiplier; }
    @Override public void setRegenMultiplier(float v) { regenMultiplier = v; }

    @Override public float getCurrentWakfu() { return currentWakfu; }
    @Override public void setCurrentWakfu(float v) { 
        currentWakfu = Math.max(0, Math.min(v, getWakfuMax()));
    }

    @Override public float getCurrentStamina() { return currentStamina; }
    @Override public void setCurrentStamina(float v) { 
        currentStamina = Math.max(0, Math.min(v, getStaminaMax()));
    }

    @Override public float getWakfuMax() { return wakfu * wakfuMultiplier; }
    @Override public float getStaminaMax() { return stamina * staminaMultiplier; }

    @Override public int getIntensity() { return intensity; }
    @Override public void setIntensity(int v) { intensity = Math.max(0, Math.min(100, v)); }
    @Override public void addIntensity(int a) { setIntensity(intensity + a); }

    @Override public int getLevel() { return level; }
    @Override public void setLevel(int lvl) { level = Math.max(1, lvl); }

    @Override public int getSkillPoints() { return skillPoints; }
    @Override public void setSkillPoints(int pts) { skillPoints = Math.max(0, pts); }
    @Override public void addSkillPoints(int amt) { skillPoints = Math.max(0, skillPoints + amt); }

    @Override public int getXp() { return xp; }
    @Override public void setXp(int xp) { this.xp = Math.max(0, xp); }

    @Override public int getXpToNextLevel() { return xpToNextLevel; }
    @Override public void setXpToNextLevel(int xpNext) { xpToNextLevel = Math.max(1, xpNext); }

    private int getXpForNextLevel(int currentLevel) {
        return (int) Math.round(50 * Math.pow(1.1, currentLevel - 1));
    }
}