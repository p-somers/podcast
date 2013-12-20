/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.android.podcastapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;

public class JSONParser {

    public Podcast toPodcastObj(JSONObject info) throws IOException {
        Podcast podcast = null;
        int cid = getInt(info,"collectionId");
        int tid = getInt(info,"trackId");
        int tcount = getInt(info,"trackCount");
        float cprice = getFloat(info, "collectionPrice");
        float tprice = getFloat(info, "trackPrice");
        String wt = getString(info, "wrapperType");
        String aname = getString(info, "artistName");
        String cname = getString(info, "collectionName");
        String tname = getString(info,"trackName");
        String cname_cens = getString(info,"collectionCensoredName");
        String tname_cens = getString(info,"trackCensoredName");
        String cvu  = getString(info,"collectionViewUrl");
        String furl = getString(info,"feedUrl");
        String turl = getString(info,"trackViewUrl");
        String a30  = getString(info,"artworkUrl30");
        String a60  = getString(info, "artworkUrl60");
        String a100 = getString(info, "artworkUrl100");
        String a600 = getString(info, "artworkUrl600");
        String rdate = getString(info, "releaseDate");
        String country = getString(info, "country");
        String curr = getString(info, "currency");
        String pgenrename = getString(info, "primaryGenreName");
        boolean c_exp = is_explicit(info, "collectionExplicitness");
        boolean t_exp = is_explicit(info, "trackExplicitness");
        int[] gids = int_array(info,"genreIds");
        String[] genres = string_array(info,"genres");
        podcast = new Podcast(wt,cid,tid,aname,cname,tname,cname_cens,tname_cens,
                cvu,furl,turl,a30,a60,a100,a600,cprice,tprice,rdate,c_exp,t_exp,tcount,
                country,curr,pgenrename,gids,genres);
        return podcast;
    }
    //for gracefully handling exceptions
    private String getString(JSONObject info, String tag){
        try{
            return info.getString(tag);
        } catch(JSONException ex) {
            return tag;
        }
    }
    private int getInt(JSONObject info, String tag){
        try{
            return info.getInt(tag);
        } catch(JSONException ex){
            return -1;
        }
    }
    private float getFloat(JSONObject info, String tag){
        try{
            return (float)info.getLong(tag);
        } catch(JSONException ex){
            return -1;
        }
    }
    private boolean is_explicit(JSONObject info, String tag){
        try{
            return info.getString(tag).equals("explicit");
        } catch (JSONException ex){
            return false;
        }
    }
    private int[] int_array(JSONObject info, String tag){
        try{
            JSONArray json_arr = info.getJSONArray(tag);
            int arr[] =  new int[json_arr.length()];
            for(int i = 0; i < json_arr.length(); i++)
                arr[i]= json_arr.getInt(i);
            return arr;
        } catch(JSONException ex){
            return new int[0];
        }
    }
    private String[] string_array(JSONObject info, String tag){
        try{
            JSONArray json_arr = info.getJSONArray(tag);
            String arr[] =  new String[json_arr.length()];
            for(int i = 0; i < json_arr.length(); i++)
                arr[i]= json_arr.getString(i);
            return arr;
        } catch(JSONException ex){
            return new String[0];
        }
    }
}

