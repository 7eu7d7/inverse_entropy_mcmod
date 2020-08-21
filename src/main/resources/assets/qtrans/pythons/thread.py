from com.qtransfer.mod7e.python import ThreadPython

def new_thread(func):
    return ThreadPython(func)