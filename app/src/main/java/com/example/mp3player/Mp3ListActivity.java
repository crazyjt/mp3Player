package com.example.mp3player;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mp3player.download.HttpDownloader;
import com.example.mp3player.model.Mp3Info;
import com.example.mp3player.service.DownloadService;
import com.example.mp3player.xml.Mp3ListContentHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;

public class Mp3ListActivity extends ListActivity {

    private static final int UPDATE = 1;
    private static final int ABOUT = 2;
    private static final int UPDATELIST = 0X001;
    private List<Mp3Info> mp3Infos = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_mp3_list);

    }

    //用户点击Menu之后调用该方法
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, UPDATE, 1, R.string.mp3list_update);
        menu.add(0, ABOUT, 2, R.string.mp3list_about);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case UPDATE:
                new Thread(){
                    @Override
                    public void run() {
                        //调用函数下载包含歌曲信息的xml文件
                        //网络操作需要在子线程中进行
                        String xml = downloadXML("http://192.168.230.1:8080/mp3/resources.xml");
                        mp3Infos = parse(xml);
                        //UI操作需要在主线程中进行，所以利用handler机制
                        handler.sendEmptyMessage(UPDATELIST);
                    }
                }.start();
                break;
            case ABOUT:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //自定义listView的数据绑定方法
    //当用户点击菜单更新列表项时调用
    private void updateListView(){
        SimpleAdapter simpleAdapter = buildSimpleAdapter(mp3Infos);
        setListAdapter(simpleAdapter);
    }

    private String downloadXML(String urlStr){
        HttpDownloader httpDownloader = new HttpDownloader();
        String result = httpDownloader.download(urlStr);
        return result;
    }

    private List<Mp3Info> parse(String xmlStr){
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        List<Mp3Info> infos = new ArrayList<Mp3Info>();
        try{
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
            Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(infos);
            xmlReader.setContentHandler(mp3ListContentHandler);
            xmlReader.parse(new InputSource(new StringReader(xmlStr)));
        }catch (Exception e){
            e.printStackTrace();
        }
        return infos;
    }

    //自定义SimpleAdapter的创建方法
    //将infos中的数据加入一个List<map<,>>中，作为SimpleAdapter的参数，并且新建一个SimpleAdapter对象
    private SimpleAdapter buildSimpleAdapter(List<Mp3Info> mp3Infos){
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        //将infos中的每个Mp3Info对象加入list中
        for(Iterator iterator = mp3Infos.iterator(); iterator.hasNext(); ){
            HashMap<String, String> map = new HashMap<String, String>();
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            map.put("mp3_name", mp3Info.getMp3Name());
            map.put("mp3_size", mp3Info.getMp3Size());
            list.add(map);
        }
        //新建SimpleAdapter对象
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                list,
                R.layout.mp3info_item,
                new String[]{"mp3_name", "mp3_size"},
                new int[]{R.id.mp3_name, R.id.mp3_size});
        return simpleAdapter;
    }

    //handler用于修改UI
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATELIST:
                    //调用ListView更新函数
                    updateListView();
                    break;
            }
        }
    };

    //ListView项监听事件
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Mp3Info mp3Info = mp3Infos.get(position);
        //进行启动service
        Intent intent = new Intent();
        intent.setClass(Mp3ListActivity.this, DownloadService.class);
        //在intent中放入数据，参数为实现了序列化的mp3Info数据
        intent.putExtra("mp3Info", mp3Info);
        startService(intent);
        super.onListItemClick(l, v, position, id);
    }

}
