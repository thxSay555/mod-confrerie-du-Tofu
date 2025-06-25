package test;

import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib3.geo.render.built.GeoBone;

public class MowzieGeoBone extends GeoBone {

    public MowzieGeoBone() {
        super();
    }

    /**
     * Setter pour le parent afin de bypasser l’accès direct au champ protégé.
     */
    public void setParent(GeoBone parent) {
        this.parent = parent;
    }

    public MowzieGeoBone getParent() {
        return (MowzieGeoBone) parent;
    }

    // --- Position utils ---

    public void addPosition(Vec3d vec) {
        addPosition((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void addPosition(float x, float y, float z) {
        setPositionX(getPositionX() + x);
        setPositionY(getPositionY() + y);
        setPositionZ(getPositionZ() + z);
    }

    public void setPosition(Vec3d vec) {
        setPosition((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void setPosition(float x, float y, float z) {
        setPositionX(x);
        setPositionY(y);
        setPositionZ(z);
    }

    public Vec3d getPosition() {
        return new Vec3d(getPositionX(), getPositionY(), getPositionZ());
    }

    // --- Rotation utils ---

    public void addRotation(Vec3d vec) {
        addRotation((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void addRotation(float x, float y, float z) {
        setRotationX(getRotationX() + x);
        setRotationY(getRotationY() + y);
        setRotationZ(getRotationZ() + z);
    }

    public void setRotation(Vec3d vec) {
        setRotation((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void setRotation(float x, float y, float z) {
        setRotationX(x);
        setRotationY(y);
        setRotationZ(z);
    }

    public Vec3d getRotation() {
        return new Vec3d(getRotationX(), getRotationY(), getRotationZ());
    }

    // --- Scale utils ---

    public void multiplyScale(Vec3d vec) {
        multiplyScale((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void multiplyScale(float x, float y, float z) {
        setScaleX(getScaleX() * x);
        setScaleY(getScaleY() * y);
        setScaleZ(getScaleZ() * z);
    }

    public void setScale(Vec3d vec) {
        setScale((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void setScale(float x, float y, float z) {
        setScaleX(x);
        setScaleY(y);
        setScaleZ(z);
    }

    public Vec3d getScale() {
        return new Vec3d(getScaleX(), getScaleY(), getScaleZ());
    }

    /**
     * Ajoute à la rotation courante la différence entre la
     * rotation présente et l’état initial de la source.
     */
    public void addRotationOffsetFromBone(MowzieGeoBone source) {
        float dx = source.getRotationX() - source.getInitialSnapshot().rotationValueX;
        float dy = source.getRotationY() - source.getInitialSnapshot().rotationValueY;
        float dz = source.getRotationZ() - source.getInitialSnapshot().rotationValueZ;
        setRotationX(getRotationX() + dx);
        setRotationY(getRotationY() + dy);
        setRotationZ(getRotationZ() + dz);
    }
}
