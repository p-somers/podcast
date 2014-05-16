package somers.pw.podcastmanager;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by petersomers on 12/22/13.
 */
public class Episode implements Parcelable {
    private static final String TAG = PodcastActivity.class.getName();

    private Context context;
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
    private boolean finished;
    private int hours, minutes, seconds,
            playback_position;//in milliseconds
    private long database_id;
    private float length;//in bytes
    private URL guid;
    private URL link;//website link
    private URL url;
    private Date pubDate;
    private File localFile;
    private Podcast parent;
    private RetrieveMediaTask task;


    private boolean downloading;

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
        finished = false;
        hours = minutes = seconds = playback_position = 0;
        database_id = 0;
        length = 0;
        guid = null;
        link = null;
        url = null;
        pubDate = null;
        localFile = null;
        parent = null;
        downloading = false;
    }

    public Episode(Parcel parcel){
        title = parcel.readString();
        copyright = parcel.readString();
        author = parcel.readString();
        mediaType = parcel.readString();
        fileType = parcel.readString();
        subtitle = parcel.readString();
        summary = parcel.readString();
        explicit = parcel.readString();
        blocked = (parcel.readByte() != 0);
        isClosedCaptioned = (parcel.readByte() != 0);
        finished = (parcel.readByte() != 0);
        hours = parcel.readInt();
        minutes = parcel.readInt();
        seconds = parcel.readInt();
        playback_position = parcel.readInt();
        database_id = parcel.readLong();
        length = parcel.readFloat();
        try{
            guid = new URL(parcel.readString());
        }catch(Exception ex){}
        try{
            link = new URL(parcel.readString());
        }catch(Exception ex){}
        try{
            url = new URL(parcel.readString());
        }catch(Exception ex){}
        pubDate = new Date();
        pubDate.setTime(parcel.readLong());
        String filename = parcel.readString();
        if(!filename.equals(""))
            localFile = new File(filename);
        try{
            parent = parcel.readParcelable(Podcast.class.getClassLoader());
        } catch(Exception e){
            Log.e(TAG,"",e);
        }
    }

    /**
     * This method copies pertinent local info (localFile, id) from
     * an episode pulled from the database and adds it to this.
     * This is done because there's no way to only fetch undownloaded
     * podcasts from the internet, so we copy important info from the
     * database.
     * @param other
     */
    public void copyLocalInfo(Episode other){
        this.localFile = other.getLocalFile();
        if(isDownloaded()){
            //Log.d(TAG,"-\nDownloaded episode:\n"+this.title+"\n"+this.localFile);
        }
        this.database_id = other.getId();
        this.playback_position = other.getPlaybackPosition();
        this.parent = other.getParent();
        this.finished = other.isFinished();
        if(this.finished){
            //Log.d(TAG,"Finished episode: "+this.title);
        }
    }

    public void setField(String declaredField, Object val){
        try {
            /*
                Note: casting to Object because of a bug in Android Studio/Intellij IDEA
             */
            Field field = ((Object)this).getClass().getDeclaredField(declaredField);
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
                }
                minutes = 0;
                hours = 0;
                break;
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
                }
                hours = 0;
                break;
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
                    hours = Integer.parseInt(vals[0]);
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
    public int getDurationInMilliseconds(){
        return (hours * 60 * 60 * 1000) +
               (minutes * 60 * 1000)+
               (seconds * 1000);
    }

    public void setLink(URL link){
        this.link = link;
    }
    public void setLink(String link){
        try{
            if(link != null && link.length() > 0)
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
    public void setLocalFile(String filename){
        if(filename != null)
            localFile = new File(filename);
    }
    public void setLocalFile(File file){
        localFile = file;
    }
    public void setPlaybackPosition(int position){
        this.playback_position = position;
    }
    public void setId(int id){
        database_id = id;
    }

    public void setLength(float length){
        this.length = length;
    }
    public void setPubDate(String pubDate) {
        /**
         * Okay this is stupid but I'm running across feeds that don't
         * conform to the specification here:
         * http://www.apple.com/itunes/podcasts/specs.html
         * So, I'm creating an array of any formats that I come across
         * that don't work.
         */
        String patterns[] = {// RFC 2822 format
                "EEE, dd MMM yyyy HH:mm:ss Z",
                "EEE MMM dd HH:mm:ss z yyyy"
        };
        boolean unfinished = true;
        int pattern_index=0;
        while(unfinished && pattern_index < patterns.length){
            SimpleDateFormat format = new SimpleDateFormat(patterns[pattern_index]);
            try {
                this.pubDate = format.parse(pubDate);
                unfinished = false;
            } catch (ParseException e) {
                pattern_index++;
            }
        }
    }
    public void setParent(Podcast podcast){
        this.parent = podcast;
    }

    public String getTitle(){
        return title;
    }
    public String getCopyright(){
        return copyright;
    }
    public Date getPubDate(){
        if(pubDate == null){
            pubDate = new Date();
        }
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
    public String getExplicitness() { return explicit; }
    public boolean isBlocked(){
        return blocked;
    }
    public boolean isClosedCaptioned(){
        return isClosedCaptioned;
    }
    public int getHours(){
        return hours;
    }
    public int getMinutes(){
        return minutes;
    }
    public int getSeconds(){
        return seconds;
    }
    public int getPlaybackPosition(){
        return playback_position;
    }
    public long getId(){
        return database_id;
    }
    public float getLength(){
        return length;
    }
    public URL getGuid(){
        return guid;
    }
    public URL getLink(){
        return link;
    }
    public URL getUrl(){
        return url;
    }
    public URL getMediaURL(){
        URL mediaUrl = guid;
        if(guid == null)
            return url;
        else{
            String urlString = mediaUrl.toString();
            String fileName = urlString.substring(urlString.lastIndexOf('/') + 1);
            if(fileName.equals("") || fileName.lastIndexOf('.') == -1){
                //Somebody didn't read the podcast specs right, which we need to account for.
                return url;
            }
        }
        return mediaUrl;
    }
    public File getLocalFile(){
        return localFile;
    }
    public Podcast getParent(){
        return parent;
    }

    public void deleteLocalFile(){
        if(localFile != null){
            localFile.delete();
            localFile = null;
        }
    }

    public void download(View view){
        if(!downloading){
            task = new RetrieveMediaTask(view);
            task.execute();
        }
    }
    public boolean isDownloaded(){
        return (localFile != null && localFile.exists());
    }
    public boolean isFinished(){
        return finished;
    }
    public void showProgressBar() {
        if(task != null) {
            Log.d(TAG,"task was not null");
            task.showProgressBar();
        }
    }

    /**
     * This class downloads the episode to local storage.
     * Inspired by: http://stackoverflow.com/a/3028660/1955559
     */
    private class RetrieveMediaTask extends AsyncTask<Void, Integer, Void> {
        private Context context;
        private ProgressBar progressBar;
        private TextView title;
        private WifiManager.WifiLock wifiLock;
        private View view;
        public RetrieveMediaTask(View v){
            view = v;
            context = view.getContext();
            title = (TextView)view.findViewById(R.id.list_item_title);
            progressBar = (ProgressBar)view.findViewById(R.id.episode_download_progress_bar);
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.bringToFront();
            wifiLock = ((WifiManager)context.getSystemService(Context.WIFI_SERVICE))
                        .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
            wifiLock.acquire();
        }
        public RetrieveMediaTask(){
            this.progressBar = null;
        }
        @Override
        protected void onPreExecute(){
            downloading = true;
        }
        public Void doInBackground(Void... v){
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {

                URL mediaUrl = Episode.this.getMediaURL();
                String urlString = mediaUrl.toString();
                String fileName = urlString.substring(urlString.lastIndexOf('/') + 1);
                connection = (HttpURLConnection)mediaUrl.openConnection();
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    int fileLength = connection.getContentLength();

                    File meta = new File(PodcastActivity.getAppContext().getFilesDir(),"meta");
                    meta.mkdirs();
                    File dir = new File(meta, parent.getArtistName().replace(' ', '_'));
                    dir.mkdirs();
                    localFile = new File(dir,fileName);
                    localFile.createNewFile();
                    output = new FileOutputStream(localFile);
                    input = connection.getInputStream();

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while((count = input.read(data)) != -1){
                        total += count;
                        if(fileLength > 0)
                            publishProgress((int)(total*100/fileLength));
                        output.write(data, 0, count);
                    }
                }

            } catch (IOException e) {
                Log.e(TAG,"",e);
                return null;
            } finally {
                try {
                    if(output != null)
                        output.close();
                    if(input != null)
                        input.close();
                } catch (IOException e) {
                    Log.e(TAG,"",e);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            super.onProgressUpdate(progress);
            progressBar.setProgress(progress[0]);
        }
        @Override
        protected void onPostExecute(Void v){
            try{
                progressBar.setVisibility(ProgressBar.GONE);
                int color = context.getResources().getColor(R.color.downloaded_text_color);
                title.setTextColor(color);
                Database database = new Database(context);
                Episode.this.database_id = database.addEpisode(Episode.this);
                //Log.d(TAG,"Added #"+Episode.this.database_id+" with parent id #"+parent.getCollectionId());
                //database.logPodcastEpisodes(parent);
                database.closeDB();
                downloading = false;
                wifiLock.release();
                DownloadManager.unregister_download(Episode.this);
            }catch(Exception ex){
                Log.e(TAG,"",ex);
            }
        }

        public void showProgressBar(){
            if(progressBar == null) {
                progressBar = (ProgressBar) view.findViewById(R.id.episode_download_progress_bar);
            }
            Log.d(TAG,"ProgressBar: "+progressBar.toString());
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.bringToFront();

        }
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

    public boolean equals(Object other) {
        //I don't think there's a better way... sigh.
        return this.title.equals(((Episode)other).title);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(copyright);
        parcel.writeString(author);
        parcel.writeString(mediaType);
        parcel.writeString(fileType);
        parcel.writeString(subtitle);
        parcel.writeString(summary);
        parcel.writeString(explicit);
        parcel.writeByte((byte) (blocked ? 1 : 0));
        parcel.writeByte((byte) (isClosedCaptioned ? 1 : 0));
        parcel.writeByte((byte) (finished ? 1 : 0));
        parcel.writeInt(hours);
        parcel.writeInt(minutes);
        parcel.writeInt(seconds);
        parcel.writeInt(playback_position);
        parcel.writeLong(database_id);
        parcel.writeFloat(length);
        if(guid != null)
            parcel.writeString(guid.toString());
        else
            parcel.writeString("");
        if(link != null)
            parcel.writeString(link.toString());
        else
            parcel.writeString("");
        if(url != null)
            parcel.writeString(url.toString());
        else
            parcel.writeString("");
        parcel.writeLong(pubDate.getTime());
        if(localFile == null)
            parcel.writeString("");
        else
            parcel.writeString(localFile.getAbsolutePath());
        parcel.writeParcelable(parent,flags);
    }
    public static final Parcelable.Creator<Episode> CREATOR
            = new Parcelable.Creator<Episode>() {
        public Episode createFromParcel(Parcel parcel) {
            return new Episode(parcel);
        }

        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };
}
