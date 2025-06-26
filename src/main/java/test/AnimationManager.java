package test;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Manages player animations
 */
public class AnimationManager {
    private final Map<String, AnimationInstance> players = Maps.newHashMap();

    public void startAnimation(String playerName, String animationName) {
        Animation anim = AnimationLoader.getAnimation(animationName);
        if (anim != null) {
            players.put(playerName, new AnimationInstance(anim));
        }
    }

    public AnimationInstance getInstance(String playerName) {
        return players.get(playerName);
    }

    public void tickAll() {
        for (AnimationInstance inst : players.values()) {
            inst.tick();
        }
    }

    public void stopAnimation(String playerName) {
        players.remove(playerName);
    }

	
}