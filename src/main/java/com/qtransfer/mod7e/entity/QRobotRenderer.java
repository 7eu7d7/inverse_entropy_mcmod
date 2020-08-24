package com.qtransfer.mod7e.entity;

import com.qtransfer.mod7e.Constant;
import com.qtransfer.mod7e.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class QRobotRenderer extends Render<QRobotEntity> {
    public static final ResourceLocation ROBOT_TEX=new ResourceLocation(Constant.item("textures/entity/qrobot.png"));
    public static final ResourceLocation ROBOT_TEX1=new ResourceLocation(Constant.item("textures/entity/mask02.png"));
    public final ResourceLocation RES_MODEL=new ResourceLocation(Constant.MODID, "models/entity/qrobot.obj");
    //public final ResourceLocation RES_MODEL=new ResourceLocation(Constant.MODID, "entity/qrobot.obj");
    IBakedModel bakedModel;

    List<ResourceLocation> textures=new ArrayList<ResourceLocation>();
    List<Integer> face_count=new ArrayList<Integer>();

    public QRobotRenderer(RenderManager manager) {
        super(manager);
        //bulidModel();
        //bindTexture(ROBOT_TEX1);
    }

    public void bulidModel(){
        if (bakedModel == null) {

            try {
                OBJModel model = (OBJModel) OBJLoader.INSTANCE.loadModel(RES_MODEL);
                int count=0;
                for(OBJModel.Group g:model.getMatLib().getGroups().values()){
                    face_count.add(count+=g.getFaces().size());
                    LinkedHashSet<OBJModel.Face> g_faces=g.getFaces();
                    if(g_faces.size()>0) {
                        String mat_name = g_faces.iterator().next().getMaterialName();
                        String tex_name = model.getMatLib().getMaterial(mat_name).getTexture().getPath();
                        System.out.println("rob tex:" + mat_name + "," + tex_name);

                        textures.add(new ResourceLocation(tex_name));
                    } else {
                        textures.add(new ResourceLocation(OBJModel.Texture.WHITE.getPath()));
                    }
                }
                bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, new DefaultTextureGetter());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {

        public TextureAtlasSprite apply(ResourceLocation location) {
            System.out.println("robot:"+location.toString());
            //QRobotRenderer.this.bindTexture(location);
            /*if(location.toString().startsWith("qtrans"))
                textures.add(location);*/

            TextureAtlasSprite tas=Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
            System.out.println(tas);
            /*try {
                debug(Minecraft.getMinecraft().getTextureMapBlocks());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }*/
            return tas;
        }

        public void debug(TextureMap textureMap) throws NoSuchFieldException, IllegalAccessException {
            Class<?> clazz = textureMap.getClass();

            System.out.println("field_110574_e");
            Field f1 = clazz.getDeclaredField("field_110574_e");
            f1.setAccessible(true);
            Map<String, TextureAtlasSprite> m1=(Map) f1.get(textureMap);

            for(String s:m1.keySet()){
                if(s.startsWith("qtrans"))
                    System.out.println(s);
            }

            System.out.println("field_94252_e");
            Field f2 = clazz.getDeclaredField("field_94252_e");
            f2.setAccessible(true);
            Map<String, TextureAtlasSprite> m2=(Map) f2.get(textureMap);

            for(String s:m2.keySet()){
                if(s.startsWith("qtrans"))
                    System.out.println(s);
            }

        }
    }

    @Override
    public void doRender(QRobotEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(x,y,z);
        //GlStateManager.translate(entity.posX, entity.posY, entity.posZ);
        //GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        //System.out.println(entity.getPosition());
        //System.out.println(x+","+y+","+z);

        bindEntityTexture(entity);
        //bindTexture(ROBOT_TEX1);
        bulidModel();

        /*Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer()
                .renderModelBrightnessColor(bakedModel, 1.0F, 1.0F, 1.0F, 1.0F);*/
        renderModelBrightnessColorQuads(1.0F, 1.0F, 1.0F, 1.0F, bakedModel.getQuads(null,null, 0L));

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
        super.doRender(entity,x,y,z,entityYaw,partialTicks);
    }

    private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        int i = 0;

        //System.out.println("qsize:"+listQuads.size());
        int p_tex=0;

        //bindTexture(textures.get(p_tex));
        for (int j = listQuads.size(); i < j; ++i)
        {
            BakedQuad bakedquad = listQuads.get(i);

            while(i>=face_count.get(p_tex)) {
                p_tex++;
                bindTexture(textures.get(p_tex));
            }
            //bindTexture(new ResourceLocation()bakedquad.getSprite().getIconName());

            //System.out.println("rander:"+bakedquad.getSprite().getIconName());

            bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
            bufferbuilder.addVertexData(bakedquad.getVertexData());

            if (bakedquad.hasTintIndex())
            {
                bufferbuilder.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
            }
            else
            {
                bufferbuilder.putColorRGB_F4(brightness, brightness, brightness);
            }

            Vec3i vec3i = bakedquad.getFace().getDirectionVec();
            bufferbuilder.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            tessellator.draw();
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(QRobotEntity entity) {
        /*TextureAtlasSprite tas=Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(ROBOT_TEX1);
        System.out.println("load textures");*/
        return ROBOT_TEX1;
        //return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

}
