package test;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.*;

import java.io.*;
import java.net.URL;
import java.net.JarURLConnection;
import java.util.*;
import java.util.jar.JarFile;

public class AnimationLoader {
    private static final Logger LOGGER = LogManager.getLogger("WakfuAnimation");
    private static final Map<String, Animation> animations = new LinkedHashMap<>();
    private static final String ANIM_DIR = "assets/wakfu/animations/";

    static {
        loadAll();
    }

    /** Liste immuable des clés d’animations disponibles */
    public static Set<String> listAnimations() {
        return Collections.unmodifiableSet(animations.keySet());
    }

    /** Récupère l’Animation par son nom, ou null si non chargée */
    public static Animation getAnimation(String name) {
        return animations.get(name);
    }

    /** Scan file:// et jar:// pour tous les .animation.json sous assets/wakfu/animations/ */
    private static void loadAll() {
        try {
            Enumeration<URL> urls = 
                AnimationLoader.class.getClassLoader().getResources(ANIM_DIR);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if ("file".equals(url.getProtocol())) {
                    scanDirectory(new File(url.toURI()));
                } else if ("jar".equals(url.getProtocol())) {
                    scanJar(url);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Erreur pendant le scan des animations", e);
        }
        LOGGER.info("Animations chargées : {}", animations.keySet());
    }

    private static void scanDirectory(File dir) {
        if (!dir.isDirectory()) return;
        File[] files = dir.listFiles((d, n) -> n.endsWith(".animation.json"));
        if (files == null) return;
        for (File f : files) {
            loadFile(f.getName());
        }
    }

    private static void scanJar(URL url) throws IOException {
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        try (JarFile jar = conn.getJarFile()) {
            jar.stream()
               .filter(e -> !e.isDirectory()
                         && e.getName().startsWith(ANIM_DIR)
                         && e.getName().endsWith(".animation.json"))
               .forEach(e -> loadFile(new File(e.getName()).getName()));
        }
    }

    /** Charge et parse un seul fichier <fileName>.animation.json */
    private static void loadFile(String fileName) {
        String resourcePath = "animations/" + fileName;
        ResourceLocation rl = new ResourceLocation("wakfu", resourcePath);

        try {
            IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
            IResource res = rm.getResource(rl);
            JsonObject root;
            try (InputStreamReader in = new InputStreamReader(res.getInputStream())) {
                root = JsonParser.parseReader(in).getAsJsonObject();
            }

            JsonObject group = root.getAsJsonObject("animations");
            if (group == null) {
                LOGGER.warn("Pas de champ « animations » dans {}", fileName);
                return;
            }

            // Valeurs globales
            float globalLen = root.has("animation_length")
                            ? root.get("animation_length").getAsFloat()
                            : 1f;
            boolean globalLoop = root.has("loop") && root.get("loop").getAsBoolean();

            // Pour chaque sous‑animation (run, jump, etc.)
            for (Map.Entry<String, JsonElement> ent : group.entrySet()) {
                String animName = ent.getKey();
                try {
                    JsonObject animDef = ent.getValue().getAsJsonObject();

                    float length = animDef.has("animation_length")
                                 ? animDef.get("animation_length").getAsFloat()
                                 : globalLen;
                    boolean loop = animDef.has("loop")
                                 ? animDef.get("loop").getAsBoolean()
                                 : globalLoop;

                    GeckoLibAnimation gla = new GeckoLibAnimation(animName, length, loop);

                    JsonObject bones = animDef.getAsJsonObject("bones");
                    if (bones != null) {
                        for (Map.Entry<String, JsonElement> boneEnt : bones.entrySet()) {
                            String boneName   = boneEnt.getKey();
                            JsonObject boneDef= boneEnt.getValue().getAsJsonObject();

                            // --- ROTATION ---
                            if (boneDef.has("rotation")) {
                                JsonObject rotObj = boneDef.getAsJsonObject("rotation");
                                // cas statique : {"vector":[...]}
                                if (rotObj.has("vector") && rotObj.entrySet().size() == 1) {
                                    JsonArray vec = rotObj.getAsJsonArray("vector");
                                    float[] vals = {
                                        vec.get(0).getAsFloat(),
                                        vec.get(1).getAsFloat(),
                                        vec.get(2).getAsFloat()
                                    };
                                    gla.addKeyframe(boneName, "rotation", 0f, vals);
                                } else {
                                    // cas timestampé : {"0.0":{…},"0.25":{…},…}
                                    for (Map.Entry<String, JsonElement> kf : rotObj.entrySet()) {
                                        float t = Float.parseFloat(kf.getKey());
                                        JsonArray vec = kf.getValue()
                                                         .getAsJsonObject()
                                                         .getAsJsonArray("vector");
                                        float[] vals = {
                                            vec.get(0).getAsFloat(),
                                            vec.get(1).getAsFloat(),
                                            vec.get(2).getAsFloat()
                                        };
                                        gla.addKeyframe(boneName, "rotation", t, vals);
                                    }
                                }
                            }

                            // --- POSITION ---
                            if (boneDef.has("position")) {
                                JsonObject posObj = boneDef.getAsJsonObject("position");
                                if (posObj.has("vector") && posObj.entrySet().size() == 1) {
                                    JsonArray vec = posObj.getAsJsonArray("vector");
                                    float[] vals = {
                                        vec.get(0).getAsFloat(),
                                        vec.get(1).getAsFloat(),
                                        vec.get(2).getAsFloat()
                                    };
                                    gla.addKeyframe(boneName, "position", 0f, vals);
                                } else {
                                    for (Map.Entry<String, JsonElement> kf : posObj.entrySet()) {
                                        float t = Float.parseFloat(kf.getKey());
                                        JsonArray vec = kf.getValue()
                                                         .getAsJsonObject()
                                                         .getAsJsonArray("vector");
                                        float[] vals = {
                                            vec.get(0).getAsFloat(),
                                            vec.get(1).getAsFloat(),
                                            vec.get(2).getAsFloat()
                                        };
                                        gla.addKeyframe(boneName, "position", t, vals);
                                    }
                                }
                            }
                        }
                    }

                    animations.put(animName, new Animation(gla));
                    LOGGER.info("Animation chargée : {}", animName);

                } catch (Exception eAnim) {
                    LOGGER.error("Échec parse animation « {} » dans {} -- on continue", animName, fileName, eAnim);
                }
            }

        } catch (Exception ex) {
            LOGGER.error("Impossible de charger assets/wakfu/animations/{} :", fileName, ex);
        }
    }

}
