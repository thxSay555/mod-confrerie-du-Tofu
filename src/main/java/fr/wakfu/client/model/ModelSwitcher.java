package fr.wakfu.client.model;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;

public class ModelSwitcher {

    private static boolean useCustomModel = false;
    private ModelBase originalModel = null;

    // Référence au champ mainModel via réflexion
    private static Field mainModelField;
    public static boolean isCustomModelEnabled() {
        return useCustomModel;
    }
    // Skin personnalisé
    private static final ResourceLocation CUSTOM_SKIN = new ResourceLocation("wakfu", "textures/entity/Saylie_Saylie.png");
    private static ResourceLocation originalSkin = null;

    static {
        try {
            // RenderPlayer hérite de RenderLivingBase, c'est là que se trouve mainModel
            mainModelField = RenderPlayer.class.getSuperclass().getDeclaredField("mainModel");
            mainModelField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ModelSwitcher());
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        if (event.getMessage().equalsIgnoreCase("model")) {
            useCustomModel = !useCustomModel; // Toggle ON/OFF
            updatePlayerSkin(useCustomModel);
            System.out.println("[DEBUG] model toggled: " + useCustomModel);
            event.setCanceled(true); // cache le message dans le chat
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (useCustomModel && event.getRenderer() instanceof RenderPlayer) {
            RenderPlayer renderPlayer = (RenderPlayer) event.getRenderer();
            try {
                // Sauvegarde l'ancien modèle
                originalModel = (ModelBase) mainModelField.get(renderPlayer);
                // Remplace par le modèle custom
                mainModelField.set(renderPlayer, new PlayerModel(15, false));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (useCustomModel && event.getRenderer() instanceof RenderPlayer && originalModel != null) {
            RenderPlayer renderPlayer = (RenderPlayer) event.getRenderer();
            try {
                // Restaure l'ancien modèle
                mainModelField.set(renderPlayer, originalModel);
                originalModel = null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.RenderTickEvent event) {
        if (!useCustomModel) return;

        AbstractClientPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            PlayerAnimation.applyJumpRotation(player, event.renderTickTime);
        }
    }

    private void updatePlayerSkin(boolean useCustom) {
        AbstractClientPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            if (useCustom) {
                // Sauvegarde le skin actuel une seule fois
                if (originalSkin == null) {
                    originalSkin = player.getLocationSkin();
                }
                player.getLocationSkin();
            } else if (originalSkin != null) {
                player.getLocationSkin();
            }
        }
    }
}
