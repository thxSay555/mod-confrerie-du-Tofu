package test;

public class AnimationInstance {
    private final Animation animation;
    private float time = 0f;
    private boolean finished = false;

    public AnimationInstance(Animation animation) {
        this.animation = animation;
    }

    public void tick() {
        if (finished) return;
        time += 0.05f;
        if (time >= animation.getLength()) {
            if (animation.isLoop()) {
                time %= animation.getLength();
            } else {
                time = animation.getLength();
                finished = true;
            }
        }
    }

    public float getTime() { return time; }
    public Animation getAnimation() { return animation; }
    public boolean isFinished() { return finished; }
}