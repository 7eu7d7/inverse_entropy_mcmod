package com.qtransfer.mod7e.utils;

import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidTank;

public class UnlistedPropertyFluid implements IUnlistedProperty<FluidTank> {
    String name;

    public UnlistedPropertyFluid(String name){
        this.name=name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(FluidTank value) {
        return true;
    }

    @Override
    public Class<FluidTank> getType() {
        return FluidTank.class;
    }

    @Override
    public String valueToString(FluidTank value) {
        return value.toString();
    }
}
