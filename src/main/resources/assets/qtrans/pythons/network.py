from com.qtransfer.mod7e.python import NetWork

def download_file(urlPath, fileSavePath, call_back=None):
    NetWork.downloadFile(urlPath, fileSavePath,call_back)