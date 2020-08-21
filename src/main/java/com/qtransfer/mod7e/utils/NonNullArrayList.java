package com.qtransfer.mod7e.utils;

import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.List;

public class NonNullArrayList<E> extends NonNullList<E> {
    public NonNullArrayList(List<E> delegateIn, @Nullable E listType,int fill)
    {
        super(delegateIn,listType);
        for (int i=0;i<fill;i++)
            delegateIn.add(listType);
    }
}
