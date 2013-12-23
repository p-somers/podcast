package com.example.android.podcastapp.RSS;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by petersomers on 12/22/13.
 */
public class RSSHandler extends DefaultHandler {
    StringBuffer chars = new StringBuffer();
    @Override
    public void startElement(String uri, String lName, String qName, Attributes a){

        chars.setLength(0);
        if(a.getLength() > 0){
            String s ="Attributes for "+lName+":\n";
            for(int i = 0; i < a.getLength(); i++){
                s+=a.getLocalName(i)+": "+a.getValue(i)+"\n";
            }
            //Log.d("test",s);
        }
    }
    @Override
    public void endElement(String uri, String lName, String qName){
        //Log.d("test",lName+": "+chars.toString());
    }
    @Override
    public void characters(char ch[], int start, int length) {
        chars.append(new String(ch, start, length));
    }
}
