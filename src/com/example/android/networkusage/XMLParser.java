package com.example.android.networkusage;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by petersomers on 9/2/13.
 */
public class XMLParser extends AsyncTask<String, Void, Void> {

    private Exception exception;

    @Override
    protected Void doInBackground(String... urls) {
        try {
            /*
            URL url= new URL(urls[0]);
            SAXParserFactory factory =SAXParserFactory.newInstance();
            SAXParser parser=factory.newSAXParser();
            XMLReader xmlreader=parser.getXMLReader();
            RssHandler theRSSHandler=new RssHandler();
            xmlreader.setContentHandler(theRSSHandler);
            InputSource is=new InputSource(url.openStream());
            xmlreader.parse(is);
            return theRSSHandler.getFeed();
            */
            URL url = new URL(urls[0]);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("item");
            //Log.d("test", "here");
            for (int i = 0; i < nodeList.getLength(); i++) {
                //Log.d("test","Nodelist: "+nodeList.item(i).toString());
            }
        } catch (Exception e) {
            this.exception = e;
            Log.e("test","yo",e);
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
