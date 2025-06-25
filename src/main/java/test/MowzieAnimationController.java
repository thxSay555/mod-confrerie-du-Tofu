package test;

import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;

/**
 * Contrôleur d'animation étendu pour gérer un décalage de tick personnalisé.
 */
public class MowzieAnimationController<T extends IAnimatable & IAnimationTickable>
        extends AnimationController<T> {

    private double tickOffset = 0.0d;

    /**
     * @param animatable            L'entité animable et tickable.
     * @param name                  Nom du contrôleur.
     * @param transitionLengthTicks Durée de transition (en ticks).
     * @param animationPredicate    Prédicat définissant l'animation à jouer.
     */
    public MowzieAnimationController(T animatable, String name, float transitionLengthTicks,
                                     IAnimationPredicate<T> animationPredicate) {
        super(animatable, name, transitionLengthTicks, animationPredicate);
    }

    /**
     * Lance immédiatement une AnimationBuilder, réinitialise le queue et
     * applique le tickOffset pour synchroniser correctement la transition.
     */
    public void playAnimation(T animatable, AnimationBuilder animationBuilder) {
        // Force la relecture du contrôleur
        markNeedsReload();
        // Enfile la nouvelle animation
        setAnimation(animationBuilder);
        // Récupère directement la head de la queue
        this.currentAnimation = this.animationQueue.poll();
        this.isJustStarting = true;
        // Ajuste l'offset de tick pour éviter les sauts
        adjustTick(animatable.tickTimer());
        // On passe en transition instantanée
        this.transitionLengthTicks = 0;
    }

    /**
     * Ajuste le tick interne en soustrayant tickOffset.
     * Lors d'un reset, on recalcule tickOffset en fonction de l'état.
     */
    @Override
    protected double adjustTick(double tick) {
        if (this.shouldResetTick) {
            if (getAnimationState() == AnimationState.Transitioning) {
                // Pendant la transition, mémorise la base du tick
                this.tickOffset = tick;
            } else if (getAnimationState() == AnimationState.Running) {
                // Une fois lancée, ajoute la durée de transition
                this.tickOffset += this.transitionLengthTicks;
            }
            this.shouldResetTick = false;
        }
        // Ne jamais revenir sous zéro
        return Math.max(tick - this.tickOffset, 0.0D);
    }
}