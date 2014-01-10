package com.example.android.podcastapp;

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

    private static final String DATABASE_NAME = "subscriptions.db";
    private static final int DATABASE_VERSION = 1;

    /*
    private boolean collectionIsExplicit;
    private boolean trackIsExplicit;
     */
    private static Context context;

    private static final String SUBSCRIPTION_DATABASE_CREATE = "create table "
            + TABLE_SUBSCRIPTIONS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_COLLECTION_ID + " integer, "
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
            + COLUMN_PUBDATE + " text"
            + ");";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
        values.put(COLUMN_COLLECTION_ID,podcast.getCollectionId());
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

    public boolean isSubscribedTo(Podcast podcast){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_SUBSCRIPTIONS, null,
                COLUMN_COLLECTION_ID + "=" + podcast.getCollectionId(), null, null, null, null);
        boolean tf = cursor.getCount() > 0;
        cursor.close();
        return tf;
    }

    public Podcast[] getSubscriptions(){
        Podcast podcasts[] = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_SUBSCRIPTIONS,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            podcasts = new Podcast[cursor.getCount()];
            for(int i = 0; i < cursor.getCount(); i++){
                podcasts[i] = fromCursor(cursor);
                cursor.moveToNext();
            }
        }
        return podcasts;
    }

    private Podcast fromCursor(Cursor cursor){
        int cid = cursor.getInt(cursor.getColumnIndex(COLUMN_COLLECTION_ID));
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
        String a30  = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_30_URL));
        String a60  = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_60_URL));
        String a100  = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_100_URL));
        String a600  = cursor.getString(cursor.getColumnIndex(COLUMN_ARTWORK_600_URL));
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
        return new Podcast(cid, tid, tcount, genre_ids, cprice, tprice, wt, aname,cname, cname_cens,
                tname, tname_cens, a30, a60, a100, a600, pgenrename, rdate, country, genres, cvu,
                furl,turl, curr, c_exp,t_exp, context);
    }

    //From: http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null && db.isOpen())
            db.close();
    }
}
