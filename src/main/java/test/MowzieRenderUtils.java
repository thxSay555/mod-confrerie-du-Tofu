package test;

import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Stack;

public class MowzieRenderUtils {
    /**
     * Notre propre MatrixStack (pas celui de GeckoLib ni Mojang),
     * 100% compatible 1.12.2.
     */
    public static class MyMatrixStack {
        private final Stack<Matrix4f> stack = new Stack<>();

        public MyMatrixStack() {
            Matrix4f id = new Matrix4f();
            id.setIdentity();
            stack.push(id);
        }

        public void push() {
            stack.push(new Matrix4f(stack.peek()));
        }

        public void pop() {
            stack.pop();
        }

        public Matrix4f getMatrix() {
            return stack.peek();
        }

        public void translate(double x, double y, double z) {
            Matrix4f m = new Matrix4f();
            m.setIdentity();
            m.m03 = (float) x;
            m.m13 = (float) y;
            m.m23 = (float) z;
            stack.peek().mul(m);
        }

        public void scale(double x, double y, double z) {
            Matrix4f m = new Matrix4f();
            m.setIdentity();
            m.m00 = (float) x;
            m.m11 = (float) y;
            m.m22 = (float) z;
            stack.peek().mul(m);
        }

        public void rotateX(float rad) {
            Matrix4f m = new Matrix4f();
            m.setIdentity();
            float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
            m.m11 = c; m.m12 = -s;
            m.m21 = s; m.m22 = c;
            stack.peek().mul(m);
        }

        public void rotateY(float rad) {
            Matrix4f m = new Matrix4f();
            m.setIdentity();
            float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
            m.m00 = c; m.m02 = s;
            m.m20 = -s; m.m22 = c;
            stack.peek().mul(m);
        }

        public void rotateZ(float rad) {
            Matrix4f m = new Matrix4f();
            m.setIdentity();
            float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
            m.m00 = c; m.m01 = -s;
            m.m10 = s; m.m11 = c;
            stack.peek().mul(m);
        }
    }

    // ---------------- AdvancedModelRenderer utils ----------------

    /** Récupère la position world d’un AdvancedModelRenderer “local (0,0,0)”. */
    public static Vec3d getWorldPosFromModel(Entity entity, float yaw, AdvancedModelRenderer model) {
        MyMatrixStack ms = new MyMatrixStack();
        ms.push();
        // translate entity
        ms.translate(entity.posX, entity.posY, entity.posZ);
        ms.rotateY((float) Math.toRadians(-yaw + 180));
        ms.scale(-1, -1, 1);
        ms.translate(0, -1.5, 0);

        // applique récursivement notre transform “ModelRenderer” maison
        matrixStackFromModel(ms, model);

        // récupère la matrice et transforme le point (0,0,0,1)
        Matrix4f mat = ms.getMatrix();
        Vector4f v = new Vector4f(0, 0, 0, 1);
        mat.transform(v);
        ms.pop();
        return new Vec3d(v.x, v.y, v.z);
    }

    /** Remonte la hiérarchie des AdvancedModelRenderer et applique translate+rotate maison. */
    public static void matrixStackFromModel(MyMatrixStack ms, AdvancedModelRenderer model) {
        AdvancedModelRenderer parent = model.getParent();
        if (parent != null) matrixStackFromModel(ms, parent);
        applyModelTransform(ms, model);
    }

    /** Simule ModelRenderer.translateRotate pour AdvancedModelRenderer. */
    private static void applyModelTransform(MyMatrixStack ms, AdvancedModelRenderer m) {
        // translate au pivot (en /16 car BlockBench → OpenGL)
        ms.translate(m.rotationPointX / 16.0, m.rotationPointY / 16.0, m.rotationPointZ / 16.0);
        // rotate Z → Y → X
        if (m.rotateAngleZ != 0) ms.rotateZ(m.rotateAngleZ);
        if (m.rotateAngleY != 0) ms.rotateY(m.rotateAngleY);
        if (m.rotateAngleX != 0) ms.rotateX(m.rotateAngleX);
        // re-translate cubes (offset)
        ms.translate(-m.rotationPointX / 16.0, -m.rotationPointY / 16.0, -m.rotationPointZ / 16.0);
    }

    // ---------------- GeoBone / GeoCube utils ----------------

    public static Vec3d getWorldPosFromModel(Entity entity, float yaw, GeoBone bone) {
        MyMatrixStack ms = new MyMatrixStack();
        ms.push();
        ms.translate(entity.posX, entity.posY, entity.posZ);
        ms.rotateY((float) Math.toRadians(-yaw + 180));
        ms.scale(-1, -1, 1);
        ms.translate(0, -1.5, 0);
        matrixStackFromModel(ms, bone);
        Matrix4f mat = ms.getMatrix();
        Vector4f v = new Vector4f(0, 0, 0, 1);
        mat.transform(v);
        ms.pop();
        return new Vec3d(v.x, v.y, v.z);
    }

    public static void matrixStackFromModel(MyMatrixStack ms, GeoBone bone) {
        if (bone.parent != null) matrixStackFromModel(ms, (GeoBone) bone.parent);
        translateRotateGeckolib(bone, ms);
    }

    public static void translateRotateGeckolib(GeoBone bone, MyMatrixStack ms) {
        ms.translate(bone.rotationPointX / 16.0, bone.rotationPointY / 16.0, bone.rotationPointZ / 16.0);
        if (bone.getRotationZ() != 0) ms.rotateZ(bone.getRotationZ());
        if (bone.getRotationY() != 0) ms.rotateY(bone.getRotationY());
        if (bone.getRotationX() != 0) ms.rotateX(bone.getRotationX());
        ms.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static void moveToPivotMirror(GeoCube cube, MyMatrixStack ms) {
        Vector3f p = cube.pivot;
        ms.translate(-p.x / 16.0, p.y / 16.0, p.z / 16.0);
    }

    public static void moveBackFromPivotMirror(GeoCube cube, MyMatrixStack ms) {
        Vector3f p = cube.pivot;
        ms.translate(p.x / 16.0, -p.y / 16.0, -p.z / 16.0);
    }

    public static void moveToPivotMirror(GeoBone bone, MyMatrixStack ms) {
        ms.translate(-bone.rotationPointX / 16.0, bone.rotationPointY / 16.0, bone.rotationPointZ / 16.0);
    }

    public static void moveBackFromPivotMirror(GeoBone bone, MyMatrixStack ms) {
        ms.translate(bone.rotationPointX / 16.0, -bone.rotationPointY / 16.0, -bone.rotationPointZ / 16.0);
    }

    public static void translateMirror(GeoBone bone, MyMatrixStack ms) {
        ms.translate(bone.getPositionX() / 16.0, bone.getPositionY() / 16.0, bone.getPositionZ() / 16.0);
    }

    public static void rotateMirror(GeoBone bone, MyMatrixStack ms) {
        if (bone.getRotationZ() != 0) ms.rotateZ(-bone.getRotationZ());
        if (bone.getRotationY() != 0) ms.rotateY(-bone.getRotationY());
        if (bone.getRotationX() != 0) ms.rotateX(bone.getRotationX());
    }

    public static void rotateMirror(GeoCube cube, MyMatrixStack ms) {
        Vector3f r = cube.rotation;
        ms.rotateZ(-r.z);
        ms.rotateY(-r.y);
        ms.rotateX(r.x);
    }
}
