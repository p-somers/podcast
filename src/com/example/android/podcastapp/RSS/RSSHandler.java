package com.example.android.podcastapp.RSS;

import android.util.Log;

import com.example.android.podcastapp.Episode;
import com.example.android.podcastapp.PodcastViewActivity;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by petersomers on 12/22/13.
 */
public class RSSHandler extends DefaultHandler {
    private StringBuffer chars = new StringBuffer();
    private PodcastViewActivity activity;
    private Episode episode;
    private final String validStringFields[] = {
            "title","copyright","author",
            "mediaType","fileType","subtitle",
            "summary","explicit"
    };
    public RSSHandler(PodcastViewActivity activity){
        this.activity = activity;
        this.episode = new Episode();
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes a){
        if(lName.equals("item"))
            episode = new Episode();
        else if(lName.equals("enclosure")){
            setEnclosureAttributes(a);
        }
        chars.setLength(0);
        /*
        if(a.getLength() > 0){
            String s ="Attributes for "+lName+":\n";
            for(int i = 0; i < a.getLength(); i++){
                s+=a.getLocalName(i)+": "+a.getValue(i)+"\n";
            }
            Log.d("test",s);
        }
        */
    }
    @Override
    public void endElement(String uri, String lName, String qName){
        //if(inItem)
           // Log.d("test",lName+": "+chars.toString());
        if(lName.equals("item"))
            activity.addEpisode(episode);
        else if(isValidStringField(lName))
            episode.setField(lName,chars.toString());
        else if(lName.equals("block"))
            episode.setField("blocked",chars.toString().equals("yes"));
        else if(lName.equals("isClosedCaptioned"))
            episode.setField("isClosedCaptioned",chars.toString().equals("yes"));
        else if(lName.equals("duration"))
            episode.setDuration(chars.toString());
        else if(lName.equals("guid"))
            episode.setGuid(chars.toString());
        else if(lName.equals("link"))
            episode.setLink(chars.toString());
        else if(lName.equals("pubDate"))
            episode.setPubDate(chars.toString());
    }
    public boolean isValidStringField(String lName){
        for ( final String field : validStringFields)
            if ( lName == field || field != null && lName.equals( field ) )
                return true;
        return false;
    }
    private void setEnclosureAttributes(Attributes attributes){
        episode.setUrl(attributes.getValue("url"));
        episode.setType(attributes.getValue("type"));
        try{
        episode.setLength(Float.parseFloat(attributes.getValue("length")));
        }catch(NumberFormatException e){
            //ignore
        }
    }
    @Override
    public void characters(char ch[], int start, int length) {
        chars.append(new String(ch, start, length));
    }
    @Override
    public void endDocument(){
        synchronized (activity){
            activity.notifyAll();
        }
    }
}
