package test;

import java.util.HashMap;
import java.util.Map;

/**
 * Représente l’animation brute lue depuis un fichier GeckoLib (.animation.json).
 * Stocke les keyframes de rotation et position pour chaque os.
 */
public class GeckoLibAnimation {
    private final String name;
    private final float length;
    private final boolean loop;
    private final Map<String, BoneAnimation> bones = new HashMap<>();

    public GeckoLibAnimation(String name, float length, boolean loop) {
        this.name   = name;
        this.length = length;
        this.loop   = loop;
    }

    public String getName() {
        return name;
    }

    public float getLength() {
        return length;
    }

    public boolean isLoop() {
        return loop;
    }

    public Map<String, BoneAnimation> getBones() {
        return bones;
    }

    /**
     * Ajoute une keyframe pour l'os spécifié.
     *
     * @param bone   Le nom de l'os (ex. "RightArm", "Body", etc.).
     * @param target "rotation" ou "position".
     * @param time   Timestamp (en secondes ou unités de timeline).
     * @param vals   Tableau de 3 floats : rotation en degrés ou position en blocs.
     */
    public void addKeyframe(String bone, String target, float time, float[] vals) {
        BoneAnimation anim = bones.computeIfAbsent(bone, k -> new BoneAnimation());
        if ("rotation".equalsIgnoreCase(target)) {
            anim.addRotationKeyframe(time, vals);
        } else if ("position".equalsIgnoreCase(target)) {
            anim.addPositionKeyframe(time, vals);
        }
    }
}
