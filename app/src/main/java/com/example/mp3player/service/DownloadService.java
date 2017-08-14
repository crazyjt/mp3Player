package com.example.mp3player.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.mp3player.Mp3ListActivity;
import com.example.mp3player.R;
import com.example.mp3player.download.HttpDownloader;
import com.example.mp3player.model.Mp3Info;

/**
 * Created by 钧童 on 2017/8/5.
 */

public class DownloadService extends Service {

    private String downloadResult = null;
    private static final int NOTIFICATION_DOWNLOAD = 0X123;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Mp3Info mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
        DownloadThread downloadThread = new DownloadThread(mp3Info);
        Thread thread = new Thread(downloadThread);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        Thread thread1 = new Thread(new NotifyThread());
        thread1.start();
        return super.onStartCommand(intent, flags, startId);
    }

    //用独立的线程下载歌曲文件
    class DownloadThread implements Runnable{

        private Mp3Info mp3Info = null;
        public DownloadThread(Mp3Info mp3Info){
            this.mp3Info = mp3Info;
        }
        @Override
        public void run() {
            //歌曲下载地址：http://localhost:8080/mp3/歌曲名称.mp3
            //根据mp3文件名，生成下载地址
            String mp3Url = "http://192.168.230.1:8080/mp3/" + mp3Info.getMp3Name();
            String lrcUrl = "http://192.168.230.1:8080/mp3/" + mp3Info.getLrcName();
            HttpDownloader httpDownloader = new HttpDownloader();
            //下载mp3文件
            int result = httpDownloader.fileDownload(mp3Url, "mp3",  mp3Info.getMp3Name());
            //下载lrc文件
            httpDownloader.fileDownload(lrcUrl, "mp3", mp3Info.getLrcName());
            switch (result){
                case -1:
                    downloadResult = "下载失败！";
                    break;
                case 0:
                    downloadResult = "下载成功！";
                    break;
                case 1:
                    downloadResult = "文件已存在！";
                    break;
            }
            System.out.println("downloadResult in Thread" + downloadResult);
        }
    }

    //使用notification通知栏显示下载结果
    class NotifyThread implements Runnable{

        @Override
        public void run() {
            notifyDownload();
        }
    }

    public void notifyDownload(){
        try{
            Thread.currentThread().sleep(5000);
        }catch (Exception e){
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, Mp3ListActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("mp3下载")
                .setContentText(downloadResult)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.notification_icon)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .getNotification();
        notificationManager.notify(NOTIFICATION_DOWNLOAD, notification);
        downloadResult = "";
    }
}
