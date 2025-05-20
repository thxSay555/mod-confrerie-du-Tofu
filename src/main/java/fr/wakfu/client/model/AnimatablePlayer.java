package fr.wakfu.client.model;

import net.minecraft.client.entity.AbstractClientPlayer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class AnimatablePlayer extends AbstractClientPlayer implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    public AnimatablePlayer() {
        super(null, null); // à adapter selon votre constructeur de base
    }



    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(
            new AnimationController<>(this, "controller", 0, this::predicate)
        );
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        // On récupère le premier extraData et on vérifie qu'il s'agit d'un Boolean à true
        if (!event.getExtraData().isEmpty() 
            && Boolean.TRUE.equals(event.getExtraData().get(0))) {
            event.getController().setAnimation(
                new AnimationBuilder().addAnimation("attack", false)
            );
        } else {
            event.getController().setAnimation(
                new AnimationBuilder().addAnimation("idle", true)
            );
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
