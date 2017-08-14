package com.example.mp3player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mp3player.lrc.LrcProcessor;
import com.example.mp3player.model.Mp3Info;
import com.example.mp3player.service.PlayerService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by 钧童 on 2017/8/11.
 */

public class PlayerActivity extends Activity {

    ImageButton start;
    ImageButton pause;
    ImageButton stop;
    TextView tvLrc;
    //表示播放状态的标识
    private boolean isPlaying = false;
    private boolean isPause = false;
    private boolean isReleased = false;
    private Mp3Info mp3Info = null;
    private ArrayList<Queue> queues = null;
    private Handler handler = new Handler();
    private UpdateTimeCallback updateTimeCallback = null;
    private long begin = 0;
    private long nextTimeMill = 0;
    private long currentTimeMill = 0;
    private long pauseTimeMill = 0;
    private String message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        View v = findViewById(R.id.Linearlayout);
        v.getBackground().setAlpha(200);

        start = (ImageButton)findViewById(R.id.ibtnStart);
        pause = (ImageButton)findViewById(R.id.ibtnPause);
        stop = (ImageButton)findViewById(R.id.ibtnStop);
        tvLrc = (TextView)findViewById(R.id.tvLrc);
        start.setImageResource(R.drawable.start);
        pause.setImageResource(R.drawable.pause);
        stop.setImageResource(R.drawable.stop);
        pause.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.INVISIBLE);
        start.setOnClickListener(new StartListener());
        pause.setOnClickListener(new PauseListener());
        stop.setOnClickListener(new StopListener());

        Intent intent = getIntent();
        mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");

    }

    class StartListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this, PlayerService.class);
            intent.putExtra("mp3Info", mp3Info);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            prepareLrc(mp3Info.getLrcName());
            startService(intent);
            begin = System.currentTimeMillis();
            handler.postDelayed(updateTimeCallback, 5);
            isPlaying = true;
            pause.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            start.setVisibility(View.INVISIBLE);
        }
    }

    class PauseListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this, PlayerService.class);
            intent.putExtra("mp3Info", mp3Info);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
            startService(intent);
            if(isPlaying){
                System.out.println("pauseListener  isPlaying is true");
                handler.removeCallbacks(updateTimeCallback);
                pauseTimeMill = System.currentTimeMillis();
                pause.setImageResource(R.drawable.start);
            } else {
                System.out.println("pauseListener  isPlaying is false");
                handler.postDelayed(updateTimeCallback, 5);
                begin = System.currentTimeMillis() - pauseTimeMill + begin;
                pause.setImageResource(R.drawable.pause);
            }
            isPlaying = isPlaying ? false : true;
            isPause = isPause ? false : true;
        }
    }

    class StopListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this, PlayerService.class);
            intent.putExtra("mp3Info", mp3Info);
            intent.putExtra("MSG", AppConstant.PlayerMsg.STOP_MSG);
            startService(intent);
            //将线程从handler中移除
            handler.removeCallbacks(updateTimeCallback);
            stop.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.INVISIBLE);
            start.setVisibility(View.VISIBLE);
            isPlaying = false;
            isPause = false;
            isReleased = true;
        }
    }

    private void prepareLrc(String lrcName){
        try{
            InputStream inputStream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mp3" + File.separator + lrcName);
            LrcProcessor lrcProcessor = new LrcProcessor();
            queues = lrcProcessor.process(inputStream);
            //给UpdateTimeCallback对象赋值
            updateTimeCallback = new UpdateTimeCallback(queues);
            begin = 0;
            currentTimeMill = 0;
            nextTimeMill = 0;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class UpdateTimeCallback implements Runnable{
        Queue times = null;
        Queue messages = null;
        public UpdateTimeCallback(ArrayList<Queue> queues){
            times = queues.get(0);
            messages = queues.get(1);
        }
        @Override
        public void run() {
            //定义偏移量
            long offset = System.currentTimeMillis() - begin;
            if(currentTimeMill == 0){
                //获取下一次的时间点和歌词
                nextTimeMill = (long) times.poll();
                message = (String) messages.poll();
            }
            if(offset >= nextTimeMill){
                tvLrc.setText(message);
                nextTimeMill = (long) times.poll();
                message = (String) messages.poll();
            }
            currentTimeMill = currentTimeMill + 10;
            //每10ms进行一次此线程
            handler.postDelayed(updateTimeCallback, 10);
        }
    }
}
