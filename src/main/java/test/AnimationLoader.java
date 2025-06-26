package test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Charge dynamiquement tous les JSON d’animations du dossier assets/wakfu/animations.
 */
public class AnimationLoader {
    private static final Logger LOGGER = LogManager.getLogger("WakfuAnimation");
    private static final Map<String, Animation> animations = new HashMap<>();

    static {
        loadAll();
    }

    private static void loadAll() {
        IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
        for (String domain : rm.getResourceDomains()) {
            try {
                for (IResource res : rm.getAllResources(new ResourceLocation(domain, "animations/index.txt"))) {
                    try (InputStreamReader in = new InputStreamReader(res.getInputStream())) {
                        String[] files = new Gson().fromJson(in, String[].class);
                        for (String f : files) {
                            loadSingle(domain, f);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Impossible de lire animations/index.txt pour domaine {}", domain, e);
            }
        }
    }

    private static void loadSingle(String domain, String filename) {
        ResourceLocation loc = new ResourceLocation(domain, "animations/" + filename);
        try (InputStreamReader in = new InputStreamReader(
                Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream())) {
            JsonObject root = new Gson().fromJson(in, JsonObject.class);
            Animation anim = Animation.fromJson(root);
            animations.put(anim.getName(), anim);
            LOGGER.info("Chargée animation '{}'", anim.getName());
        } catch (Exception ex) {
            LOGGER.error("Erreur chargement de l'animation {}", filename, ex);
        }
    }

    public static Animation getAnimation(String name) {
        return animations.get(name);
    }
}