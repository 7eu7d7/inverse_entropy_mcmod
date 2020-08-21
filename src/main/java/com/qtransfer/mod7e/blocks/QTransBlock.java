package com.qtransfer.mod7e.blocks;

import com.qtransfer.mod7e.blocks.energy.EnergyProviderEntity;
import com.qtransfer.mod7e.blocks.energy.Wire;
import com.qtransfer.mod7e.blocks.transfer.Pipe;
import com.qtransfer.mod7e.blocks.transfer.WaveStabilizer;
import com.qtransfer.mod7e.blocks.transfer.WaveStabilizerEntity;
import com.qtransfer.mod7e.energy.CapabilityQEnergy;
import com.qtransfer.mod7e.energy.IQEProvider;
import com.qtransfer.mod7e.energy.IQEUser;
import com.qtransfer.mod7e.transfer.IRequestable;
import com.qtransfer.mod7e.transfer.IStorageable;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class QTransBlock extends Block {
    public static final IProperty<EnumFacing> FACING = PropertyDirection.create("facing");

    private boolean preserveTileEntity;
    private boolean energyable=false;
    public boolean energy_init=false;
    public boolean rotateable=true;

    public QTransBlock(boolean preserve){
        this();
        preserveTileEntity=preserve;
    }

    public QTransBlock() {
        super(Material.ROCK, MapColor.LIGHT_BLUE);
        setHardness(0.6F);
        setHarvestLevel("pickaxe", 0);
        setResistance(1000);
    }

    public QTransBlock(Material material) {
        super(material);
        setHardness(0.6F);
        setHarvestLevel("pickaxe", 0);
        setResistance(1000);
    }

    public void disableEnergy(){
        energyable=false;
    }
    public void enableEnergy(){
        energyable=true;
    }


    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer, final EnumHand hand) {
        if(rotateable) {
            final EnumFacing newFacing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
            return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, newFacing);
        } else {
            return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random)
    {
        return 1;
    }

    @Override
    public boolean requiresUpdates()
    {
        return true;
    }


    @Override
    public boolean removedByPlayer(final IBlockState state, final World world, final BlockPos pos, final EntityPlayer player, final boolean willHarvest) {
        // If it will harvest, delay deletion of the block until after getDrops
        return preserveTileEntity && willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void harvestBlock(final World world, final EntityPlayer player, final BlockPos pos, final IBlockState state, @Nullable final TileEntity te, final ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);

        if (preserveTileEntity) {
            world.setBlockToAir(pos);
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);

        if(energyable){
            System.out.println(worldIn.getBlockState(pos).getBlock().getRegistryName());
            for(EnumFacing face:EnumFacing.VALUES){
                Block block=worldIn.getBlockState(pos.offset(face)).getBlock();
                if(block instanceof Wire || (block instanceof QTransBlock && ((QTransBlock) block).energyable)){
                    updateEnergyNet(worldIn,pos.offset(face), Arrays.asList(new BlockPos[]{pos}));
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if(energyable) {
            updateEnergyNet(worldIn, pos);
        }
    }

    /*@Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        if(energy_init){
            System.out.println("q add "+pos);
            if(!((EnergyProviderEntity)worldIn.getTileEntity(pos)).initok) {
                ((EnergyProviderEntity)worldIn.getTileEntity(pos)).initok=true;
                updateEnergyNet(worldIn, pos);
            }
        }
    }*/

    public static void updateQuantumNet(World world, BlockPos pos){
        if(world.isRemote)
            return;

        //广度搜索查找连接的方块
        HashSet<BlockPos> visited=new HashSet<BlockPos>();
        Queue<BlockPos> queue = new LinkedList<BlockPos>();

        List<WaveStabilizerEntity> wslist=new ArrayList<WaveStabilizerEntity>();
        HashMap<String,IRequestable> reqlist=new HashMap<String,IRequestable>();
        List<IStorageable> bufferist=new ArrayList<IStorageable>();
        List<IRequestable> storageist=new ArrayList<IRequestable>();

        queue.offer(pos);

        while (!queue.isEmpty()) {
            BlockPos nowpos=queue.poll();

            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos nextpos = nowpos.offset(facing);
                if(visited.contains(nextpos))
                    continue;

                IBlockState blk_state=world.getBlockState(nextpos);
                Block blk=blk_state.getBlock();
                if (blk instanceof Pipe) {
                    queue.offer(nextpos);
                    visited.add(nextpos);
                }else if(blk instanceof WaveStabilizer){
                    wslist.add((WaveStabilizerEntity) world.getTileEntity(nextpos));
                    visited.add(nextpos);
                }else if(blk.hasTileEntity(blk_state)) {
                    TileEntity entity=world.getTileEntity(nextpos);
                    if (entity instanceof IRequestable) {
                        reqlist.put(((IRequestable) entity).getName(),(IRequestable) entity);
                        visited.add(nextpos);
                    } else if (entity instanceof IStorageable) {
                        bufferist.add((IStorageable) entity);
                        visited.add(nextpos);
                    }
                }
            }
        }

        //处理检索结果
        for(WaveStabilizerEntity wse:wslist){
            wse.qi_list.clear();
            wse.qi_list.putAll(reqlist);
            wse.buffer_list.clear();
            wse.buffer_list.addAll(bufferist);
            wse.updateNetwork();
        }
    }

    public static void updateEnergyNet(World world, BlockPos pos) {
        updateEnergyNet(world,pos,null);
    }

    public static void updateEnergyNet(World world, BlockPos pos,Collection<BlockPos> pos_ignore){
        if(world.isRemote)
            return;
        System.out.println("update");
        //广度搜索查找连接的方块
        HashSet<BlockPos> visited=new HashSet<BlockPos>();
        Queue<BlockPos> queue = new LinkedList<BlockPos>();

        List<IQEProvider> provlist=new ArrayList<IQEProvider>();
        List<IQEUser> userist=new ArrayList<IQEUser>();

        queue.offer(pos);
        if(pos_ignore!=null)
            visited.addAll(pos_ignore);

        while (!queue.isEmpty()) {
            BlockPos nowpos=queue.poll();

            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos nextpos = nowpos.offset(facing);
                if(visited.contains(nextpos))
                    continue;

                IBlockState blk_state=world.getBlockState(nextpos);
                Block blk=blk_state.getBlock();
                if (blk instanceof Wire) {
                    queue.offer(nextpos);
                    visited.add(nextpos);
                }else if(blk.hasTileEntity(blk_state)) {
                    TileEntity entity=world.getTileEntity(nextpos);
                    if (entity.hasCapability(CapabilityQEnergy.QENERGY_PROVIDER,facing.getOpposite())) {
                        if(entity instanceof EnergyProviderEntity)
                            ((EnergyProviderEntity)entity).initok=true;
                        provlist.add(entity.getCapability(CapabilityQEnergy.QENERGY_PROVIDER,facing.getOpposite()));
                        queue.offer(nextpos);
                        visited.add(nextpos);
                    }
                    if (entity.hasCapability(CapabilityQEnergy.QENERGY_USER,facing.getOpposite())) {
                        userist.add(entity.getCapability(CapabilityQEnergy.QENERGY_USER,facing.getOpposite()));
                        queue.offer(nextpos);
                        visited.add(nextpos);
                    }
                }
            }
        }

        //处理检索结果
        for(IQEProvider prov:provlist){
            prov.setUsers(userist);
        }
    }
}
