package com.example.android.podcastapp;

import java.net.URL;

/**
 * Created by petersomers on 12/22/13.
 */
public class Episode {
    private String title;
    private String copyright;
    private String pubDate;
    private String author;
    private URL link;
    public Episode(Podcast podcast){
        title = null;
        copyright = null;
        pubDate = null;
        author = null;
        link = null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }
}
