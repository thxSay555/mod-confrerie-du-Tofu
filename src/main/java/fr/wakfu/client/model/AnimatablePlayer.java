package fr.wakfu.client.model;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class AnimatablePlayer implements IAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0,
            event -> {
                if (event.getExtraData().get(0)) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("attack", false));
                    return PlayState.CONTINUE;
                } else {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", true));
                    return PlayState.CONTINUE;
                }
            })
        );
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
	@Override
	public AnimationFactory getFactory() {
		// TODO Auto-generated method stub
		return null;
	}
}
