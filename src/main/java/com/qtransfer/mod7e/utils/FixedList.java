package com.qtransfer.mod7e.utils;

import java.util.ArrayList;
import java.util.LinkedList;

public class FixedList<T> extends LinkedList<T> {

    int list_size;

    public FixedList(int list_size){
        this.list_size=list_size;
    }

    @Override
    public boolean offer(T t) {
        if(size()==list_size)
            poll();
        return super.offer(t);
    }

    public boolean full(){
        return size()==list_size;
    }
}
