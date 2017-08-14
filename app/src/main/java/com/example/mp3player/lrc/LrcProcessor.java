package com.example.mp3player.lrc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 钧童 on 2017/8/13.
 */

public class LrcProcessor {
    //读取歌词文件
    public ArrayList<Queue> process(InputStream inputStream){
        //定义歌词队列
        Queue<String> messages = new LinkedList<String>();
        //定义时间队列
        Queue<Long> timeMills = new LinkedList<Long>();
        //定义ArrayList对象，存储两个队列
        ArrayList<Queue> queues = new ArrayList<Queue>();
        //读取文件
        InputStreamReader inputStreamReader =null;
        BufferedReader bufferedReader = null;
        try{
            inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
            //BufferedReader对象可以逐行读取
            bufferedReader = new BufferedReader(inputStreamReader);
            //定义正则表达式，用于过滤带中括号的时间信息
            Pattern p = Pattern.compile("\\[([^\\]]+)\\]");
            String temp = null;
            String result = null;
            int i = 0;
            while ((temp = bufferedReader.readLine()) != null){
                //歌词行数
                i++;
                System.out.println("temp------>" + temp);
                //创建过滤器对象,并对每行数据进行过滤
                Matcher m = p.matcher(temp);
                //找到中括号的操作
                if(m.find()){
                    if(result != null){
                        //数据加入歌词队列
                        messages.add(result);
                    }
                    //用group方法获得中括号中的信息
                    String timeStr = m.group();
                    //将字符串转化为Long型数据
                    Long timeMill = strToLong(timeStr.substring(1, timeStr.length()-1));
                    timeMills.add(timeMill);
                    //获取从第11个字符之后的字符串
                    String msg = temp.substring(10);
                    result = msg + "\n";
                } else {
                    result = result + temp + "\n";
                }
            }
            //加入最后一行歌词
            messages.add(result);
            queues.add(timeMills);
            queues.add(messages);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                bufferedReader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return queues;
    }

    public Long strToLong(String timeStr){
        //将冒号两端分开
        String s[] = timeStr.split(":");
        int min = Integer.parseInt(s[0]);
        //将句号两端分开
        String s1[] = s[1].split("\\.");
        int sec = Integer.parseInt(s1[0]);
        int mill = Integer.parseInt(s1[1]);
        //最终将时间转换为毫秒
        return min * 60 * 1000 + sec * 1000 + mill * 10L;
    }
}
