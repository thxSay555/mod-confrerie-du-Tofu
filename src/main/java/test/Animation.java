package test;

import java.util.Map;

/**
 * Wrapper pour une animation GeckoLib chargée par AnimationLoader.
 */
public class Animation {
    private final GeckoLibAnimation source;
    public Animation(GeckoLibAnimation src) { this.source = src; }

    public String getName()   { return source.getName(); }
    public float  getLength() { return source.getLength(); }
    public boolean isLoop()   { return source.isLoop(); }
    public Map<String, BoneAnimation> getBones() {
        return source.getBones();
    }
}
