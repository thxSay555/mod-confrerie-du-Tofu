package test;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import java.util.Map;
import java.util.HashMap;

/**
 * Représente une animation JSON telle que chargée.
 */
public class Animation {
    private final String name;
    private final boolean loop;
    private final float length;
    private final Map<String, BoneAnimation> bones;

    private Animation(String name, boolean loop, float length, Map<String, BoneAnimation> bones) {
        this.name = name;
        this.loop = loop;
        this.length = length;
        this.bones = bones;
    }

    public String getName() { return name; }
    public boolean isLoop() { return loop; }
    public float getLength() { return length; }
    public Map<String, BoneAnimation> getBones() { return bones; }

    public static Animation fromJson(JsonObject root) {
        String format = root.get("format_version").getAsString();
        JsonObject anims = root.getAsJsonObject("animations");
        if (anims.entrySet().isEmpty()) {
            throw new IllegalArgumentException("Aucune animation dans JSON");
        }
        Map.Entry<String, JsonElement> entry = anims.entrySet().iterator().next();
        String animName = entry.getKey();
        JsonObject data = entry.getValue().getAsJsonObject();

        boolean loop = data.has("loop") && data.get("loop").getAsBoolean();
        float len = data.get("animation_length").getAsFloat();

        Map<String, BoneAnimation> boneMap = new HashMap<>();
        JsonObject bonesJson = data.getAsJsonObject("bones");
        for (Map.Entry<String, JsonElement> be : bonesJson.entrySet()) {
            boneMap.put(be.getKey(), BoneAnimation.fromJson(be.getKey(), be.getValue().getAsJsonObject()));
        }

        return new Animation(animName, loop, len, boneMap);
    }
}