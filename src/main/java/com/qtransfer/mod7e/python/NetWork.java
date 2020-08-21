package com.qtransfer.mod7e.python;

import org.python.core.PyFloat;
import org.python.core.PyFunction;
import org.python.core.PyInteger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class NetWork {

    public static void downloadFile(String urlPath, String fileSavePath, PyFunction call_back){
        new Thread(){
            @Override
            public void run() {
                try {
                    downloadFile_java(urlPath,fileSavePath,call_back);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(call_back!=null){
                    call_back.__call__(new PyFloat(-1));
                }
            }
        }.start();
    }

    private static void downloadFile_java(String urlPath, String fileSavePath, PyFunction call_back) throws Exception {

        File file = null;
        BufferedInputStream bin = null;
        OutputStream out = null;
        try {
            // 统一资源
            URL url = new URL(urlPath);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            httpURLConnection.connect();

            // 文件大小
            int fileLength = httpURLConnection.getContentLength();

            // 文件名
            String filePathUrl = httpURLConnection.getURL().getFile();
            String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);
            fileFullName = fileFullName.substring(fileFullName.lastIndexOf("/") + 1);

            url.openConnection();

            bin = new BufferedInputStream(httpURLConnection.getInputStream());

            String path = fileSavePath;
            file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file);
            int size = 0;
            int len = 0;
            byte[] buf = new byte[1024];
            while ((size = bin.read(buf)) != -1) {
                len += size;
                out.write(buf, 0, size);
                // 打印下载百分比
                if(call_back!=null){
                    call_back.__call__(new PyFloat(len * 100f / fileLength));
                }
                /*if ((len * 100 / fileLength)>50&&(len * 100 / fileLength)<55) {
                    logger.info("下载了-------> " + len * 100 / fileLength + "%");
                }else    if ((len * 100 / fileLength)>=98) {
                    logger.info("下载了-------> " + len * 100 / fileLength + "%");
                }*/
                //logger.info("下载了-------> " + len * 100 / fileLength + "%");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bin!=null) {
                bin.close();
            }
            if (out!=null) {
                out.close();
            }
        }
    }
}
