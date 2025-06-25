package test;

public interface IAnimationTickable {
    /** Appelé chaque tick pour mettre à jour l'animation. */
    void tick();

    /**
     * Retourne le compteur de ticks actuel pour piloter les animations.
     * @return nombre de ticks écoulés
     */
    int tickTimer();
}