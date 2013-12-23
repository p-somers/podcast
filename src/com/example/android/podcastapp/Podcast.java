package com.example.android.podcastapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.Date;

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
    private String wrapperType;
    private String artistName;
    private String collectionName;
    private String collectionCensoredName;
    private String trackName;
    private String trackCensoredName;
    private String artwork30;
    private String artwork60;
    private String artwork100;
    private String artwork600;
    private String artworkFileExtension;
    private String primaryGenreName;
    private String releaseDate;
    private String[] genres;
    private String country;
    private URL collectionViewUrl;
    private URL feedUrl;
    private URL trackViewUrl;
    private Currency currency;
    private boolean collectionIsExplicit;
    private boolean trackIsExplicit;

    public Podcast(int c_id, int t_id, int tcount, int[] gids, float cprice, float tprice, String wt, String aname, String cname, String cname_cens, String tname,
                   String tname_cens, String art30, String art60, String art100, String art600, String pgenre, String rdate, String c, String[] genres_arr, String cvUrl, String fUrl, String tvUrl,
                   String curr, boolean cexp, boolean texp){
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
        try {
            downloadArtworkFromUrl(new URL(art30), "30");
            downloadArtworkFromUrl(new URL(art60), "60");
            downloadArtworkFromUrl(new URL(art100), "100");
            downloadArtworkFromUrl(new URL(art600), "600");
        }catch(MalformedURLException ex){
            Log.e("test","artwork url not formed correctly",ex);
        }
        currency = Currency.getInstance(curr);
        collectionIsExplicit = cexp;
        trackIsExplicit = texp;
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
        artwork30 = parcel.readString();
        artwork60 = parcel.readString();
        artwork100 = parcel.readString();
        artwork600 = parcel.readString();
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

    /**
     * This method Downloads the artwork for the image of the specified dimension.
     * Note: it does NOT use an AsyncTask (yet) because it's more efficient to just create
     * one background thread wherever the podcasts are being created.
     * @param url
     * @param dimension
     */
    public void downloadArtworkFromUrl(URL url, String dimension) {
        try {
            InputStream in = url.openStream();
            String external = Environment.getExternalStorageDirectory().toString();
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
    public URL getFeedUrl(){
        return feedUrl;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
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
        parcel.writeString(artwork30);
        parcel.writeString(artwork60);
        parcel.writeString(artwork100);
        parcel.writeString(artwork600);
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
        @Override
        public Podcast createFromParcel(Parcel parcel) {
            return new Podcast(parcel);
        }

        public Podcast[] newArray(int size) {
            return new Podcast[size];
        }
    };
}