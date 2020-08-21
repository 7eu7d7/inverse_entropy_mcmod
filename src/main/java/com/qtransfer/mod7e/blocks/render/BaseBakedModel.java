/*
 * Copyright (c) 2015, 2016, 2017, 2018, 2019 Adrian Siekierka
 *
 * This file is part of Charset.
 *
 * Charset is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Charset is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Charset.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.qtransfer.mod7e.blocks.render;

import com.google.common.primitives.Ints;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public abstract class BaseBakedModel implements IBakedModel {
    List<BakedQuad> quad_list=new ArrayList<BakedQuad>();
    List<IBakedModel> sub_models=new LinkedList<IBakedModel>();

    public BaseBakedModel(IBakedModel parent){
        parentModel=parent;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> combinedQuadsList = new ArrayList<BakedQuad>();
        for(IBakedModel model:sub_models){
            combinedQuadsList.addAll(model.getQuads(state,side,rand));
        }
        combinedQuadsList.addAll(quad_list);
        return combinedQuadsList;
//    FaceBakery.makeBakedQuad() can also be useful for generating quads
    }

    @Override
    public boolean isAmbientOcclusion() {
        return parentModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return parentModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return parentModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return parentModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        Vector3f rotation = new Vector3f();
        Vector3f translation = new Vector3f();
        Vector3f scale = new Vector3f(1f, 1f, 1f);

        // Third Person
        rotation = new Vector3f(0f, (float) -Math.PI / 2f, (float) Math.PI * 7f / 36f); // (0, 90, -35)
        translation = new Vector3f(0f, 5.5f, 2.5f);
        translation.scale(0.0625f);
        ItemTransformVec3f hirdPersonRight = new ItemTransformVec3f(rotation, translation, scale);
        return new ItemCameraTransforms(ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT,
                ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, hirdPersonRight,
                ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
//    if (parentModel instanceof IPerspectiveAwareModel) {
        Matrix4f matrix4f = parentModel.handlePerspective(cameraTransformType).getRight();
        switch (cameraTransformType) {
            case GUI: {
                //ItemCameraTransforms itemCameraTransforms = parentModel.getItemCameraTransforms();
                ItemTransformVec3f itemTransformVec3f = new ItemTransformVec3f(new Vector3f(30, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.6f, 0.6f, 0.6f));
                TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);
                matrix4f = tr.getMatrix();
                /*matrix4f=new Matrix4f();
                matrix4f.rotY(45);

                Matrix4f mat1=new Matrix4f();
                mat1.rotX(20);
                //matrix4f.mul(mat1);
                mat1.mul(matrix4f);
                matrix4f=mat1;*/

            }break;
            case GROUND: {
                ItemTransformVec3f itemTransformVec3f = new ItemTransformVec3f(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.6f, 0.6f, 0.6f));
                TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);
                matrix4f = tr.getMatrix();
            }break;
            default:
                break;
        }
        return Pair.of(this, matrix4f);
