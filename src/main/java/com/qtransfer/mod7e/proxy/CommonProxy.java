package com.qtransfer.mod7e.proxy;

import com.qtransfer.mod7e.*;
import com.qtransfer.mod7e.blocks.*;
import com.qtransfer.mod7e.blocks.energy.*;
import com.qtransfer.mod7e.blocks.transfer.AutoCrafterEntity;
import com.qtransfer.mod7e.blocks.transfer.QuantumBufferEntity;
import com.qtransfer.mod7e.blocks.transfer.QuantumInterfaceEntity;
import com.qtransfer.mod7e.blocks.transfer.WaveStabilizerEntity;
import com.qtransfer.mod7e.entity.FakePlayerLoader;
import com.qtransfer.mod7e.entity.QRobotEntity;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import com.qtransfer.mod7e.python.PythonScript;
import com.qtransfer.mod7e.utils.ExtClasspathLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.Properties;

import static com.qtransfer.mod7e.Constant.MODID;

public class CommonProxy
{

	public void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
		QNetworkManager.INSTANCE.init();
	}

	public void init(FMLInitializationEvent event)
	{
        Properties props = new Properties();
        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site", "false");
        props.put("python.cachedir.skip", "false");
        props.put("python.cachedir", "./jcache"); // again, this option is optional
        PythonInterpreter.initialize(System.getProperties(), props, new String[0]);

		PythonScript.createInterpreter().execfile("./init.py");
		//PythonScript.createInterpreter().exec(Utils.readAssets("pythons/init.py"));

        ArrayList<String> jars=new ArrayList<String>();
        jars.add(Utils.getJarPath(getClass()));
        ExtClasspathLoader.loadClasspath(jars);

        System.out.println("init python");
        System.out.println(Utils.getJarPath(getClass()));
	    new GuiElementLoader();
        new FakePlayerLoader();
	}

	public void postInit(FMLPostInitializationEvent event)
	{
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public void OnBlockRegistration(RegistryEvent.Register<Block> event)
	{
        new BlockMaker();
		GameRegistry.registerTileEntity(H2ODecomposerEntity.class, Constant.item("h2o_decmp_entity"));
		GameRegistry.registerTileEntity(WaveStabilizerEntity.class, Constant.item("wave_stabilizer_entity"));
		GameRegistry.registerTileEntity(QuantumInterfaceEntity.class, Constant.item("quantum_interface_entity"));
		GameRegistry.registerTileEntity(QuantumBufferEntity.class, Constant.item("quantum_buffer_entity"));
		GameRegistry.registerTileEntity(AutoCrafterEntity.class, Constant.item("auto_crafter_entity"));
		GameRegistry.registerTileEntity(BlockFluidTankEntity.class, Constant.item("fluid_tank_entity"));
		GameRegistry.registerTileEntity(AirIonizerEntity.class, Constant.item("air_ionizer_entity"));
		GameRegistry.registerTileEntity(ElectronConstraintorEntity.class, Constant.item("electron_constraintor_entity"));
		GameRegistry.registerTileEntity(EnergyConverterEntity.class, Constant.item("energy_converter_entity"));
		GameRegistry.registerTileEntity(BlockShaperEntity.class, Constant.item("block_shaper_entity"));
		GameRegistry.registerTileEntity(QuantumChestEntity.class, Constant.item("quantum_chest_entity"));
		GameRegistry.registerTileEntity(EnergyBoxEntity.class, Constant.item("energy_box_entity"));

        new FluidMaker();
        for (final IFluidBlock fluidBlock : FluidMaker.fluidlist) {
            final Block block = (Block) fluidBlock;
            BlockMaker.addBlock(block, fluidBlock.getFluid().getName());
        }


		for(Block block:BlockMaker.blocklist){
			event.getRegistry().register(block);
		}


	}

	@SubscribeEvent
	public void OnItemRegistration(RegistryEvent.Register<Item> event)
	{
        new ItemMaker();
		for(Item item: ItemMaker.itemlist) {
			event.getRegistry().register(item);
		}

		for(Fluid fluid:FluidMaker.fluidlist_raw)
            FluidRegistry.addBucketForFluid(fluid);
	}

	@SubscribeEvent
	public void OnPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if (player == null) return;

		ItemStack heldItemStack = event.getItemStack();
		if (heldItemStack.isEmpty()) return;

        if(!event.getWorld().isRemote && heldItemStack.getItem().getRegistryName().toString().equals(Constant.item("qrobot_item"))){
            EntityLiving entityLiving = new QRobotEntity(event.getWorld());
            BlockPos pos = event.getPos();
            entityLiving.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
            heldItemStack.shrink(1);
            event.getWorld().spawnEntity(entityLiving);
        }

		/*TileEntity tileEntity = player.getEntityWorld().getChunkFromBlockCoords(event.getPos()).getTileEntity(event.getPos(), Chunk.EnumCreateEntityType.CHECK);
		if (tileEntity instanceof H2ODecomposerEntity)
		{
			event.setUseBlock(Event.Result.ALLOW);
			event.setUseItem(Event.Result.DENY);
		}*/
	}

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        System.out.println("Entries registered");
        event.getRegistry().register(EntityEntryBuilder.create().entity(QRobotEntity.class)
                .id(new ResourceLocation(Constant.MODID, "robot_entity"), 0)
                .name("QRobotEntity").tracker(128, 2, true)
                .egg(0x4c3e30, 0xf0f0f).build());

    }

	/*@SubscribeEvent
	public void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<TileEntity> event){
		if(event.getObject() != null){
			ICapabilitySerializable<NBTTagCompound> providerConsciousness = new CapabilityQEnergy();
			event.addCapability(new ResourceLocation(Constant.item("qenergy")), providerConsciousness);
		}
	}*/
}
