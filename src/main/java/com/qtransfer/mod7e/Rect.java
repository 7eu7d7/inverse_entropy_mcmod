package com.qtransfer.mod7e;

public class Rect {
    public int x,y,w,h;
    public Rect(int x,int y,int w,int h){
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
    }

    public int mapto_X(int p,Rect dst){
        return dst.w*(p-x)/w+dst.x;
    }

    public int mapto_Y(int p,Rect dst){
        return dst.h*(p-y)/h+dst.y;
    }

    public boolean inRect(int px,int py){
        return px>=x && px<=x+w && py>=y && py<=y+h;
    }

    @Override
    public String toString() {
        return "x"+x+" y"+y+" w"+w+" h"+h;
    }
}
