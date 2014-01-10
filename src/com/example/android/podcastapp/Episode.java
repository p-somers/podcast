package com.example.android.podcastapp;

import android.util.Log;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by petersomers on 12/22/13.
 */
public class Episode {
    private String title;
    private String copyright;
    private String author;
    private String mediaType;
    private String fileType;
    private String subtitle;//short description
    private String summary;//longer description
    private String explicit;
    private boolean blocked;
    private boolean isClosedCaptioned;
    private int hours, minutes, seconds;
    private float length;//in bytes
    private URL guid;
    private URL link;//website link
    private URL url;
    private Date pubDate;
    public Episode(){
        title = "";
        copyright = "";
        author = "";
        mediaType = "";
        fileType = "";
        subtitle = "";
        summary = "";
        explicit = "";
        blocked = false;
        isClosedCaptioned = false;
        hours = minutes = seconds = 0;
        length = 0;
        guid = null;
        link = null;
        url = null;
        pubDate = null;
    }

    public void setField(String declaredField, Object val){
        //Log.d("test","trying to set "+declaredField+" to "+val.toString());
        try {
            Field field = this.getClass().getDeclaredField(declaredField);
            field.set(this,val);
        } catch (NoSuchFieldException e) {
            Log.e("test","",e);
        } catch (IllegalAccessException e) {
            Log.e("test","",e);
        }
    }

    public void setType(String type){
        String vals[] = type.split("/");
        if(vals.length > 1){
            mediaType = vals[0];
            fileType  = vals[1];
        }
    }

    public void setDuration(String duration){
        if(duration.length() == 0)
            return;
        String vals[] = duration.split(":");
        switch(vals.length){
            case 0:
            case 1:
                try{
                    seconds = Integer.parseInt(duration);
                } catch(NumberFormatException e){
                    Log.e("test","Error parsing duration");
                } break;
            case 2:
                try{
                    seconds = Integer.parseInt(vals[1]);
                } catch(NumberFormatException e){
                    Log.e("test","Error parsing seconds of duration");
                }
                try{
                    minutes = Integer.parseInt(vals[0]);
                } catch(NumberFormatException e){
                    Log.e("test","error parsing minutes of duration");
                } break;
            case 3:
                try{
                    seconds = Integer.parseInt(vals[2]);
                } catch(NumberFormatException e){
                    Log.e("test","Error parsing seconds of duration");
                }
                try{
                    minutes = Integer.parseInt(vals[1]);
                } catch(NumberFormatException e){
                    Log.e("test","error parsing minutes of duration");
                }
                try{
                    hours = Integer.parseInt(vals[2]);
                } catch(NumberFormatException e){
                    Log.e("test","error parsing hours of duration");
                }
        }
    }
    public String getDuration(){
        String s = "";
        if(hours < 10)
            s+="0";
        s+=hours+":";
        if(minutes < 10)
            s+="0";
        s+=minutes+":";
        if(seconds < 10)
            s+="0";
        s+=seconds;
        return s;
    }

    public void setLink(URL link){
        this.link = link;
    }
    public void setLink(String link){
        try{
            if(link.length() > 0)
            this.link = new URL(link);
        } catch (MalformedURLException e) {
            Log.e("test","",e);
        }
    }
    public void setGuid(URL guid){
        this.guid = guid;
    }
    public void setGuid(String guid){
        try {
            if(guid.length()>0)
                this.guid = new URL(guid);
        } catch (MalformedURLException e) {
            Log.e("test","",e);
        }
    }
    public void setUrl(URL url){
        this.url = url;
    }
    public void setUrl(String url){
        try {
            if(url.length() > 0)
                this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setLength(float length){
        this.length = length;
    }
    public void setPubDate(String pubDate) {
        String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";// RFC 2822 format
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            this.pubDate = format.parse(pubDate);
        } catch (ParseException e) {
            Log.e("test","",e);
        }
    }

    public String getTitle(){
        return title;
    }
    public String getCopyright(){
        return copyright;
    }
    public Date getPubDate(){
        return pubDate;
    }
    public String getAuthor(){
        return author;
    }
    public String getMediaType(){
        return mediaType;
    }
    public String getFileType(){
        return fileType;
    }
    public String getSubtitle(){
        return subtitle;
    }
    public String getSummary(){
        return summary;
    }
    public boolean isBlocked(){
        return blocked;
    }
    public boolean isClosedCaptioned(){
        return isClosedCaptioned;
    }
    public float getLength(){
        return length;
    }
    public URL getLink(){
        return link;
    }
    public URL getMediaURL(){
        if(guid != null)
            return guid;
        return url;
    }
    @Override
    public String toString(){
        return allFieldsToString();
    }

    /**
     * For debugging
     */
    public String allFieldsToString(){
        Field[] fields = Episode.class.getDeclaredFields();
        String s = "";
        for(Field field: fields){
            try{
                s+=field.getName()+": "+field.get(this)+", \n";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return s;
    }
}
