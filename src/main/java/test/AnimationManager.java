// src/main/java/test/AnimationManager.java
package test;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager des animations par joueur.
 * Délègue le listing et le parsing à AnimationLoader.
 */
public class AnimationManager {
    private final Map<String, AnimationInstance> instances = new HashMap<>();

    public AnimationManager() {
        // on n'appelle plus loadAll() ici, c'est fait statiquement par AnimationLoader
        System.out.println("[AnimationManager] Chargé : " + AnimationLoader.listAnimations());
    }

    /**
     * Démarre et renvoie une instance temporaire pour ce joueur + animation.
     * Replace toute instance existante pour ce joueur.
     */
    public AnimationInstance startAnimation(String playerName, String animationName) {
        Animation anim = AnimationLoader.getAnimation(animationName);
        if (anim == null) return null;
        AnimationInstance inst = new AnimationInstance(anim);
        instances.put(playerName, inst);
        return inst;
    }

    /** Arrête l'animation en cours pour le joueur (s'il en avait une). */
    public void stopAnimation(String playerName) {
        instances.remove(playerName);
    }

    /** Récupère l'instance en cours pour le joueur, ou null. */
    public AnimationInstance getInstance(String playerName) {
        return instances.get(playerName);
    }
}
