package fr.wakfu.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderPlayer;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ModelSwitcher {

    private static boolean useCustomModel = false;
    private static Field mainModelField;

    // Mémorise les modèles originaux pour chaque instance de RenderPlayer
    private static final Map<RenderPlayer, ModelBase> originalModels = new HashMap<>();

    static {
        try {
            mainModelField = RenderPlayer.class.getSuperclass().getDeclaredField("mainModel");
            mainModelField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCustomModelEnabled() {
        return useCustomModel;
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ModelSwitcher());
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        if (event.getMessage().equalsIgnoreCase("model")) {
            useCustomModel = !useCustomModel;
            System.out.println("[DEBUG] Custom model toggled: " + useCustomModel);
            event.setCanceled(true); // Empêche l'envoi du message dans le chat
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (!useCustomModel) return;

        RenderPlayer renderPlayer = event.getRenderer();
        if (!originalModels.containsKey(renderPlayer)) {
            try {
                ModelBase original = (ModelBase) mainModelField.get(renderPlayer);
                originalModels.put(renderPlayer, original);
                mainModelField.set(renderPlayer, new PlayerModel(0.0f, false));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (!useCustomModel) return;

        RenderPlayer renderPlayer = event.getRenderer();
        if (originalModels.containsKey(renderPlayer)) {
            try {
                mainModelField.set(renderPlayer, originalModels.get(renderPlayer));
                originalModels.remove(renderPlayer);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.RenderTickEvent event) {
        if (!useCustomModel || event.phase != TickEvent.Phase.END) return;

        AbstractClientPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            PlayerAnimation.applyJumpRotation(player, event.renderTickTime);
        }
    }
}
