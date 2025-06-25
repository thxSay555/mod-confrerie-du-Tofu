package test;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.FloatBuffer;

/**
 * ModelRenderer étendu pour appliquer une transformation matricielle custom
 * sous Forge 1.12.2 (+ Geckolib 3.0.31 si besoin d'ajouter après les AnimationControllers).
 */
public class ModelRendererMatrix extends ModelRenderer {
    private final Matrix4f worldXform = new Matrix4f();
    private final Matrix3f worldNormal = new Matrix3f();
    private boolean useMatrixMode = true;

    /**
     * @param model     Le ModelBase parent (ton modèle Geckolib).
     * @param texWidth  Largeur de la texture.
     * @param texHeight Hauteur de la texture.
     * @param texOffX   Offset X sur l'atlas.
     * @param texOffY   Offset Y sur l'atlas.
     */
    public ModelRendererMatrix(ModelBase model, int texWidth, int texHeight, int texOffX, int texOffY) {
        super(model, texOffX, texOffY);
        // on applique la taille de texture du ModelBase
        this.setTextureSize(texWidth, texHeight);

        // matrices identités
        Matrix4f.setIdentity(worldXform);
        Matrix3f.setIdentity(worldNormal);
    }

    @Override
    public void render(float scale) {
        if (useMatrixMode) {
            GlStateManager.pushMatrix();
            applyWorldTransform();
            super.render(scale);
            GlStateManager.popMatrix();
            // on revient au mode vanilla après un render
            useMatrixMode = false;
        } else {
            super.render(scale);
        }
    }

    private void applyWorldTransform() {
        // on stocke worldXform dans un FloatBuffer puis on l'applique
        FloatBuffer buf = GLAllocation.createDirectFloatBuffer(16);
        worldXform.store(buf);
        buf.flip();
        GlStateManager.multMatrix(buf);
        // les normales seront recalculées automatiquement
    }

    @Override
    public void setRotationPoint(float x, float y, float z) {
        super.setRotationPoint(x, y, z);
        useMatrixMode = true;
    }

    // Si tu relies sur ModelBase#setRotationAngles pour animer,
    // tu n'as pas besoin d'override ici ; Geckolib injectera
    // ses AnimationControllers après ton ModelRendererMatrix.

    // --- Accesseurs pour piloter la matrice depuis l'extérieur ---

    public Matrix4f getWorldXform() {
        return worldXform;
    }

    public void setWorldXform(Matrix4f worldXform) {
        this.worldXform.load(worldXform);
        this.useMatrixMode = true;
    }

    public Matrix3f getWorldNormal() {
        return worldNormal;
    }

    public void setWorldNormal(Matrix3f worldNormal) {
        this.worldNormal.load(worldNormal);
        this.useMatrixMode = true;
    }

    /** Permet de forcer ou non le mode matrice au prochain render. */
    public void setUseMatrixMode(boolean useMatrixMode) {
        this.useMatrixMode = useMatrixMode;
    }

    public boolean isUseMatrixMode() {
        return useMatrixMode;
    }
}