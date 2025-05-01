package fr.wakfu.stats;

public class PlayerStats implements IPlayerStats {
    private float currentWakfu;
    private float currentStamina;

    private float regenMultiplier = 1.0f;
    private int force = 0;
    private int stamina = 0;
    private int wakfu = 0;
    private int agility = 0;

    private float wakfuRegen = 0.1f;   // par défaut
    private float staminaRegen = 0.2f; // par défaut

    private float wakfuMultiplier = 10.0f;
    private float staminaMultiplier = 5.0f;

    private float wakfuAccum = 0f;
    private float staminaAccum = 0f;

    // --- Force ---
    @Override public int getForce() { return force; }
    @Override public void setForce(int v) { this.force = v; }
    @Override public void addForce(int a) { this.force += a; }

    // --- Stamina ---
    @Override public int getStamina() { return stamina; }
    @Override public void setStamina(int v) {
        this.stamina = v;
        this.currentStamina = Math.min(currentStamina, getStaminaMax());
    }
    @Override public void addStamina(int a) { this.stamina += a; }

    // --- Wakfu ---
    @Override public int getWakfu() { return wakfu; }
    @Override public void setWakfu(int v) {
        this.wakfu = v;
        this.currentWakfu = Math.min(currentWakfu, getWakfuMax());
    }
    @Override public void addWakfu(int a) { this.wakfu += a; }

    // --- Agilité ---
    @Override public int getAgility() { return agility; }
    @Override public void setAgility(int v) { this.agility = v; }
    @Override public void addAgility(int a) { this.agility += a; }

    // --- Régénération ---
    @Override public float getWakfuRegeneration() { return wakfuRegen; }
    @Override public void setWakfuRegeneration(float v) { this.wakfuRegen = v; }

    @Override public float getStaminaRegeneration() { return staminaRegen; }
    @Override public void setStaminaRegeneration(float v) { this.staminaRegen = v; }

    // --- Multiplicateurs ---
    @Override public float getWakfuMultiplier() { return wakfuMultiplier; }
    @Override public void setWakfuMultiplier(float v) { this.wakfuMultiplier = v; }

    @Override public float getStaminaMultiplier() { return staminaMultiplier; }
    @Override public void setStaminaMultiplier(float v) { this.staminaMultiplier = v; }

    @Override public float getRegenMultiplier() { return regenMultiplier; }
    @Override public void setRegenMultiplier(float v) { this.regenMultiplier = v; }

    // --- Valeurs actuelles ---
    @Override public float getCurrentWakfu() { return currentWakfu; }
    @Override public void setCurrentWakfu(float value) {
        this.currentWakfu = Math.max(0, Math.min(value, getWakfuMax()));
    }

    @Override public float getCurrentStamina() { return currentStamina; }
    @Override public void setCurrentStamina(float value) {
        this.currentStamina = Math.max(0, Math.min(value, getStaminaMax()));
    }

    // --- Max calculé ---
    public float getWakfuMax() {
        return wakfu * wakfuMultiplier;
    }

    public float getStaminaMax() {
        return stamina * staminaMultiplier;
    }

    // --- Tick de régénération ---
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
}
