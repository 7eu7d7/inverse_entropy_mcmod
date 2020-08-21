package com.qtransfer.mod7e;

import com.qtransfer.mod7e.blocks.fluid.FBElectron;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class FluidMaker {
    public static List<IFluidBlock> fluidlist=new ArrayList<IFluidBlock>();
    public static List<Fluid> fluidlist_raw=new ArrayList<Fluid>();

    public FluidMaker(){
        addFluids();
    }

    public void addFluids(){
        createFluid("condensedelectron", false,
                fluid -> fluid.setLuminosity(10).setDensity(1000).setViscosity(200),
                fluid -> new FBElectron(fluid, new MaterialLiquid(MapColor.ADOBE)));
    }

    private static <T extends Block & IFluidBlock> Fluid createFluid(final String name, final boolean hasFlowIcon, final Consumer<Fluid> fluidPropertyApplier, final Function<Fluid, T> blockFactory) {
        final String texturePrefix = Constant.item("fluids/");

        final ResourceLocation still = new ResourceLocation(texturePrefix + name + "_still");
        final ResourceLocation flowing = hasFlowIcon ? new ResourceLocation(texturePrefix + name + "_flow") : still;

        Fluid fluid = new Fluid(name, still, flowing);
        final boolean useOwnFluid = FluidRegistry.registerFluid(fluid);
        System.out.println("fluid "+useOwnFluid);
        System.out.println("fluid "+still.toString());

        if (useOwnFluid) {
            fluidPropertyApplier.accept(fluid);
            fluidlist.add(blockFactory.apply(fluid));
        } else {
            fluid = FluidRegistry.getFluid(name);
        }

        fluidlist_raw.add(fluid);

        return fluid;
    }
}
