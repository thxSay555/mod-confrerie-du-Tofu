package test;

/** Garde l’état d’une animation en cours (temps courant). */
public class AnimationInstance {
    private final Animation animation;
    private float time = 0f;

    public AnimationInstance(Animation anim) {
        this.animation = anim;
    }

    public Animation getAnimation() { return animation; }
    public float getTime()          { return time; }
    public void tick() {
        // avance d’1/20e de s
        time += 1f / 20f;
        if (animation.isLoop() && time > animation.getLength()) {
            time %= animation.getLength();
        }
    }
}
