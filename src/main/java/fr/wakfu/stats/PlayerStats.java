package fr.wakfu.stats;

public class PlayerStats implements IPlayerStats {
    private float currentWakfu;
    private float currentStamina;

    private float regenMultiplier = 1.0f;
    private int force = 0;
    private int stamina = 0;
    private int wakfu = 0;
    private int agility = 0;

    private float wakfuRegen = 0.1f;
    private float staminaRegen = 0.2f;

    private float wakfuMultiplier = 10.0f;
    private float staminaMultiplier = 5.0f;

    private float wakfuAccum = 0f;
    private float staminaAccum = 0f;

    private int intensity = 0;

    // ----- Leveling and XP -----
    private int level = 1;
    private int skillPoints = 0;
    private int xp = 0;
    private int xpToNextLevel = getXpForNextLevel(level);

    @Override public int getForce() { return force; }
    @Override public void setForce(int v) { this.force = v; }
    @Override public void addForce(int a) { this.force += a; }

    @Override public int getStamina() { return stamina; }
    @Override public void setStamina(int v) {
        this.stamina = v;
        this.currentStamina = Math.min(currentStamina, getStaminaMax());
    }
    @Override public void addStamina(int a) { this.stamina += a; }

    @Override public int getWakfu() { return wakfu; }
    @Override public void setWakfu(int v) {
        this.wakfu = v;
        this.currentWakfu = Math.min(currentWakfu, getWakfuMax());
    }
    @Override public void addWakfu(int a) { this.wakfu += a; }

    @Override public int getAgility() { return agility; }
    @Override public void setAgility(int v) { this.agility = v; }
    @Override public void addAgility(int a) { this.agility += a; }

    @Override public float getWakfuRegeneration() { return wakfuRegen; }
    @Override public void setWakfuRegeneration(float v) { this.wakfuRegen = v; }

    @Override public float getStaminaRegeneration() { return staminaRegen; }
    @Override public void setStaminaRegeneration(float v) { this.staminaRegen = v; }

    @Override public float getWakfuMultiplier() { return wakfuMultiplier; }
    @Override public void setWakfuMultiplier(float v) { this.wakfuMultiplier = v; }

    @Override public float getStaminaMultiplier() { return staminaMultiplier; }
    @Override public void setStaminaMultiplier(float v) { this.staminaMultiplier = v; }

    @Override public float getRegenMultiplier() { return regenMultiplier; }
    @Override public void setRegenMultiplier(float v) { this.regenMultiplier = v; }

    @Override public float getCurrentWakfu() { return currentWakfu; }
    @Override public void setCurrentWakfu(float value) {
        this.currentWakfu = Math.max(0, Math.min(value, getWakfuMax()));
    }

    @Override public float getCurrentStamina() { return currentStamina; }
    @Override public void setCurrentStamina(float value) {
        this.currentStamina = Math.max(0, Math.min(value, getStaminaMax()));
    }

    public float getWakfuMax() { return wakfu * wakfuMultiplier; }
    public float getStaminaMax() { return stamina * staminaMultiplier; }

    // Regen tick
    @Override
    public void tickRegen() {
        float wRegen = wakfuRegen * regenMultiplier;
        float sRegen = staminaRegen * regenMultiplier;

        wakfuAccum += wRegen;
        int addW = (int) wakfuAccum;
        if (addW > 0) {
            setCurrentWakfu(currentWakfu + addW);
            wakfuAccum -= addW;
        }

        staminaAccum += sRegen;
        int addS = (int) staminaAccum;
        if (addS > 0) {
            setCurrentStamina(currentStamina + addS);
            staminaAccum -= addS;
        }
    }

    @Override public int getIntensity() { return intensity; }
    @Override public void setIntensity(int value) { this.intensity = Math.max(0, Math.min(100, value)); }
    @Override public void addIntensity(int amount) { setIntensity(this.intensity + amount); }

    // ----- Level & XP methods -----
    @Override public int getLevel() { return level; }
    @Override public void setLevel(int lvl) { this.level = Math.max(1, lvl); }

    @Override public int getSkillPoints() { return skillPoints; }
    @Override public void setSkillPoints(int pts) { this.skillPoints = Math.max(0, pts); }
    @Override public void addSkillPoints(int amt) { this.skillPoints = Math.max(0, this.skillPoints + amt); }

    @Override public int getXp() { return xp; }
    @Override public void setXp(int xp) { this.xp = Math.max(0, xp); }

    @Override public int getXpToNextLevel() { return xpToNextLevel; }
    @Override public void setXpToNextLevel(int xpNext) { this.xpToNextLevel = Math.max(1, xpNext); }

    // Utility: calculate needed XP (exponential +10%)
    private int getXpForNextLevel(int currentLevel) {
        double base = 50 * Math.pow(1.1, currentLevel - 1);
        return (int) Math.round(base);
    }
}
