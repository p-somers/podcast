package com.example.android.podcastapp.RSS;

import android.os.AsyncTask;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.example.android.podcastapp.RSS.RSSHandler;

/**
 * Created by petersomers on 9/2/13.
 */
public class XMLParser extends AsyncTask<URL, Void, Void> {

    @Override
    protected Void doInBackground(URL... urls) {
        try {
            URL url = urls[0];
            SAXParserFactory factory =SAXParserFactory.newInstance();
            SAXParser parser=factory.newSAXParser();
            XMLReader xmlreader=parser.getXMLReader();
            RSSHandler rh = new RSSHandler();
            xmlreader.setContentHandler(rh);
            xmlreader.parse(new InputSource(url.openStream()));
        } catch (Exception e) {
            Log.e("test","",e);
        }
        return null;
    }
}