//    } else {
//      // If the parent model isn't an IPerspectiveAware, we'll need to generate the correct matrix ourselves using the
//      //  ItemCameraTransforms.
//
//      ItemCameraTransforms itemCameraTransforms = parentModel.getItemCameraTransforms();
//      ItemTransformVec3f itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType);
//      TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);
//      Matrix4f mat = null;
//      if (tr != null) { // && tr != TRSRTransformation.identity()) {
//        mat = tr.getMatrix();
//      }
//      // The TRSRTransformation for vanilla items have blockCenterToCorner() applied, however handlePerspective
//      //  reverses it back again with blockCornerToCenter().  So we don't need to apply it here.
//
//      return Pair.of(this, mat);
//    }
    }

    @Override
    public ItemOverrideList getOverrides() {
        return null;
    }

    public void addQuad(BakedQuad quad){
        quad_list.add(quad);
    }

    /**
     // Creates a baked quad for the given face.
     // When you are directly looking at the face, the quad is centred at [centreLR, centreUD]
     // The left<->right "width" of the face is width, the bottom<-->top "height" is height.
     // The amount that the quad is displaced towards the viewer i.e. (perpendicular to the flat face you can see) is forwardDisplacement
     //   - for example, for an EAST face, a value of 0.00 lies directly on the EAST face of the cube.  a value of 0.01 lies
     //     slightly to the east of the EAST face (at x=1.01).  a value of -0.01 lies slightly to the west of the EAST face (at x=0.99).
     // The orientation of the faces is as per the diagram on this page
     //   http://greyminecraftcoder.blogspot.com.au/2014/12/block-models-texturing-quads-faces.html
     // Read this page to learn more about how to draw a textured quad
     //   http://greyminecraftcoder.blogspot.co.at/2014/12/the-tessellator-and-worldrenderer-18.html
     * @param centreLR the centre point of the face left-right
     * @param width    width of the face
     * @param centreUD centre point of the face top-bottom
     * @param height height of the face from top to bottom
     * @param forwardDisplacement the displacement of the face (towards the front)
     * @param itemRenderLayer which item layer the quad is on
     * @param texture the texture to use for the quad
     * @param face the face to draw this quad on
     * @return
     */
    public BakedQuad createBakedQuadForFace(float centreLR, float width, float centreUD, float height, float forwardDisplacement,
                                             int itemRenderLayer,
                                             TextureAtlasSprite texture, EnumFacing face)
    {
        float x1, x2, x3, x4;
        float y1, y2, y3, y4;
        float z1, z2, z3, z4;
        int packednormal;
        final float CUBE_MIN = 0.0F;
        final float CUBE_MAX = 1.0F;

        switch (face) {
            case UP: {
                x1 = x2 = centreLR + width/2.0F;
                x3 = x4 = centreLR - width/2.0F;
                z1 = z4 = centreUD + height/2.0F;
                z2 = z3 = centreUD - height/2.0F;
                y1 = y2 = y3 = y4 = CUBE_MAX + forwardDisplacement;
                break;
            }
            case DOWN: {
                x1 = x2 = centreLR + width/2.0F;
                x3 = x4 = centreLR - width/2.0F;
                z1 = z4 = centreUD - height/2.0F;
                z2 = z3 = centreUD + height/2.0F;
                y1 = y2 = y3 = y4 = CUBE_MIN - forwardDisplacement;
                break;
            }
            case WEST: {
                z1 = z2 = centreLR + width/2.0F;
                z3 = z4 = centreLR - width/2.0F;
                y1 = y4 = centreUD - height/2.0F;
                y2 = y3 = centreUD + height/2.0F;
                x1 = x2 = x3 = x4 = CUBE_MIN - forwardDisplacement;
                break;
            }
            case EAST: {
                z1 = z2 = centreLR - width/2.0F;
                z3 = z4 = centreLR + width/2.0F;
                y1 = y4 = centreUD - height/2.0F;
                y2 = y3 = centreUD + height/2.0F;
                x1 = x2 = x3 = x4 = CUBE_MAX + forwardDisplacement;
                break;
            }
            case NORTH: {
                x1 = x2 = centreLR - width/2.0F;
                x3 = x4 = centreLR + width/2.0F;
                y1 = y4 = centreUD - height/2.0F;
                y2 = y3 = centreUD + height/2.0F;
                z1 = z2 = z3 = z4 = CUBE_MIN - forwardDisplacement;
                break;
            }
            case SOUTH: {
                x1 = x2 = centreLR + width/2.0F;
                x3 = x4 = centreLR - width/2.0F;
                y1 = y4 = centreUD - height/2.0F;
                y2 = y3 = centreUD + height/2.0F;
                z1 = z2 = z3 = z4 = CUBE_MAX + forwardDisplacement;
                break;
            }
            default: {
                assert false : "Unexpected facing in createBakedQuadForFace:" + face;
                return null;
            }
        }

        packednormal = calculatePackedNormal(x1, y1, z1,  x2, y2, z2,  x3, y3, z3,  x4, y4, z4);
        return new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), texture, 16, 16, packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), texture, 16, 0, packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, 0, 0, packednormal),
                vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), texture, 0, 16, packednormal)),
                itemRenderLayer, face, texture, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM);
    }

    /**
     * Converts the vertex information to the int array format expected by BakedQuads.  Useful if you don't know
     *   in advance what it should be.
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param color RGBA colour format - white for no effect, non-white to tint the face with the specified colour
     * @param texture the texture to use for the face
     * @param u u-coordinate of the texture (0 - 16) corresponding to [x,y,z]
     * @param v v-coordinate of the texture (0 - 16) corresponding to [x,y,z]
     * @param normal the packed representation of the normal vector, see calculatePackedNormal().  Used for lighting items.
     * @return
     */
    public int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v, int normal)
    {
        return new int[] {
                Float.floatToRawIntBits(x),
                Float.floatToRawIntBits(y),
                Float.floatToRawIntBits(z),
                color,
                Float.floatToRawIntBits(texture.getInterpolatedU(u)),
                Float.floatToRawIntBits(texture.getInterpolatedV(v)),
                normal
        };
    }

    public int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v)
    {
        return new int[] {
                Float.floatToRawIntBits(x),
                Float.floatToRawIntBits(y),
                Float.floatToRawIntBits(z),
                color,
                Float.floatToRawIntBits(texture.getInterpolatedU(u)),
                Float.floatToRawIntBits(texture.getInterpolatedV(v)),
                0xFF00
        };
    }

    /**
     * Calculate the normal vector based on four input coordinates
     * assumes that the quad is coplanar but should produce a 'reasonable' answer even if not.
     * @return the packed normal, ZZYYXX
     */
    public int calculatePackedNormal(
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4) {

        float xp = x4-x2;
        float yp = y4-y2;
        float zp = z4-z2;

        float xq = x3-x1;
        float yq = y3-y1;
        float zq = z3-z1;

        //Cross Product
        float xn = yq*zp - zq*yp;
        float yn = zq*xp - xq*zp;
        float zn = xq*yp - yq*xp;

        //Normalize
        float norm = (float)Math.sqrt(xn*xn + yn*yn + zn*zn);
        final float SMALL_LENGTH =  1.0E-4F;  //Vec3d.normalise() uses this
        if (norm < SMALL_LENGTH) norm = 1.0F;  // protect against degenerate quad

        norm = 1.0F / norm;
        xn *= norm;
        yn *= norm;
        zn *= norm;

        int x = ((byte)(xn * 127)) & 0xFF;
        int y = ((byte)(yn * 127)) & 0xFF;
        int z = ((byte)(zn * 127)) & 0xFF;
        return x | (y << 0x08) | (z << 0x10);
    }

    public int calculatePackedNormal(
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3) {

        float xp = x3-x2;
        float yp = y3-y2;
        float zp = z3-z2;

        float xq = x3-x1;
        float yq = y3-y1;
        float zq = z3-z1;

        //Cross Product
        float xn = yq*zp - zq*yp;
        float yn = zq*xp - xq*zp;
        float zn = xq*yp - yq*xp;

        //Normalize
        float norm = (float)Math.sqrt(xn*xn + yn*yn + zn*zn);
        final float SMALL_LENGTH =  1.0E-4F;  //Vec3d.normalise() uses this
        if (norm < SMALL_LENGTH) norm = 1.0F;  // protect against degenerate quad

        norm = 1.0F / norm;
        xn *= norm;
        yn *= norm;
        zn *= norm;

        int x = ((byte)(xn * 127)) & 0xFF;
        int y = ((byte)(yn * 127)) & 0xFF;
        int z = ((byte)(zn * 127)) & 0xFF;
        return x | (y << 0x08) | (z << 0x10);
    }

    private IBakedModel parentModel;
}
