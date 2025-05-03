package fr.wakfu.stats;

public interface IPlayerStats {
	void tickRegen();

    // --- Force ---
    int getForce();
    void setForce(int value);
    void addForce(int amount);

    // --- Stamina ---
    int getStamina();
    void setStamina(int value);
    void addStamina(int amount);

    // --- Wakfu ---
    int getWakfu();
    void setWakfu(int value);
    void addWakfu(int amount);

    // --- Agilité ---
    int getAgility();
    void setAgility(int value);
    void addAgility(int amount);

    // --- Régénération de base ---
    float getWakfuRegeneration();
    void setWakfuRegeneration(float value);
    float getStaminaRegeneration();
    void setStaminaRegeneration(float value);

    // --- Multiplicateurs de maximum ---
    float getWakfuMultiplier();
    void setWakfuMultiplier(float value);
    float getStaminaMultiplier();
    void setStaminaMultiplier(float value);
    
//Variable du jeu en live
    // --- Multiplicateur global de régénération ---
    float getRegenMultiplier();
    void setRegenMultiplier(float value);
    
    float getCurrentWakfu();
    void setCurrentWakfu(float value);

    float getCurrentStamina();
    void setCurrentStamina(float value);
    
 // --- Intensité ---
    int getIntensity();
    void setIntensity(int value);
    void addIntensity(int amount);
    
 //level and skill point
    
    int getLevel();
    void setLevel(int level);
    int getSkillPoints();
    void setSkillPoints(int points);
    void addSkillPoints(int amount);
    
 // Interface IPlayerStats
    int getXp();
    void setXp(int xp);
    int getXpToNextLevel();
    void setXpToNextLevel(int xp);


}
