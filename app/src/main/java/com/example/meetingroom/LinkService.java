package com.example.meetingroom;

import android.content.Context;
import android.widget.Button;
import android.widget.TableRow;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class LinkService {
    private Context context;

    private String linkFile = "links.txt";

//    private String saluteJazz= "https://salutejazz.ru/#/calls/";

    public LinkService(Context c){
        context = c;
    }
    public void saveText(String text){

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(linkFile, context.MODE_PRIVATE);
            fos.write(text.getBytes());
        }
        catch(IOException ex) {
            LogManager.getInstance().addLog("Ошибка записи файла:"+ex.getMessage());
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){
                LogManager.getInstance().addLog("Ошибка закрытия файла:"+ex.getMessage());

            }
        }
    }


    public String openText(){

        FileInputStream fin = null;
        try {
            fin = context.openFileInput(linkFile);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            LogManager.getInstance().addLog(text);
            return text;
        }
        catch(IOException ex) {
            LogManager.getInstance().addLog("Ошибка чтения файла:"+ex.getMessage());
        }
        finally{
            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){
                LogManager.getInstance().addLog("Ошибка закрытия файла:"+ex.getMessage());
            }
        }
        return null;
    }

    public ArrayList<Link> readLinks(){
        String text = openText();
        if (text==null || text.equals(""))
            return initLinks();
        StringTokenizer tokenizer = new StringTokenizer(text, "\n");
        ArrayList<Link> list = new ArrayList<>();
        while(tokenizer.hasMoreElements()){
            String row = tokenizer.nextToken();
            String name = row.substring(0, row.indexOf("["));
            String url = row.substring(row.indexOf("[")+1, row.indexOf("]"));
            list.add(new Link(name.trim(), url));
        }
        return  list;
    }

    private ArrayList<Link> initLinks() {
        Link l1 = new Link("Daily", "https://jazz.sber.ru/in0sec?psw=OBgWD0RREwQNCBEZG0UEFwICDA");
        ArrayList<Link> linkList = new ArrayList<>();
        linkList.add(l1);

        Link l2 = new Link("Daily2", "jazz://join?psw=OBgWD0RREwQNCBEZG0UEFwICDA&id=in0sec%40salutejazz.ru&password=OBgWD0RREwQNCBEZG0UEFwICDA&utmClientId=a1f80e90-1412-11ef-8de0-592e8564d2c5&clientId=a1f80e90-1412-11ef-8de0-592e8564d2c5");
        linkList.add(l2);

        saveLinks(linkList);
        return linkList;
    }

    public void saveLinks(Collection<Link> linkList){
        String text = "";
        for (Link link: linkList){
            String text2 = (link.getName()!=null? link.getName(): "SberJazz ") +" ["+link.getUrl()+"]\n";
            text+=text2;
        }
        saveText(text);
    }

    public void addLink(Link link){
        ArrayList<Link> linkList = readLinks();
        TreeSet<Link> linkSet = new TreeSet<>(linkList);
        linkSet.add(link);
        saveLinks(linkSet);
    }

    public void removeLink(String name){
        ArrayList<Link> linkList = readLinks();
        Iterator<Link> linkIterator = linkList.iterator();
        while(linkIterator.hasNext()) {
            Link link = linkIterator.next();
            if (link.getName().equals(name)) {
                linkIterator.remove();
            }
        }
        saveLinks(linkList);
    }

    public void addLink(String call, String name){
        addLink(new Link(name, call));
    }
}
