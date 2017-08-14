package com.example.mp3player.download;

import com.example.mp3player.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 钧童 on 2017/8/2.
 */

public class HttpDownloader {
    private URL url = null;

    public String download(String urlStr){
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = null;
        String result = null;
        try {
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = conn.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while((result = bufferedReader.readLine()) != null){
                stringBuffer.append(result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                bufferedReader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }

    //下载成功返回0，下载出错返回-1，文件已存在返回1
    public int fileDownload(String urlStr, String path, String fileName){
        InputStream inputStream = null;
        try{
            FileUtils fileUtils = new FileUtils();
            if(fileUtils.isFileExist(fileName,path))
                return 1;
            else {
                url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                inputStream = conn.getInputStream();
                File file = fileUtils.writeToSDFromInput(path, fileName, inputStream);
                if(file == null)
                    return -1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }
}
