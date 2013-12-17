package com.example.android.networkusage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.Date;

/**
 * Created by petersomers on 8/29/13.
 */
/*
"genreIds":["1301", "26"], "genres":["Arts", "Podcasts"]}
 */
public class Podcast {
    private String wrapperType;
    private int collectionId;
    private int trackId;
    private String artistName;
    private String collectionName;
    private String trackName;
    private String collectionCensoredName;
    private String trackCensoredName;
    private URL collectionViewUrl;
    private URL feedUrl;
    private URL trackViewUrl;
    private String artwork30;
    private String artwork60;
    private String artwork100;
    private String artwork600;
    private String artworkFileExtension;
    private float collectionPrice;
    private float trackPrice;
    private Date releaseDate;
    private boolean collectionIsExplicit;
    private boolean trackIsExplicit;
    private int trackCount;
    private String country;
    private Currency currency;
    private String primaryGenreName;
    private int[] genreIds;
    private String[] genres;

    public Podcast( String wt, int c_id, int t_id, String aname, String cname, String tname, String cname_cens,
                    String tname_cens, String cvUrl, String fUrl, String tvUrl, String art30, String art60, String art100,
                    String art600, float cprice, float tprice, String rdate, boolean cexp, boolean texp, int tcount, String c,
                    String curr, String pgenre, int[] gids, String[] genres_arr ){
        wrapperType = wt;
        collectionId = c_id;
        trackId = t_id;
        artistName = aname;
        collectionName = cname;
        trackName = tname;
        collectionCensoredName = cname_cens;
        trackCensoredName = tname_cens;
        try {
            collectionViewUrl = new URL(cvUrl);
            feedUrl = new URL(fUrl);
            trackViewUrl = new URL(tvUrl);
        } catch( MalformedURLException ex ) {
            Log.e("test","error setting URL in Podcast constructor", ex);
        }
        collectionPrice = cprice;
        trackPrice = tprice;
        collectionIsExplicit = cexp;
        trackIsExplicit = texp;
        trackCount = tcount;
        country = c;
        currency = Currency.getInstance(curr);
        primaryGenreName = pgenre;
        genreIds = new int[gids.length];
        for( int i = 0; i < gids.length; i++ )
            genreIds[i] = gids[i];
        genres = new String[genres_arr.length];
        for( int i = 0; i < genres_arr.length; i++ )
            genres[i] = genres_arr[i];
        artworkFileExtension = art30.substring(art30.lastIndexOf('.')+1);
        try {
            downloadArtworkFromUrl(new URL(art30), "30");
            downloadArtworkFromUrl(new URL(art60), "60");
            downloadArtworkFromUrl(new URL(art100), "100");
            downloadArtworkFromUrl(new URL(art600), "600");
        }catch(MalformedURLException ex){
            Log.e("test","artwork url not formed correctly",ex);
        }
    }
    public String toString(){
        return "Artist name: "+artistName+", track name: "+trackName;
    }
    public String getArtworkPath(String dimension){
        try{
            Field f = this.getClass().getDeclaredField("artwork"+dimension);
            return f.get(this).toString();
        }catch(NoSuchFieldException ex){
            Log.e("test","No such artwork file",ex);
            return null;
        }catch(IllegalAccessException ex){
            Log.e("test","Illegal access attempt",ex);
            return null;
        }
    }
    public void downloadArtworkFromUrl(URL url, String dimension) {
        try {
            InputStream in = url.openStream();
            String external = Environment.getExternalStorageDirectory().toString();
            File meta = new File(NetworkActivity.getAppContext().getFilesDir(),"meta");
            meta.mkdirs();
            File dir = new File(meta,artistName.replace(' ','_'));
            dir.mkdirs();
            try {
                String artworkUrl = url.toString();
                artworkFileExtension = artworkUrl.substring(artworkUrl.lastIndexOf('.')+1);
            } catch( Exception ex ) {
                Log.e("test","Dunno what happened, lol.",ex);
            }
            String filename = "artwork"+dimension+"."+artworkFileExtension;
            //File imageFile = new File(external,filename);
            File imageFile = new File(dir,filename);
            imageFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            Bitmap image = BitmapFactory.decodeStream(in);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            in.close();
            try {
                this.getClass().getDeclaredField("artwork"+dimension).set(this,imageFile.getAbsolutePath());
            }catch( NoSuchFieldException ex ){
                Log.e("test","Error setting path to artwork",ex);
            }catch( IllegalAccessException ex ){
                Log.e("test","Error accessing variable for path to artwork",ex);
            }
        } catch ( IOException ex ) {
            Log.e("test","Error opening url for "+dimension+"x"+dimension+" artwork",ex);
        }
    }
    public String getWrapperType(){
        return wrapperType;
    }
    public String getArtistName(){
        return artistName;
    }
    public String getCollectionName(){
        return collectionName;
    }
    public String getTrackName(){
        return trackName;
    }
    public String getCollectionCensoredName(){
        return collectionCensoredName;
    }
    public String getTrackCensoredName(){
        return trackCensoredName;
    }
    public String getCountry(){
        return country;
    }
    public String getPrimaryGenreName(){
        return primaryGenreName;
    }
    public String getCollectionViewUrl(){
        return collectionViewUrl.toString();
    }
    public String getFeedUrl(){
        return feedUrl.toString();
    }
    public String getTrackViewUrl(){
        return trackViewUrl.toString();
    }
    public int getCollectionId(){
        return collectionId;
    }
    public int getTrackId(){
        return trackId;
    }
    public int getTrackCount(){
        return trackCount;
    }
    public float getCollectionPrice(){
        return collectionPrice;
    }
    public int[] getGenreIds() {
        return genreIds;
    }
    public String[] getGenres() {
        return genres;
    }
    public float getTrackPrice() {
        return trackPrice;
    }
    public boolean collectionIsExplicit() {
        return collectionIsExplicit;
    }
    public boolean trackIsExplicit() {
        return trackIsExplicit;
    }
}
