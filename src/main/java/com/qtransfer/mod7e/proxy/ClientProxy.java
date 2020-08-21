package com.qtransfer.mod7e.proxy;

import com.qtransfer.mod7e.*;
import com.qtransfer.mod7e.blocks.BlockFluidTankEntity;
import com.qtransfer.mod7e.blocks.energy.AirIonizerEntity;
import com.qtransfer.mod7e.blocks.render.*;
import com.qtransfer.mod7e.entity.QRobotEntity;
import com.qtransfer.mod7e.entity.QRobotRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		TabsList.addCreativeTab();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
	}

    /*@SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event)
    {
        event.getMap().registerSprite(Constant.TEXTURE_LIGHTING);
    }*/

	@SubscribeEvent
	public void loadModel(ModelRegistryEvent event) {

		OBJLoader.INSTANCE.addDomain(Constant.MODID);

        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
                return AirIonizerBakedModel.variantTag;
            }
        };
        ModelLoader.setCustomStateMapper(BlockMaker.air_ionizer, ignoreState);

		StateMapperBase ignoreState_ft = new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
				return FluidTankBakedModel.variantTag;
			}
		};
		ModelLoader.setCustomStateMapper(BlockMaker.fluid_tank, ignoreState_ft);

        FluidMaker.fluidlist.forEach(this::registerFluidModel);

        ModelLoaderRegistry.registerLoader(new ModelLoaderAI());
        ModelLoaderRegistry.registerLoader(new ModelLoaderFT());

        RenderingRegistry.registerEntityRenderingHandler(QRobotEntity.class, QRobotRenderer::new);

		for(Item item: ItemMaker.itemlist) {
			if(item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof IFluidBlock){
				continue;
			}

			System.out.println(item.getRegistryName());
            switch (item.getRegistryName().toString()) {
                case "qtrans:air_ionizer":
                    //ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "normal"));
                    ModelLoader.setCustomModelResourceLocation(item, 0, AirIonizerBakedModel.variantTag);
                    break;
                case "qtrans:fluid_tank":
                    //ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "normal"));
                    ModelLoader.setCustomModelResourceLocation(item, 0, FluidTankBakedModel.variantTag);
                    break;
                default:
                    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
                    break;
            }
		}

		//注册TESR
		//ClientRegistry.bindTileEntitySpecialRenderer(BlockFluidTankEntity.class, new FluidTankRender());
		ClientRegistry.bindTileEntitySpecialRenderer(AirIonizerEntity.class, new AirIonizerRender());
		//ModelLoader.setCustomModelResourceLocation(Item.getByNameOrId("example_block"), 0, new ModelResourceLocation("example_block", "inventory"));
	}

	private void registerFluidModel(final IFluidBlock fluidBlock) {
		final Item item = Item.getItemFromBlock((Block) fluidBlock);
		assert item != Items.AIR;

		ModelBakery.registerItemVariants(item);

		final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(Constant.item(fluidBlock.getFluid().getName()), "fluid");

		ModelLoader.setCustomMeshDefinition(item, stack -> modelResourceLocation);

		ModelLoader.setCustomStateMapper((Block) fluidBlock, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(final IBlockState p_178132_1_) {
				return modelResourceLocation;
			}
		});
	}

	/*@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event)
	{




		// Find the existing mapping for CamouflageBakedModel - it will have been added automatically because
		//  we registered a custom BlockStateMapper for it (using ModelLoader.setCustomStateMapper)
		// Replace the mapping with our CamouflageBakedModel.
		Object object =  event.getModelRegistry().getObject(AirIonizerBakedModel.variantTag);
		if (object instanceof IBakedModel) {
			IBakedModel existingModel = (IBakedModel)object;
			AirIonizerBakedModel customModel = new AirIonizerBakedModel(existingModel);
			event.getModelRegistry().putObject(AirIonizerBakedModel.variantTag, customModel);
		}
	}*/
	/*@SubscribeEvent
	public void drawWireHighlight(DrawBlockHighlightEvent event) {
		if (event.getTarget() != null && event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = event.getTarget().getBlockPos();
			World world=event.getPlayer().world;
			Block block=world.getBlockState(pos).getBlock();
			if (block instanceof Pipe)
			{
				Pipe pipe=(Pipe)block;
				Vec3d cameraPos = Utils.interpolate(event.getPlayer(), Minecraft.getMinecraft().getRenderPartialTicks());
				System.out.println(event.getTarget().hitVec);
			}
		}
	}*/

	/*@SubscribeEvent
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		BlockPos pos = event.getTarget().getBlockPos();
		World world = event.getPlayer().world;

		if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK)
		{
			if (world.getBlockState(pos).getBlock() instanceof Pipe)
			{
				event.setCanceled(true);
				System.out.println(event.getTarget().hitVec.toString());
				for (AxisAlignedBB axisAlignedBB : Pipe.CONNECTED_BOUNDING_BOXES)
				{
					drawSelectionBox(event.getPlayer(), axisAlignedBB.offset(pos), (double) event.getPartialTicks());
				}
			}
		}
	}

	private void drawSelectionBox(EntityPlayer player, AxisAlignedBB axisAlignedBB, double partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);

		double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		RenderGlobal.drawSelectionBoundingBox(axisAlignedBB.offset(-d0, -d1, -d2), 0.0F, 0.0F, 0.0F, 0.4F);

		AxisAlignedBB newBB = axisAlignedBB.offset(-d0, -d1, -d2);

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}*/
}
