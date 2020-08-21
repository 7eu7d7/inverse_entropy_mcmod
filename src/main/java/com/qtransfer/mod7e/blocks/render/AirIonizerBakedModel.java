package com.qtransfer.mod7e.blocks.render;

import com.google.common.primitives.Ints;
import com.qtransfer.mod7e.Constant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class AirIonizerBakedModel extends BaseBakedModel{
    public static final ModelResourceLocation variantTag
            = new ModelResourceLocation(Constant.item("air_ionizer_model"), "normal");
    TextureAtlasSprite texture;

    public AirIonizerBakedModel(IBakedModel parent,TextureAtlasSprite texture) {
        super(parent);
        this.texture=texture;
        init_model(texture);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return super.getQuads(state, side, rand);
    }

    private void init_model(TextureAtlasSprite texture){
        float x1, x2, x3;
        float y1, y2, y3;
        float z1, z2, z3;
        int packednormal;

        x1=0.5f; y1=1f; z1=0.5f;
        x2=0f; y2=0f; z2=0f;
        x3=0f; y3=0f; z3=1f;

        packednormal=calculatePackedNormal(x1,y1,z1,x2,y2,z2,x3,y3,z3);
        addQuad(new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), texture, 8, 0,packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), texture, 0, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, 16, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, 16, 16,packednormal)),
                0, EnumFacing.SOUTH, texture, true, DefaultVertexFormats.ITEM));

        x1=0.5f; y1=1f; z1=0.5f;
        x2=0f; y2=0f; z2=1f;
        x3=1f; y3=0f; z3=1f;

        packednormal=calculatePackedNormal(x1,y1,z1,x2,y2,z2,x3,y3,z3);
        addQuad(new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), texture, 8, 0,packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), texture, 0, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, 16, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, 16, 16,packednormal)),
                0, EnumFacing.EAST, texture, true, DefaultVertexFormats.ITEM));

        x1=0.5f; y1=1f; z1=0.5f;
        x2=1f; y2=0f; z2=1f;
        x3=1f; y3=0f; z3=0f;

        packednormal=calculatePackedNormal(x1,y1,z1,x2,y2,z2,x3,y3,z3);
        addQuad(new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), texture, 8, 0,packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), texture, 0, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, 16, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, 16, 16,packednormal)),
                0, EnumFacing.NORTH, texture, true, DefaultVertexFormats.ITEM));

        x1=0.5f; y1=1f; z1=0.5f;
        x2=1f; y2=0f; z2=0f;
        x3=0f; y3=0f; z3=0f;

        packednormal=calculatePackedNormal(x1,y1,z1,x2,y2,z2,x3,y3,z3);
        addQuad(new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), texture, 8, 0,packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), texture, 0, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, 16, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, 16, 16,packednormal)),
                0, EnumFacing.WEST, texture, true, DefaultVertexFormats.ITEM));
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return texture;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(Collections.EMPTY_LIST){
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return new AirIonizerBakedModel(originalModel,texture);
            }
        };
    }
}
