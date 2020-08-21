package com.qtransfer.mod7e.python;

import org.python.core.PyFunction;
import org.python.core.PyObject;

public class ThreadPython {
    PyFunction runnable;
    public ThreadPython(PyFunction func){
        runnable=func;
    }

    public void start(){
        new Thread(){
            @Override
            public void run() {
                try {
                    runnable.__call__();
                }catch (Throwable e){
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public void start(PyObject... args){
        new Thread(){
            @Override
            public void run() {
                try {
                    runnable.__call__(args);
                }catch (Throwable e){
                    e.printStackTrace();
                }

            }
        }.start();
    }
}
