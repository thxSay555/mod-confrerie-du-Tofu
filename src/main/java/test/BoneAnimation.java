package test;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Map;
import java.util.HashMap;

/**
 * Contient les key-frames de rotation et position pour un os.
 */
public class BoneAnimation {
    private final String boneName;
    private final Map<Float, float[]> rotations;
    private final Map<Float, float[]> positions;

    private BoneAnimation(String boneName, Map<Float, float[]> rotations, Map<Float, float[]> positions) {
        this.boneName = boneName;
        this.rotations = rotations;
        this.positions = positions;
    }

    public static BoneAnimation fromJson(String boneName, JsonObject obj) {
        Map<Float, float[]> rotMap = new HashMap<>();
        Map<Float, float[]> posMap = new HashMap<>();

        if (obj.has("rotation")) {
            for (Map.Entry<String, JsonElement> e : obj.getAsJsonObject("rotation").entrySet()) {
                float t = Float.parseFloat(e.getKey());
                JsonArray vec = e.getValue().getAsJsonObject().getAsJsonArray("vector");
                rotMap.put(t, new float[]{vec.get(0).getAsFloat(), vec.get(1).getAsFloat(), vec.get(2).getAsFloat()});
            }
        }
        if (obj.has("position")) {
            for (Map.Entry<String, JsonElement> e : obj.getAsJsonObject("position").entrySet()) {
                float t = Float.parseFloat(e.getKey());
                JsonArray vec = e.getValue().getAsJsonObject().getAsJsonArray("vector");
                posMap.put(t, new float[]{vec.get(0).getAsFloat(), vec.get(1).getAsFloat(), vec.get(2).getAsFloat()});
            }
        }
        return new BoneAnimation(boneName, rotMap, posMap);
    }

    public float[] getRotationAt(float time, float totalLength, boolean loop) {
        return interpolate(rotations, time, totalLength, loop);
    }

    public float[] getPositionAt(float time, float totalLength, boolean loop) {
        return interpolate(positions, time, totalLength, loop);
    }

    private float[] interpolate(Map<Float, float[]> map, float time, float length, boolean loop) {
        if (map.isEmpty()) return new float[]{0,0,0};
        if (loop) time %= length;
        Float prev = 0f, next = length;
        for (Float k : map.keySet()) {
            if (k <= time && k > prev) prev = k;
            if (k >= time && k < next) next = k;
        }
        float[] a = map.get(prev), b = map.get(next);
        if (prev.equals(next) || b == null) return a;
        float t = (time - prev) / (next - prev);
        return new float[]{
            a[0] + (b[0]-a[0])*t,
            a[1] + (b[1]-a[1])*t,
            a[2] + (b[2]-a[2])*t
        };
    }
}