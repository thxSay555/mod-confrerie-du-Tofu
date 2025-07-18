package test;

import java.util.NavigableMap;
import java.util.TreeMap;

/** Stocke keyframes de rotation et de position pour un os. */
public class BoneAnimation {
    // timestamp → rotation [x,y,z] en degrés
    private final NavigableMap<Float, float[]> rotationKeyframes = new TreeMap<>();
    // timestamp → position [x,y,z] en blocs / unité
    private final NavigableMap<Float, float[]> positionKeyframes = new TreeMap<>();

    public void addRotationKeyframe(float time, float[] rot) {
        rotationKeyframes.put(time, rot);
    }
    public void addPositionKeyframe(float time, float[] pos) {
        positionKeyframes.put(time, pos);
    }

    /** Interpole de façon linéaire la rotation à `t` (avec loop si demandé). */
    public float[] getRotationAt(float t, float length, boolean loop) {
        return interpolate(rotationKeyframes, t, length, loop);
    }

    /** Interpole de façon linéaire la position à `t` (avec loop si demandé). */
    public float[] getPositionAt(float t, float length, boolean loop) {
        return interpolate(positionKeyframes, t, length, loop);
    }

    private float[] interpolate(NavigableMap<Float, float[]> map,
                                float t, float length, boolean loop) {
        if (map.isEmpty()) return new float[]{0f,0f,0f};
        float time = t;
        if (loop) time %= length;
        // si on est avant la première keyframe
        Float first = map.firstKey();
        if (time <= first) return map.get(first);
        // si on est après la dernière keyframe
        Float last = map.lastKey();
        if (time >= last) return map.get(last);

        // on récupère l’intervalle
        Float lower = map.floorKey(time);
        Float upper = map.ceilingKey(time);
        float[] a = map.get(lower);
        float[] b = map.get(upper);
        float span = upper - lower;
        float fraction = (time - lower) / span;
        // interpolation linéaire
        float[] out = new float[3];
        for (int i = 0; i < 3; i++) {
            out[i] = a[i] + (b[i] - a[i]) * fraction;
        }
        return out;
    }
}
