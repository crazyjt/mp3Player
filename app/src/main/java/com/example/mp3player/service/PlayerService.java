package com.example.mp3player.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;

import com.example.mp3player.AppConstant;
import com.example.mp3player.model.Mp3Info;

import java.io.File;

/**
 * Created by 钧童 on 2017/8/12.
 */

public class PlayerService extends Service {
    private boolean isPlaying = false;
    private boolean isPause = false;
    private boolean isReleased = false;
    //声明一个MediaPlayer对象
    MediaPlayer mediaPlayer = null;
    Mp3Info mp3Info = null;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
        //如果没有取到MSG的值，则返回0
        int MSG = intent.getIntExtra("MSG", 0);
        System.out.println("onStartCommand mp3Info:     " + mp3Info);
        System.out.println("MSG:        " + MSG);
        if(mp3Info != null){
            switch (MSG){
                case AppConstant.PlayerMsg.PLAY_MSG:
                    start(mp3Info);
                    break;
                case AppConstant.PlayerMsg.PAUSE_MSG:
                    System.out.println("before pause");
                    pause();
                    System.out.println("after pause");
                    break;
                case AppConstant.PlayerMsg.STOP_MSG:
                    stop();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void start(Mp3Info mp3Info){
        if(!isPlaying) {
            String path = getMp3Path(mp3Info);
            //调用create函数创建对象
            //提取SD卡的文件时Uri协议是file开头
            mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + path));
            mediaPlayer.setLooping(false);
            //调用MediaPlayer的start方法开始播放
            mediaPlayer.start();
            isPlaying = true;
            isReleased = false;
        }
    }

    private void pause(){
        if(mediaPlayer != null){
            if(!isReleased){
                if(!isPause){
                    //音乐暂停
                    mediaPlayer.pause();
                    isPause = true;
                    isPlaying = false;
                }
                else{
                    mediaPlayer.start();
                    isPause = false;
                    isPlaying = true;
                }
            }
        }
    }

    private void stop(){
        System.out.println("PlayerService stop()    ");
        if(mediaPlayer != null){
            if(isPlaying){
                if(!isReleased){
                    //用stop停止音乐
                    mediaPlayer.stop();
                    //释放音乐资源，不再使用
                    mediaPlayer.release();
                    isReleased = true;
                    isPlaying = false;
                }
            }
        }
    }


    private String getMp3Path(Mp3Info mp3Info){
        String SDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = SDCardPath + File.separator + "mp3" + File.separator + mp3Info.getMp3Name();
        return path;
    }
}
