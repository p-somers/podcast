package somers.pw.podcastmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by petersomers on 12/27/13.
 */
public class Database extends SQLiteOpenHelper {
    private static final String TAG = PodcastActivity.class.getName();

    public static final String TABLE_SUBSCRIPTIONS = "subscriptions";
    public static final String COLUMN_COLLECTION_ID = "collection_id";
    public static final String COLUMN_TRACK_ID = "track_id";
    public static final String COLUMN_TRACK_COUNT = "track_count";
    public static final String COLUMN_GENRE_IDS = "genre_ids";
    public static final String COLUMN_COLLECTION_PRICE = "collection_price";
    public static final String COLUMN_TRACK_PRICE = "track_price";
    public static final String COLUMN_WRAPPER_TYPE = "wrapper_type";
    public static final String COLUMN_ARTIST_NAME = "artist_name";
    public static final String COLUMN_COLLECTION_NAME = "collection_name";
    public static final String COLUMN_COLLECTION_CENSORED_NAME = "collection_censored_name";
    public static final String COLUMN_TRACK_NAME = "track_name";
    public static final String COLUMN_TRACK_CENSORED_NAME = "track_censored_name";
    public static final String COLUMN_ARTWORK_30_PATH = "artwork_30_path";
    public static final String COLUMN_ARTWORK_60_PATH = "artwork_60_path";
    public static final String COLUMN_ARTWORK_100_PATH = "artwork_100_path";
    public static final String COLUMN_ARTWORK_600_PATH = "artwork_600_path";
    public static final String COLUMN_ARTWORK_30_URL = "artwork_30_url";
    public static final String COLUMN_ARTWORK_60_URL = "artwork_60_url";
    public static final String COLUMN_ARTWORK_100_URL = "artwork_100_url";
    public static final String COLUMN_ARTWORK_600_URL = "artwork_600_url";
    public static final String COLUMN_ARTWORK_FILE_EXTENSION = "artwork_file_extension";
    public static final String COLUMN_PRIMARY_GENRE_NAME = "primary_genre_name";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_GENRES = "genres";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_COLLECTION_VIEW_URL = "collection_view_url";
    public static final String COLUMN_FEED_URL = "feed_url";
    public static final String COLUMN_TRACKVIEW_URL = "trackview_url";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_COLLECTION_IS_EXPLICIT = "collection_is_explicit";
    public static final String COLUMN_TRACK_IS_EXPLICIT = "track_is_explicit";

    public static final String TABLE_EPISODES = "episodes";
    public static final String COLUMN_ID        = "_id";
    public static final String COLUMN_PARENT_ID = "parent_id";
    public static final String COLUMN_TITLE     = "title";
    public static final String COLUMN_COPYRIGHT = "copyright";
    public static final String COLUMN_AUTHOR    = "author";
    public static final String COLUMN_MEDIATYPE = "mediaType";
    public static final String COLUMN_FILETYPE  = "fileType";
    public static final String COLUMN_SUBTITLE  = "subtitle";
    public static final String COLUMN_SUMMARY   = "summary";
    public static final String COLUMN_EXPLICIT  = "explicit";
    public static final String COLUMN_BLOCKED   = "blocked";
    public static final String COLUMN_ISCLOSEDCAPTIONED = "isClosedCaptioned";
    public static final String COLUMN_HOURS   = "hours";
    public static final String COLUMN_MINUTES = "minutes";
    public static final String COLUMN_SECONDS = "seconds";
    public static final String COLUMN_LENGTH  = "length";
    public static final String COLUMN_GUID    = "guid";
    public static final String COLUMN_LINK    = "link";
    public static final String COLUMN_URL     = "url";
    public static final String COLUMN_PUBDATE = "pubDate";
    public static final String COLUMN_LOCAL_FILE = "localFile";
    public static final String COLUMN_PLAYBACK_POSITION = "playback_position";
    public static final String COLUMN_EPISODE_FINISHED = "episode_finished";

    private static final String DATABASE_NAME = "subscriptions.db";
    private static final int DATABASE_VERSION = 1;

