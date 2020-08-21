package com.qtransfer.mod7e.blocks.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class FBElectron extends BlockFluidClassic {
    public FBElectron(Fluid fluid, Material material) {
        super(fluid, material);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
        if(entityIn instanceof EntityLiving) {
            entityIn.attackEntityFrom(new DamageSource("electricShock").setDamageBypassesArmor().setDamageIsAbsolute(), 100);
        }
    }
}
