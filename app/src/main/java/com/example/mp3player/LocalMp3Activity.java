package com.example.mp3player;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mp3player.model.Mp3Info;
import com.example.mp3player.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 钧童 on 2017/8/11.
 */

public class LocalMp3Activity extends ListActivity {
    private List<Mp3Info> mp3Infos = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_mp3_list);

    }

    @Override
    protected void onResume() {
        FileUtils fileUtils = new FileUtils();
        mp3Infos = fileUtils.getMp3Files("mp3/");
        List<HashMap<String, String >> list = new ArrayList<HashMap<String, String>>();
        if (mp3Infos != null) {
            for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext(); ) {
                Mp3Info mp3Info = (Mp3Info) iterator.next();
                System.out.println("mp3Info:    " + mp3Info);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("mp3_name", mp3Info.getMp3Name());
                map.put("mp3_size", mp3Info.getMp3Size());
                list.add(map);
            }
        }
        else {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("mp3_name", null);
            map.put("mp3_size", null);
            list.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                list,
                R.layout.mp3info_item,
                new String[]{"mp3_name","mp3_size"},
                new int[]{R.id.mp3_name,R.id.mp3_size});
        setListAdapter(simpleAdapter);
        super.onResume();
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(mp3Infos != null){
            Mp3Info mp3Info = mp3Infos.get(position);
            Intent intent = new Intent();
            intent.putExtra("mp3Info", mp3Info);
            intent.setClass(this, PlayerActivity.class);
            startActivity(intent);
        }
        super.onListItemClick(l, v, position, id);
    }
}
