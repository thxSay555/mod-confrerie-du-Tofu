package test;

import test.IAnimationTickable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;

import javax.annotation.Nullable;
import java.util.Collections;

public abstract class MowzieAnimatedGeoModel<T extends IAnimatable & IAnimationTickable> 
        extends AnimatedGeoModel<T> {

    private double lastGameTickTime = 0;
    private double seekTime = 0;

    @Override
    public abstract @Nullable
    net.minecraft.util.ResourceLocation getModelLocation(T object);

    @Override
    public abstract @Nullable
    net.minecraft.util.ResourceLocation getTextureLocation(T object);

    @Override
    public abstract @Nullable
    net.minecraft.util.ResourceLocation getAnimationFileLocation(T object);

    public MowzieGeoBone getMowzieBone(String boneName) {
        IBone bone = this.getAnimationProcessor().getBone(boneName);
        return (MowzieGeoBone) bone;
    }

    public boolean isInitialized() {
        return !this.getAnimationProcessor().getModelRendererList().isEmpty();
    }

    public void setCustomAnimations(T animatable, int instanceId, @Nullable AnimationEvent animationEvent) {
        Minecraft mc = Minecraft.getMinecraft(); // 1.12.2 call
        AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
        double currentTick = animatable.tickTimer();

        // Initialisation du tick de départ
        if (manager.startTick < 0) {
            manager.startTick = currentTick + mc.getRenderPartialTicks();
        }

        // Avance le tick si non en pause ou si autorisé
        if (!mc.isGamePaused() || manager.shouldPlayWhilePaused) {
            manager.tick = currentTick + mc.getRenderPartialTicks();
            double gameTick = manager.tick;
            double delta = gameTick - this.lastGameTickTime;
            this.seekTime += delta;
            this.lastGameTickTime = gameTick;
        }

        // Prépare l'événement à passer au processor
        AnimationEvent<T> predicate = (animationEvent == null)
            ? new AnimationEvent<>(animatable, 0, 0, (float)(manager.tick - this.lastGameTickTime), false, Collections.emptyList())
            : animationEvent;
        predicate.animationTick = this.seekTime;

        // Setup avant animation
        getAnimationProcessor().preAnimationSetup(animatable, this.seekTime);

        // Lance la boucle d'animation
        if (isInitialized()) {
            getAnimationProcessor()
                .tickAnimation(animatable, instanceId, this.seekTime, predicate, GeckoLibCache.getInstance().parser,
                               this.shouldCrashOnMissing);
        }

        // Applique ton code custom si on n'est pas en pause
        if (!mc.isGamePaused() || manager.shouldPlayWhilePaused) {
            codeAnimations(animatable, instanceId, animationEvent);
        }
    }

    /**
     * À implémenter dans tes sous-classes pour ajouter des
     * animations spécifiques après le processing Geckolib.
     */
    public void codeAnimations(T animatable, int instanceId, AnimationEvent<?> customPredicate) {
    }

    /**
     * Exemple : override si tu veux filtrer certains skins de joueur.
     */
    public boolean resourceForModelId(AbstractClientPlayer player) {
        return true;
    }

    /**
     * Exemple d’accès à une valeur custom (ici position X du bone).
     */
    public float getControllerValue(String controllerName) {
        if (!isInitialized()) return 1.0f;
        IBone bone = getAnimationProcessor().getBone(controllerName);
        return 1.0f - bone.getPositionX();
    }
}