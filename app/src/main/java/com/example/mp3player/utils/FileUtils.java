package com.example.mp3player.utils;

import android.os.Environment;

import com.example.mp3player.model.Mp3Info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 钧童 on 2017/8/5.
 */

//需要在模拟器中打开该应用程序的存储访问权限
public class FileUtils {

    private String SDPath = null;

    public FileUtils(){
        //获取SD卡路径
        SDPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    //创建目录
    public File createSDDir(String dirName){
        File dir = new File(SDPath + dirName + File.separator);
        dir.mkdir();
        return dir;
    }

    //创建文件
    public File createSDFile(String fileName, String dir) throws IOException {
        File file = new File(SDPath + dir + File.separator + fileName);
        file.createNewFile();
        return file;
    }

    //判断文件是否存在
    public boolean isFileExist(String fileName,String path){
        File file = new File(SDPath + path + File.separator + fileName);
        return file.exists();
    }

    //文件写入SD卡
    public File writeToSDFromInput(String path, String fileName, InputStream inputStream){
        File file = null;
        OutputStream outputStream = null;
        try {
            createSDDir(path);
            file = createSDFile(fileName,path);
            outputStream = new FileOutputStream(file);
//            OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
            byte bytes[] = new byte[4 *1024];
            int temp;
            while ((temp = inputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0 , temp);
            }
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return  file;
    }

    //读取目录中MP3文件的名字和大小
    public List<Mp3Info> getMp3Files(String path){
        List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        File file = new File(SDPath + File.separator + path);
            //返回当前文件中（即文件“mp3”）中的所有文件
        File[] files = file.listFiles();
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith("mp3")) {
                    Mp3Info mp3Info = new Mp3Info();
                    String mp3Name = files[i].getName();
                    String mp3Size = files[i].length() + "";
                    mp3Info.setMp3Name(mp3Name);
                    mp3Info.setMp3Size(mp3Size);
                    for(int j = 0; j < files.length; j++){
                        String mp3Names[] = mp3Name.split("\\.");
                        String lrcNames[] = files[j].getName().split("\\.");
                        if(lrcNames[0].equals(mp3Names[0]) && lrcNames[1].equals("lrc")){
                            mp3Info.setLrcName(files[j].getName());
                            mp3Info.setLrcSize(files[j].length() + "");
                        }
                    }
                    mp3Infos.add(mp3Info);
                }
            }
            return mp3Infos;
        }
        else
            return null;
    }
}
