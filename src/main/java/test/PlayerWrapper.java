package test;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class PlayerWrapper extends EntityLiving implements IAnimatable {
    public final EntityPlayer originalPlayer;
    private final AnimationFactory factory = new AnimationFactory(this);

    public PlayerWrapper(EntityPlayer player) {
        super(player.world);
        this.originalPlayer = player;
        this.setPosition(player.posX, player.posY, player.posZ);
        this.rotationYaw = player.rotationYaw;
        this.rotationPitch = player.rotationPitch;
        this.prevRotationYaw = player.prevRotationYaw;
        this.prevRotationPitch = player.prevRotationPitch;
        this.limbSwing = player.limbSwing;
        this.limbSwingAmount = player.limbSwingAmount;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "base_controller", 5, this::animate));
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
	private PlayState animate(AnimationEvent<PlayerWrapper> event) {
        AnimationController<PlayerWrapper> controller = event.getController();
        
        if (originalPlayer.isSprinting()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("run", true));
        } else if (originalPlayer.isSneaking()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("sneak", true));
        } else if (originalPlayer.moveForward > 0) {
            controller.setAnimation(new AnimationBuilder().addAnimation("walk", true));
        } else {
            controller.setAnimation(new AnimationBuilder().addAnimation("idle", true));
        }
        
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}