package com.example.android.podcastapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
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
import java.util.concurrent.ExecutionException;

import static java.lang.System.*;

/**
 * Created by petersomers on 8/29/13.
 */
/*
"genreIds":["1301", "26"], "genres":["Arts", "Podcasts"]}
 */
public class Podcast implements Parcelable {
    private int collectionId;
    private int trackId;
    private int trackCount;
    private int[] genreIds;
    private float collectionPrice;
    private float trackPrice;
    private File artwork30File;
    private File artwork60File;
    private File artwork100File;
    private File artwork600File;
    private String wrapperType;
    private String artistName;
    private String collectionName;
    private String collectionCensoredName;
    private String trackName;
    private String trackCensoredName;
    private String artworkFileExtension;
    private String primaryGenreName;
    private String releaseDate;
    private String[] genres;
    private String country;
    private URL artwork30Url;
    private URL artwork60Url;
    private URL artwork100Url;
    private URL artwork600Url;
    private URL collectionViewUrl;
    private URL feedUrl;
    private URL trackViewUrl;
    private Currency currency;
    private boolean collectionIsExplicit;
    private boolean trackIsExplicit;
    private Context context;

    public Podcast(int c_id, int t_id, int tcount, int[] gids, float cprice, float tprice, String wt, String aname, String cname, String cname_cens, String tname,
                   String tname_cens, String art30, String art60, String art100, String art600, String pgenre, String rdate, String c, String[] genres_arr, String cvUrl, String fUrl, String tvUrl,
                   String curr, boolean cexp, boolean texp, Context context){
        collectionId = c_id;
        trackId = t_id;
        trackCount = tcount;
        genreIds = new int[gids.length];
        arraycopy(gids, 0, genreIds, 0, gids.length);
        collectionPrice = cprice;
        trackPrice = tprice;
        wrapperType = wt;
        artistName = aname;
        collectionName = cname;
        collectionCensoredName = cname_cens;
        trackName = tname;
        trackCensoredName = tname_cens;
        try {
            collectionViewUrl = new URL(cvUrl);
            feedUrl = new URL(fUrl);
            trackViewUrl = new URL(tvUrl);
        } catch( MalformedURLException ex ) {
            Log.e("test","error setting URL in Podcast constructor", ex);
        }
        primaryGenreName = pgenre;
        releaseDate = rdate;
        country = c;
        genres = new String[genres_arr.length];
        arraycopy(genres_arr, 0, genres, 0, genres_arr.length);
        artworkFileExtension = art30.substring(art30.lastIndexOf('.')+1);
        currency = Currency.getInstance(curr);
        collectionIsExplicit = cexp;
        trackIsExplicit = texp;
        try{
            artwork30Url = new URL(art30);
            artwork60Url = new URL(art60);
            artwork100Url = new URL(art100);
            artwork600Url = new URL(art600);
        }catch(MalformedURLException ex){
            Log.e("test","artwork url not formed correctly",ex);
        }
        artwork30File = new File("");
        artwork60File = new File("");
        artwork100File = new File("");
        artwork600File = new File("");
        this.context = context;
    }