    /*
    private boolean collectionIsExplicit;
    private boolean trackIsExplicit;
     */
    private static Context context;

    private static final String SUBSCRIPTION_DATABASE_CREATE = "create table "
            + TABLE_SUBSCRIPTIONS + "("
            + COLUMN_ID + " integer primary key, "
            //+ COLUMN_COLLECTION_ID + " integer, "
            + COLUMN_TRACK_ID + " integer, "
            + COLUMN_TRACK_COUNT + " integer, "
            + COLUMN_GENRE_IDS + " text, "
            + COLUMN_COLLECTION_PRICE + " real, "
            + COLUMN_TRACK_PRICE + " real, "
            + COLUMN_WRAPPER_TYPE + " text, "
            + COLUMN_ARTIST_NAME + " text, "
            + COLUMN_COLLECTION_NAME + " text, "
            + COLUMN_COLLECTION_CENSORED_NAME + " text, "
            + COLUMN_TRACK_NAME + " text, "
            + COLUMN_TRACK_CENSORED_NAME + " text, "
            + COLUMN_ARTWORK_30_PATH + " text, "
            + COLUMN_ARTWORK_60_PATH + " text, "
            + COLUMN_ARTWORK_100_PATH + " text, "
            + COLUMN_ARTWORK_600_PATH + " text, "
            + COLUMN_ARTWORK_30_URL + " text, "
            + COLUMN_ARTWORK_60_URL + " text, "
            + COLUMN_ARTWORK_100_URL + " text, "
            + COLUMN_ARTWORK_600_URL + " text, "
            + COLUMN_ARTWORK_FILE_EXTENSION + " text, "
            + COLUMN_PRIMARY_GENRE_NAME + " text, "
            + COLUMN_RELEASE_DATE + " text, "
            + COLUMN_GENRES + " text, "
            + COLUMN_COUNTRY + " text, "
            + COLUMN_COLLECTION_VIEW_URL + " text, "
            + COLUMN_FEED_URL + " text, "
            + COLUMN_TRACKVIEW_URL + " text, "
            + COLUMN_CURRENCY + " text, "
            + COLUMN_COLLECTION_IS_EXPLICIT + " boolean, "
            + COLUMN_TRACK_IS_EXPLICIT + " boolean"
            + ");";

