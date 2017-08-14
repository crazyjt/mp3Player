package com.example.mp3player.xml;

import com.example.mp3player.model.Mp3Info;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Iterator;
import java.util.List;

/**
 * Created by 钧童 on 2017/8/3.
 */

public class Mp3ListContentHandler extends DefaultHandler {

    //用List方式存储所有的Mp3Info对象
    private List<Mp3Info> infos = null;
    private Mp3Info mp3Info = null;
    private String tagName = null;

    public Mp3ListContentHandler(List<Mp3Info> infos) {
        this.infos = infos;
    }

    public List<Mp3Info> getInfos() {
        return infos;
    }

    public void setInfos(List<Mp3Info> infos) {
        this.infos = infos;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        for(Iterator iterator = infos.iterator(); iterator.hasNext(); ){
            System.out.println("Iterator");
            Mp3Info mp3Info = (Mp3Info)iterator.next();
            System.out.println(mp3Info);
        }
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tagName = localName;
        if(tagName.equals("resource"))
            mp3Info = new Mp3Info();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equals("resource")) {
            //将当前的歌曲对象加入infos
            infos.add(mp3Info);
        }
        tagName = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String temp = new String(ch, start, length);
        if(tagName.equals("id"))
            mp3Info.setId(temp);
        else if(tagName.equals("mp3.name"))
            mp3Info.setMp3Name(temp);
        else if(tagName.equals("mp3.size"))
            mp3Info.setMp3Size(temp);
        else if(tagName.equals("lrc.name"))
            mp3Info.setLrcName(temp);
        else if(tagName.equals("lrc.size"))
            mp3Info.setLrcSize(temp);
    }
}
