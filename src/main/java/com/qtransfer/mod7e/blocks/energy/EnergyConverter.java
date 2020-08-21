package com.qtransfer.mod7e.blocks.energy;

import com.qtransfer.mod7e.Constant;
import com.qtransfer.mod7e.blocks.QTransBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

public class EnergyConverter extends QTransBlock{
    public EnergyConverter(){
        super();
        enableEnergy();
        energy_init=true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new EnergyConverterEntity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        if(worldIn.getTileEntity(fromPos)!=null &&
                worldIn.getTileEntity(fromPos).hasCapability(CapabilityEnergy.ENERGY, EnumFacing.getFacingFromVector(pos.getX()-fromPos.getX(),pos.getY()-fromPos.getY(),pos.getZ()-fromPos.getZ()))){
            ((EnergyConverterEntity)worldIn.getTileEntity(pos)).fe_map.put(fromPos,worldIn.getTileEntity(fromPos).getCapability(CapabilityEnergy.ENERGY, EnumFacing.getFacingFromVector(pos.getX()-fromPos.getX(),pos.getY()-fromPos.getY(),pos.getZ()-fromPos.getZ())));
        } else {
            ((EnergyConverterEntity)worldIn.getTileEntity(pos)).fe_map.remove(fromPos);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
    }
}
