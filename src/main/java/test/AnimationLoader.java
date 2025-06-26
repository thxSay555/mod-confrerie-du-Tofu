package test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.JarURLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Charge dynamiquement tous les JSON *.animation.json
 * depuis assets/wakfu/animations/, qu'ils soient dans un dossier
 * ou empaquetés dans un JAR.
 */
public class AnimationLoader {
    private static final Logger LOGGER = LogManager.getLogger("WakfuAnimation");
    private static final Map<String, Animation> animations = new HashMap<>();
    private static final String ANIM_DIR = "assets/wakfu/animation/";

    static {
        loadAll();
    }

    private static void loadAll() {
        try {
            // Cherche la ressource "assets/wakfu/animations/" dans le ClassLoader
            Enumeration<URL> urls = AnimationLoader.class.getClassLoader().getResources(ANIM_DIR);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    scanDirectory(new File(url.toURI()));
                } else if ("jar".equals(protocol)) {
                    scanJar(url);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Erreur lors du scan des animations", e);
        }
    }

    /** Scanne un dossier de dev/local pour y trouver tous les *.animation.json */
    private static void scanDirectory(File dir) {
        if (!dir.exists() || !dir.isDirectory()) return;
        for (File f : dir.listFiles((d, n) -> n.endsWith(".animation.json"))) {
            loadFile(f.getName());
        }
    }

    /** Scanne un JAR pour y trouver tous les entrées assets/wakfu/animations/*.animation.json */
    private static void scanJar(URL url) throws IOException {
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        try (JarFile jar = conn.getJarFile()) {
            jar.stream()
               .filter(e -> !e.isDirectory()
                         && e.getName().startsWith(ANIM_DIR)
                         && e.getName().endsWith(".animation.json"))
               .forEach(e -> {
                   String name = new File(e.getName()).getName();
                   loadResource(e.getName(), name);
               });
        }
    }

    /** Charge une ressource via ResourceManager et GSON */
    private static void loadResource(String assetPath, String fileName) {
        try {
            ResourceLocation loc = new ResourceLocation("wakfu", "animation/" + fileName);
            IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
            try (InputStreamReader in = new InputStreamReader(
                    rm.getResource(loc).getInputStream())) {
                JsonObject root = new Gson().fromJson(in, JsonObject.class);
                Animation anim = Animation.fromJson(root);
                animations.put(anim.getName(), anim);
                LOGGER.info("Animation chargée: {}", anim.getName());
            }
        } catch (Exception ex) {
            LOGGER.error("Impossible de charger {}", assetPath, ex);
        }
    }

    /** Pour la méthode scanDirectory, on connaît seulement le nom du fichier */
    private static void loadFile(String fileName) {
        loadResource(ANIM_DIR + fileName, fileName);
    }

    /** Récupère une animation par son nom (clé JSON). */
    public static Animation getAnimation(String name) {
        return animations.get(name);
    }

    /** 
     * Renvoie les clés de toutes les animations chargées (pour listing éventuel). 
     */
    public static Set<String> listAnimations() {
        return Collections.unmodifiableSet(animations.keySet());
    }
}