    /**
     * This should only be called when the podcast is being subscribed to.
     */
    public void downloadAllArtworkToFiles(){
        downloadArtworkFromUrl(artwork30Url, "30");
        downloadArtworkFromUrl(artwork60Url, "60");
        downloadArtworkFromUrl(artwork100Url, "100");
        downloadArtworkFromUrl(artwork600Url, "600");
    }
    public Podcast(Parcel parcel) {
        collectionId = parcel.readInt();
        trackId = parcel.readInt();
        trackCount = parcel.readInt();
        genreIds = parcel.createIntArray();
        collectionPrice = parcel.readFloat();
        trackPrice = parcel.readFloat();
        wrapperType = parcel.readString();
        artistName = parcel.readString();
        collectionName = parcel.readString();
        collectionCensoredName = parcel.readString();
        trackName =parcel.readString();
        trackCensoredName = parcel.readString();
        artwork30File = new File(parcel.readString());
        artwork60File = new File(parcel.readString());
        artwork100File = new File(parcel.readString());
        artwork600File = new File(parcel.readString());
        try {
            artwork30Url = new URL(parcel.readString());
            artwork60Url = new URL(parcel.readString());
            artwork100Url = new URL(parcel.readString());
            artwork600Url = new URL(parcel.readString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        primaryGenreName = parcel.readString();
        releaseDate = parcel.readString();
        country = parcel.readString();
        try {
            collectionViewUrl = new URL(parcel.readString());
            feedUrl = new URL(parcel.readString());
            trackViewUrl = new URL(parcel.readString());
        } catch( MalformedURLException ex ) {
            Log.e("test","error setting URL in Podcast constructor", ex);
        }
        genres = parcel.createStringArray();
        currency = Currency.getInstance(parcel.readString());
        collectionIsExplicit = (parcel.readByte() != 0);
        trackIsExplicit = (parcel.readByte() != 0);
    }

    public String toString(){
        return "Artist name: "+artistName+", track name: "+trackName;
    }
    public String getArtworkPath(String dimension){
        try{
            Field f = this.getClass().getDeclaredField("artwork"+dimension+"File");
            return ((File)f.get(this)).getAbsolutePath();
        }catch(NoSuchFieldException ex){
            Log.e("test","No such artwork file",ex);
            return null;
        }catch(IllegalAccessException ex){
            Log.e("test","Illegal access attempt",ex);
            return null;
        }
    }
    public URL getArtworkUrl(String dimension){
        try{
            Field f = this.getClass().getDeclaredField("artwork"+dimension+"Url");
            return (URL)f.get(this);
        }catch(NoSuchFieldException ex){
            Log.e("test","No such artwork file",ex);
            return null;
        }catch(IllegalAccessException ex){
            Log.e("test","Illegal access attempt",ex);
            return null;
        }catch(Exception ex){
            Log.e("test","",ex);
        }
        return null;
    }

    /**
     * This returns a bitmap of the local file if it exists. Otherwise it downloads
     * a bitmap from the stored url, blocking the main thread.
     * @param dimension
     * @return
     */
    public Bitmap getBitmap(String dimension){
        Bitmap bitmap = null;
        String artworkPath = getArtworkPath(dimension);
        if(artworkPath.equals("/")){//The artwork hasn't been downloaded. Get it from the URL.
            URL url = getArtworkUrl(dimension);
            try {
                bitmap = new RetrieveBitmapTask().execute(url).get();
            } catch (Exception ex) {
                Log.e("test","",ex);
                int id = R.drawable.default_artwork30;
                if(dimension.equals("60"))
                    id = R.drawable.default_artwork60;
                else if(dimension.equals("100"))
                    id = R.drawable.default_artwork100;
                else if(dimension.equals("600"))
                    id = R.drawable.default_artwork600;
                bitmap = BitmapFactory.decodeResource(context.getResources(), id);
            }
        }
        else
            bitmap = BitmapFactory.decodeFile(artworkPath);
        return bitmap;
    }
    private class RetrieveBitmapTask extends AsyncTask<URL,Void,Bitmap>{
        public Bitmap doInBackground(URL... urls){
            try {
                return BitmapFactory.decodeStream(urls[0].openStream());
            } catch (IOException e) {
                Log.e("test","",e);
                return null;
            }
        }
    }

    /**
     * This method Downloads the artwork for the image of the specified dimension.
     * Note: it does NOT use an AsyncTask (yet) because it's more efficient to just create
     * one background thread wherever the podcasts are being created.
     * @param url
     * @param dimension
     */
    private void downloadArtworkFromUrl(URL url, String dimension) {
        try {
            File meta = new File(PodcastActivity.getAppContext().getFilesDir(),"meta");
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
            File imageFile = new File(dir,filename);
            imageFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            try {
                Bitmap image = new RetrieveBitmapTask().execute(url).get();
                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                in.close();
                this.getClass().getDeclaredField("artwork"+dimension+"File").set(this,imageFile);
            }catch( NoSuchFieldException ex ){
                Log.e("test","Error setting path to artwork",ex);
            }catch( IllegalAccessException ex ){
                Log.e("test","Error accessing variable for path to artwork",ex);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
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
    public String getReleaseDate(){
        return releaseDate;
    }
    public String getCollectionViewUrl(){
        return collectionViewUrl.toString();
    }
    public URL getFeedUrl(){
        return feedUrl;
    }
    public String getTrackViewUrl(){
        return trackViewUrl.toString();
    }
    public Currency getCurrency(){
        return currency;
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

    /**
     * For debugging
     */
    public String allFieldsToString(){
        Field[] fields = Podcast.class.getDeclaredFields();
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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(collectionId);
        parcel.writeInt(trackId);
        parcel.writeInt(trackCount);
        parcel.writeIntArray(genreIds);
        parcel.writeFloat(collectionPrice);
        parcel.writeFloat(trackPrice);
        parcel.writeString(wrapperType);
        parcel.writeString(artistName);
        parcel.writeString(collectionName);
        parcel.writeString(collectionCensoredName);
        parcel.writeString(trackName);
        parcel.writeString(trackCensoredName);
        parcel.writeString(artwork30File.getAbsolutePath());
        parcel.writeString(artwork60File.getAbsolutePath());
        parcel.writeString(artwork100File.getAbsolutePath());
        parcel.writeString(artwork600File.getAbsolutePath());
        parcel.writeString(artwork30Url.toString());
        parcel.writeString(artwork60Url.toString());
        parcel.writeString(artwork100Url.toString());
        parcel.writeString(artwork600Url.toString());
        parcel.writeString(primaryGenreName);
        parcel.writeString(releaseDate);
        parcel.writeString(country);
        parcel.writeString(collectionViewUrl.toString());
        parcel.writeString(feedUrl.toString());
        parcel.writeString(trackViewUrl.toString());
        parcel.writeStringArray(genres);
        parcel.writeString(currency.toString());
        parcel.writeByte((byte) (collectionIsExplicit ? 1 : 0));//for whatever reason there's no
        parcel.writeByte((byte) (trackIsExplicit ? 1 : 0));     //writeBoolean()...
    }
    public static final Parcelable.Creator<Podcast> CREATOR
                  = new Parcelable.Creator<Podcast>() {
        public Podcast createFromParcel(Parcel parcel) {
            return new Podcast(parcel);
        }

        public Podcast[] newArray(int size) {
            return new Podcast[size];
        }
    };
}