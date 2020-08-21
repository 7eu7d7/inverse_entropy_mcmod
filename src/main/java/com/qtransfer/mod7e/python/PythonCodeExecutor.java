package com.qtransfer.mod7e.python;

import com.qtransfer.mod7e.Constant;
import org.python.core.*;
import org.python.util.InteractiveInterpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PythonCodeExecutor extends InteractiveInterpreter implements Runnable
{
    @SuppressWarnings("WeakerAccess")
    public static final String LIBS = Constant.MINECRAFT_LIBS;

    @SuppressWarnings("WeakerAccess")
    protected PyCode code;

    public PythonCodeExecutor() { super(null, newState()); }

    public PythonCodeExecutor(String script) { super(null, newState()); set(script); }

    public PythonCodeExecutor(File file) throws FileNotFoundException
    {
        super(null, newState());
        set(file);
    }

    public static PySystemState newState()
    {
        PySystemState state = new PySystemState();
        state.path.append(new PyString((new File(".")).getAbsolutePath()));
        //state.path.append(new PyString((new File(Config.PYMCFO_LIBS)).getAbsolutePath()));
        return state;
    }

    @Override
    public void run() {
        System.out.println(code);
        exec(code);
    }

    public PythonCodeExecutor set(String script) { return set(script, false); }

    public PythonCodeExecutor set(String script, boolean interactive)
    {
        code = Py.compile_flags(script, "<script>", interactive ? CompileMode.single : CompileMode.eval, this.cflags);
        return this;
    }

    public PythonCodeExecutor set(File file) throws java.io.FileNotFoundException
    {
        code = Py.compile_flags(new FileInputStream(file), file.getName(), CompileMode.exec, this.cflags);
        return this;
    }
}