    // Database creation sql statement
    private static final String EPISODE_DATABASE_CREATE = "create table "
            + TABLE_EPISODES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PARENT_ID + " integer, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_COPYRIGHT + " text, "
            + COLUMN_AUTHOR + " text, "
            + COLUMN_MEDIATYPE + " text, "
            + COLUMN_FILETYPE + " text, "
            + COLUMN_SUBTITLE + " text, "
            + COLUMN_SUMMARY + " text, "
            + COLUMN_EXPLICIT + " text, "
            + COLUMN_BLOCKED + " boolean, "
            + COLUMN_ISCLOSEDCAPTIONED + " numeric, "
            + COLUMN_HOURS + " integer, "
            + COLUMN_MINUTES + " integer, "
            + COLUMN_SECONDS + " integer, "
            + COLUMN_LENGTH + " real, "
            + COLUMN_GUID + " text, "
            + COLUMN_LINK + " text, "
            + COLUMN_URL + " text, "
            + COLUMN_PUBDATE + " text, "
            + COLUMN_LOCAL_FILE + " text, "
            + COLUMN_PLAYBACK_POSITION + " integer default 0, "
            + COLUMN_EPISODE_FINISHED + " boolean default 0"
            + ");";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(EPISODE_DATABASE_CREATE);
        database.execSQL(SUBSCRIPTION_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Database.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EPISODES);
        onCreate(db);
    }
    public long subscribeTo(Podcast podcast) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID,podcast.getCollectionId());
        values.put(COLUMN_TRACK_ID,podcast.getTrackId());
        values.put(COLUMN_TRACK_COUNT,podcast.getTrackCount());
        String genre_ids_string = "";
        int genre_ids[] = podcast.getGenreIds();
        for(int i = 0; i < genre_ids.length; i++){
            if(i > 0)
                genre_ids_string += ",";
            genre_ids_string += genre_ids[i];
        }
        values.put(COLUMN_GENRE_IDS,genre_ids_string);
        values.put(COLUMN_COLLECTION_PRICE,podcast.getCollectionPrice());
        values.put(COLUMN_COLLECTION_PRICE,podcast.getCollectionPrice());
        values.put(COLUMN_TRACK_PRICE,podcast.getTrackPrice());
        values.put(COLUMN_WRAPPER_TYPE,podcast.getWrapperType());
        values.put(COLUMN_ARTIST_NAME,podcast.getArtistName());
        values.put(COLUMN_COLLECTION_NAME,podcast.getCollectionName());
        values.put(COLUMN_COLLECTION_CENSORED_NAME,podcast.getCollectionCensoredName());
        values.put(COLUMN_TRACK_NAME,podcast.getTrackName());
        values.put(COLUMN_TRACK_CENSORED_NAME,podcast.getTrackCensoredName());
        values.put(COLUMN_ARTWORK_30_PATH,podcast.getArtworkPath("30"));
        values.put(COLUMN_ARTWORK_60_PATH,podcast.getArtworkPath("60"));
        values.put(COLUMN_ARTWORK_100_PATH,podcast.getArtworkPath("100"));
        values.put(COLUMN_ARTWORK_600_PATH,podcast.getArtworkPath("600"));
        values.put(COLUMN_ARTWORK_30_URL,podcast.getArtworkUrl("30").toString());
        values.put(COLUMN_ARTWORK_60_URL,podcast.getArtworkUrl("60").toString());
        values.put(COLUMN_ARTWORK_100_URL,podcast.getArtworkUrl("100").toString());
        values.put(COLUMN_ARTWORK_600_URL,podcast.getArtworkUrl("600").toString());
        values.put(COLUMN_PRIMARY_GENRE_NAME,podcast.getPrimaryGenreName());
        values.put(COLUMN_RELEASE_DATE,podcast.getReleaseDate());
        String genres_string = "";
        String genres[] = podcast.getGenres();
        for(int i = 0; i < genres.length; i++){
            if(i > 0)
                genres_string += ",";
            genres_string += genres[i];
        }
        values.put(COLUMN_GENRES,genres_string);
        values.put(COLUMN_COUNTRY,podcast.getCountry());
        values.put(COLUMN_COLLECTION_VIEW_URL,podcast.getCollectionViewUrl().toString());
        values.put(COLUMN_FEED_URL,podcast.getFeedUrl().toString());
        values.put(COLUMN_TRACKVIEW_URL,podcast.getTrackViewUrl().toString());
        values.put(COLUMN_CURRENCY,podcast.getCurrency().toString());
        values.put(COLUMN_COLLECTION_IS_EXPLICIT,podcast.collectionIsExplicit());
        values.put(COLUMN_TRACK_IS_EXPLICIT,podcast.trackIsExplicit());

        // insert row
        long podcast_id = db.insert(TABLE_SUBSCRIPTIONS, null, values);
        return podcast_id;
    }
    public int unsubscribeFrom( long id ){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBSCRIPTIONS,COLUMN_ID+" = "+id,null);
        return db.delete(TABLE_EPISODES,COLUMN_PARENT_ID+" = "+id,null);
    }

    public boolean isSubscribedTo(Podcast podcast){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_SUBSCRIPTIONS, null,
                COLUMN_ID + "=" + podcast.getCollectionId(), null, null, null, null);
        boolean tf = cursor.getCount() > 0;
        cursor.close();
        return tf;
    }

    public boolean episodeIsDownloaded(Episode episode, Podcast parent){
        return episodeIsDownloaded(episode.getTitle(),parent.getCollectionId());
    }

    public boolean episodeIsDownloaded(String title, long parent_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_EPISODES,null,COLUMN_TITLE+"=\""+title+"\" and "+COLUMN_PARENT_ID+"="+parent_id,
                null,null,null,null);
        boolean tf = cursor.getCount() > 0;
        cursor.close();
        return tf;
    }

    public Podcast[] getSubscriptions(){
        Podcast podcasts[] = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SUBSCRIPTIONS,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            podcasts = new Podcast[cursor.getCount()];
            for(int i = 0; i < cursor.getCount(); i++){
                podcasts[i] = podcastFromCursor(cursor);
                cursor.moveToNext();
            }
        }
        return podcasts;
    }

    public Podcast podcastFromId(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SUBSCRIPTIONS,null,COLUMN_ID+"="+id,null,null,null,null);
        if(!cursor.moveToFirst() || cursor.getCount() == 0)
            return null;
        return podcastFromCursor(cursor);
    }

    private Podcast podcastFromCursor(Cursor cursor){
        int cid = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        int tid = cursor.getInt(cursor.getColumnIndex(COLUMN_TRACK_ID));
        int tcount = cursor.getInt(cursor.getColumnIndex(COLUMN_TRACK_COUNT));
        float cprice = cursor.getFloat(cursor.getColumnIndex(COLUMN_COLLECTION_PRICE));
        float tprice = cursor.getFloat(cursor.getColumnIndex(COLUMN_TRACK_PRICE));
        String wt = cursor.getString(cursor.getColumnIndex(COLUMN_WRAPPER_TYPE));
        String aname = cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST_NAME));
        String cname = cursor.getString(cursor.getColumnIndex(COLUMN_COLLECTION_NAME));
        String tname = cursor.getString(cursor.getColumnIndex(COLUMN_TRACK_NAME));
        String cname_cens = cursor.getString(cursor.getColumnIndex(COLUMN_COLLECTION_CENSORED_NAME));
        String tname_cens = cursor.getString(cursor.getColumnIndex(COLUMN_TRACK_CENSORED_NAME));
        String cvu  = cursor.getString(cursor.getColumnIndex(COLUMN_COLLECTION_VIEW_URL));
        String furl = cursor.getString(cursor.getColumnIndex(COLUMN_FEED_URL));
        String turl = cursor.getString(cursor.getColumnIndex(COLUMN_TRACKVIEW_URL));
        String a30file  = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_30_PATH ));
        String a60file  = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_60_PATH ));
        String a100file = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_100_PATH));
        String a600file = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_600_PATH));
        String a30url   = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_30_URL ));
        String a60url   = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_60_URL ));
        String a100url  = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_100_URL));
        String a600url  = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_600_URL));
        String rdate = cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE));
        String country = cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY));
        String curr = cursor.getString(cursor.getColumnIndex(COLUMN_CURRENCY));
        String pgenrename = cursor.getString(cursor.getColumnIndex(COLUMN_PRIMARY_GENRE_NAME));
        boolean c_exp = cursor.getInt(cursor.getColumnIndex(COLUMN_COLLECTION_IS_EXPLICIT))>0;
        boolean t_exp = cursor.getInt(cursor.getColumnIndex(COLUMN_TRACK_IS_EXPLICIT))>0;
        String genre_ids_string[] = null;
        try{
            genre_ids_string = cursor.getString(cursor.getColumnIndex(COLUMN_GENRE_IDS)).split(",");
        } catch(NullPointerException ex){
            Log.e("test","",ex);
        }
        int[] genre_ids = new int[genre_ids_string.length];
        for(int i = 0; i < genre_ids_string.length; i++)
            try{
                genre_ids[i] = Integer.parseInt(genre_ids_string[i]);
            }catch(NumberFormatException ex){
                Log.e("test","",ex);
            }
        String genres[] = cursor.getString(cursor.getColumnIndex(COLUMN_GENRES)).split(",");
        Podcast podcast = new Podcast(cid, tid, tcount, genre_ids, cprice, tprice, wt, aname,cname,
                cname_cens, tname, tname_cens, a30url, a60url, a100url, a600url, pgenrename, rdate,
                country, genres, cvu, furl,turl, curr, c_exp,t_exp, context);
        podcast.setArtworkFile(30, a30file );
        podcast.setArtworkFile(60, a60file );
        podcast.setArtworkFile(100,a100file);
        podcast.setArtworkFile(600,a600file);
        return podcast;
    }

    public long addEpisode(Episode episode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(episodeIsDownloaded(episode,episode.getParent())){
            Log.e(TAG,"Episode already downloaded: "+episode.getTitle());
            return -1;
        }

        values.put(COLUMN_PARENT_ID,episode.getParent().getCollectionId());
        values.put(COLUMN_TITLE,episode.getTitle());
        values.put(COLUMN_COPYRIGHT,episode.getCopyright());
        values.put(COLUMN_AUTHOR,episode.getAuthor());
        values.put(COLUMN_MEDIATYPE,episode.getMediaType());
        values.put(COLUMN_SUBTITLE,episode.getSubtitle());
        values.put(COLUMN_SUMMARY,episode.getSummary());
        values.put(COLUMN_EXPLICIT,episode.getExplicitness());
        values.put(COLUMN_BLOCKED,episode.isBlocked());
        values.put(COLUMN_ISCLOSEDCAPTIONED,episode.isClosedCaptioned());
        values.put(COLUMN_HOURS,episode.getHours());
        values.put(COLUMN_MINUTES,episode.getMinutes());
        values.put(COLUMN_SECONDS,episode.getSeconds());
        values.put(COLUMN_LENGTH,episode.getLength());
        try{
            values.put(COLUMN_GUID,episode.getGuid().toString());
        }catch (Exception ex){
            values.put(COLUMN_GUID,"");
        }
        try{
            values.put(COLUMN_LINK,episode.getLink().toString());
        }catch(Exception ex){
        }
        try{
            values.put(COLUMN_URL,episode.getUrl().toString());
        }catch(Exception ex){
            values.put(COLUMN_URL,"");
        }
        try{
            values.put(COLUMN_PUBDATE,episode.getPubDate().toString());
        }catch(Exception ex){
            values.put(COLUMN_PUBDATE,"");
        }
        try{
            values.put(COLUMN_LOCAL_FILE,episode.getLocalFile().getAbsolutePath());
        }catch(Exception ex){
            values.put(COLUMN_LOCAL_FILE,"");
        }
        // insert row
        long episode_id = db.insert(TABLE_EPISODES, null, values);
        return episode_id;
    }

    public Episode[] getEpisodes(Podcast podcast){
        Episode episodes[] = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EPISODES,null,COLUMN_PARENT_ID+"="+podcast.getCollectionId(),null,null,null,null);
        if(cursor.moveToFirst()){
            episodes = new Episode[cursor.getCount()];
            for(int i = 0; i < cursor.getCount(); i++){
                episodes[i] = episodeFromCursor(cursor, podcast);
                episodes[i].setParent(podcast);
                cursor.moveToNext();
            }
        }
        return episodes;
    }

    public Episode getEpisodeById(long id){
        Episode episode = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EPISODES,null,COLUMN_ID+"="+id,null,null,null,null);
        if(cursor.moveToFirst()){
            episode = episodeFromCursor(cursor);
            int parent_id = cursor.getInt(cursor.getColumnIndex(COLUMN_PARENT_ID));
            Podcast parent = podcastFromId(parent_id);
            episode.setParent(parent);
        }
        return episode;
    }

    public int setEpisodePlaybackPosition(long id, int pos){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYBACK_POSITION, pos);
        return db.update(TABLE_EPISODES,values,COLUMN_ID+" = "+id,null);
    }
    public int getEpisodePlaybackPosition(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        String columns[] = {COLUMN_PLAYBACK_POSITION};
        Cursor cursor = db.query(TABLE_EPISODES,columns,COLUMN_ID+"="+id,null,null,null,null);
        if(cursor.moveToFirst()){
            return cursor.getInt(cursor.getColumnIndex(COLUMN_PLAYBACK_POSITION));
        }
        return -1;
    }

    public boolean markEpisodeFinished(Episode episode){
        long id = episode.getId();
        if(id < 1){
            //episode has not been downloaded
            id = addEpisode(episode);
        }else {
            episode.deleteLocalFile();
        }
        return markEpisodeFinished(id);
    }

    public boolean markEpisodeFinished(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EPISODE_FINISHED, true);
        values.put(COLUMN_LOCAL_FILE, "");
        assert db != null;
        if (db.update(TABLE_EPISODES, values, COLUMN_ID + " = " + id, null) == 0) {
            Log.e(TAG, "No rows updated when trying to mark #" + id + " as finished");
            return false;
        }
        return true;
    }

    private Episode episodeFromCursor(Cursor cursor, Podcast parent){
        Episode episode = episodeFromCursor(cursor);
        episode.setParent(parent);
        return episode;
    }
    private Episode episodeFromCursor(Cursor cursor){
        Episode episode = new Episode();
        episode.setField("database_id", cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        episode.setField("title",cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        episode.setField("copyright",cursor.getString(cursor.getColumnIndex(COLUMN_COPYRIGHT)));
        episode.setField("author",cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)));
        episode.setField("mediaType",cursor.getString(cursor.getColumnIndex(COLUMN_MEDIATYPE)));
        episode.setField("fileType",cursor.getString(cursor.getColumnIndex(COLUMN_FILETYPE)));
        episode.setField("subtitle",cursor.getString(cursor.getColumnIndex(COLUMN_SUBTITLE)));
        episode.setField("summary",cursor.getString(cursor.getColumnIndex(COLUMN_SUMMARY)));
        episode.setField("explicit",cursor.getString(cursor.getColumnIndex(COLUMN_EXPLICIT)));
        episode.setField("blocked",cursor.getInt(cursor.getColumnIndex(COLUMN_BLOCKED))>0);
        episode.setField("isClosedCaptioned",cursor.getInt(cursor.getColumnIndex(COLUMN_ISCLOSEDCAPTIONED))>0);
        episode.setField("finished",cursor.getInt(cursor.getColumnIndex(COLUMN_EPISODE_FINISHED))>0);
        episode.setField("hours",cursor.getInt(cursor.getColumnIndex(COLUMN_HOURS)));
        episode.setField("minutes",cursor.getInt(cursor.getColumnIndex(COLUMN_MINUTES)));
        episode.setField("seconds",cursor.getInt(cursor.getColumnIndex(COLUMN_SECONDS)));
        episode.setField("length",cursor.getInt(cursor.getColumnIndex(COLUMN_LENGTH)));
        episode.setGuid(cursor.getString(cursor.getColumnIndex(COLUMN_GUID)));
        episode.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_LINK)));
        episode.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
        episode.setPubDate(cursor.getString(cursor.getColumnIndex(COLUMN_PUBDATE)));
        episode.setLocalFile(cursor.getString(cursor.getColumnIndex(COLUMN_LOCAL_FILE)));
        episode.setPlaybackPosition(cursor.getInt(cursor.getColumnIndex(COLUMN_PLAYBACK_POSITION)));
        return episode;
    }

    //From: http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null && db.isOpen())
            db.close();
    }

    public void logPodcastInfo(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SUBSCRIPTIONS,null,COLUMN_ID+"="+id,null,null,null,null);
        if(cursor.getCount() == 0){
            Log.d(TAG,"No results");
            return;
        }
        String s = "\n";
        if(cursor.moveToFirst()){
            for(int i = 0; i < cursor.getColumnCount(); i++){
                String column = cursor.getColumnName(i);
                s += "\t"+column+": "+cursor.getString(i)+"\n";
            }
        }
        Log.d(TAG,s);
    }

    public void logPodcastEpisodes(Podcast podcast){
        Episode[] episodes = getEpisodes(podcast);
        StringBuilder sb = new StringBuilder();
        sb.append("Number of results: ").append(episodes.length).append("\n");
        for(Episode episode: episodes){
            sb.append(episode.getTitle()).append("\n");
            sb.append(episode.isFinished()).append("\n");
            sb.append("-------------------------------------------------------------\n");
        }
        Log.d(TAG,sb.toString());
    }
}
