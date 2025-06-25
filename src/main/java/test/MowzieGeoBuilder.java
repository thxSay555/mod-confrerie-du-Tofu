package test;

import org.apache.commons.lang3.ArrayUtils;
import javax.vecmath.Vector3f;

import software.bernie.geckolib3.geo.raw.pojo.Bone;
import software.bernie.geckolib3.geo.raw.pojo.Cube;
import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;
import software.bernie.geckolib3.geo.raw.tree.RawBoneGroup;
import software.bernie.geckolib3.geo.render.GeoBuilder;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.util.VectorUtils;

/**
 * GeoBuilder adapté pour Forge 1.12.2 + GeckoLib 3.0.31,
 * avec inflate en Double (null possible).
 */
public class MowzieGeoBuilder extends GeoBuilder {

    @Override
    public GeoBone constructBone(RawBoneGroup rawGroup, ModelProperties props, GeoBone parent) {
        MowzieGeoBone geoBone = new MowzieGeoBone();
        if (parent != null) geoBone.setParent(parent);

        Bone rawBone = rawGroup.selfBone;
        Vector3f rotation = VectorUtils.convertDoubleToFloat(
            VectorUtils.fromArray(rawBone.getRotation())
        );
        rotation.x *= -1; rotation.y *= -1;

        Vector3f pivot = VectorUtils.convertDoubleToFloat(
            VectorUtils.fromArray(rawBone.getPivot())
        );

        geoBone.mirror     = rawBone.getMirror();
        geoBone.dontRender = rawBone.getNeverRender();
        geoBone.reset      = rawBone.getReset();
        // inflate est un Double : on divise ici et on laisse null si absent
        geoBone.inflate    = rawBone.getInflate() != null
            ? rawBone.getInflate() / 16.0
            : null;

        geoBone.setModelRendererName(rawBone.getName());

        geoBone.setRotationX((float) Math.toRadians(rotation.x));
        geoBone.setRotationY((float) Math.toRadians(rotation.y));
        geoBone.setRotationZ((float) Math.toRadians(rotation.z));

        geoBone.rotationPointX = -pivot.x;
        geoBone.rotationPointY =  pivot.y;
        geoBone.rotationPointZ =  pivot.z;

        if (!ArrayUtils.isEmpty(rawBone.getCubes())) {
            for (Cube cubePojo : rawBone.getCubes()) {
                GeoCube cube = GeoCube.createFromPojoCube(
                    cubePojo,
                    props,
                    geoBone.inflate,
                    geoBone.mirror
                );
                geoBone.childCubes.add(cube);
            }
        }

        for (RawBoneGroup childGroup : rawGroup.children.values()) {
            geoBone.childBones.add(
                constructBone(childGroup, props, geoBone)
            );
        }

        return geoBone;
    }
}
