package com.example.mp3player;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Created by 钧童 on 2017/8/11.
 */

public class MainActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TabHost tabHost = getTabHost();

        //生成一个intent对象，指向Mp3ListActivity
        Intent remoteIntent = new Intent();
        remoteIntent.setClass(this, Mp3ListActivity.class);
        TabHost.TabSpec remoteSpec = tabHost.newTabSpec("Remote");
        //设置选项卡便签和图标
        remoteSpec.setIndicator("Remote", getResources().getDrawable(android.R.drawable.stat_sys_download));
        remoteSpec.setContent(remoteIntent);
        tabHost.addTab(remoteSpec);

        //生成一个intent对象，指向LocalMp3Activity
        Intent localIntent = new Intent();
        localIntent.setClass(this, LocalMp3Activity.class);
        TabHost.TabSpec localSpec = tabHost.newTabSpec("Local");
        localSpec.setIndicator("Local", getResources().getDrawable(android.R.drawable.stat_sys_download_done));
        localSpec.setContent(localIntent);
        tabHost.addTab(localSpec);
    }
}
