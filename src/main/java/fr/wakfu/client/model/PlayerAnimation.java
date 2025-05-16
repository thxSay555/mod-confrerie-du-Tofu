package fr.wakfu.client.model;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.math.MathHelper;

public class PlayerAnimation {

    public static void applyJumpRotation(AbstractClientPlayer player, float partialTicks) {
        if (player.isAirBorne && !player.onGround) {
            float ticks = player.ticksExisted + partialTicks;
            float rotation = ticks * 20 % 360;
            player.renderYawOffset = rotation;
            player.rotationYawHead = rotation;
        }
    }
}
