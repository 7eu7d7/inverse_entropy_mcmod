package com.qtransfer.mod7e.python;

import com.qtransfer.mod7e.Utils;
import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.File;

public class PythonScript {
    PythonInterpreter interpreter;

    public PythonScript(PythonInterpreter interpreter){
        this.interpreter=interpreter;
    }

    public PythonScript(){
        //this.interpreter=new PythonInterpreter();
    }

    public static PythonInterpreter createInterpreter(){
        PySystemState state = new PySystemState();
        state.path.append(new PyString(Utils.getJarPath()+"/assets/qtrans/pythons"));
        //state.setClassLoader(Thread.currentThread().getContextClassLoader());
        return new PythonInterpreter(null,state);
    }

    public PyFunction getFunction(String name){
        return interpreter==null?null:interpreter.get(name,PyFunction.class);
    }

    public Object callFunction(String name,Object... objs){
        PyFunction func=null;
        try {
            if((func=getFunction(name))!=null){
                return func.__call__(Py.javas2pys(objs));
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
        return null;
    }

}
