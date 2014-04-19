package somers.pw.podcastmanager.RSS;

import android.os.AsyncTask;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import somers.pw.podcastmanager.PodcastActivity;
import somers.pw.podcastmanager.PodcastViewActivity;

/**
 * Created by petersomers on 9/2/13.
 */
public class XMLParser extends AsyncTask<URL, Void, Void> {
    private static final String TAG = PodcastActivity.class.getName();
    private PodcastViewActivity activity;
    public XMLParser(PodcastViewActivity activity, boolean showProgressDialog){
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(URL... urls) {
        try {
            URL url = urls[0];
            SAXParserFactory factory =SAXParserFactory.newInstance();
            SAXParser parser=factory.newSAXParser();
            XMLReader xmlreader=parser.getXMLReader();
            RSSHandler rh = new RSSHandler(activity);
            rh.setMaxNumOfEpisodes(PodcastViewActivity.MAX_EPISODES_LOADED);
            xmlreader.setContentHandler(rh);
            xmlreader.parse(new InputSource(url.openStream()));
        } catch ( RSSHandler.MaxNumOfEpisodesException ex) {//ignore
        } catch (Exception e) {
            Log.e(TAG,"",e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        activity.onEpisodeParsingDone();
    }
}
