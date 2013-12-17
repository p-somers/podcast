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

package com.example.android.networkusage;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
public class JSONParser {

    public Podcast parse(InputStream in) throws IOException {
        try {
            Podcast podcast = null;
            Scanner scan = new Scanner(in);
            StringBuilder result = new StringBuilder();
            while( scan.hasNextLine() ) {
                String line = scan.nextLine();
                result.append( line );
            }
            try {
                //Log.d("test","RESULT: "+result.toString());
                JSONObject json = new JSONObject(result.toString());
                JSONObject info = json.getJSONArray("results").getJSONObject(0);

                String wt = info.getString("wrapperType");
                int cid = info.getInt("collectionId");
                int tid = info.getInt("trackId");
                String aname = info.getString("artistName");
                String cname = info.getString("collectionName");
                String tname = info.getString("trackName");
                String cname_cens = info.getString("collectionCensoredName");
                String tname_cens = info.getString("trackCensoredName");
                String cvu = info.getString("collectionViewUrl");
                String furl = info.getString("feedUrl");
                String turl = info.getString("trackViewUrl");
                String a30 = info.getString("artworkUrl30");
                String a60 = info.getString("artworkUrl60");
                String a100 = info.getString("artworkUrl100");
                String a600 = info.getString("artworkUrl600");
                float cprice = (float)info.getLong("collectionPrice");
                float tprice = (float)info.getLong("trackPrice");
                String rdate = info.getString("releaseDate");
                boolean c_exp = info.getString("collectionExplicitness").equals("explicit");
                boolean t_exp = info.getString("trackExplicitness").equals("explicit");
                int tcount = info.getInt("trackCount");
                String country = info.getString("country");
                String curr = info.getString("currency");
                String pgenrename = info.getString("primaryGenreName");
                JSONArray json_gids = info.getJSONArray("genreIds");
                int[] gids = new int[json_gids.length()];
                for(int i = 0; i < json_gids.length(); i++)
                    gids[i]=json_gids.getInt(i);
                JSONArray json_genres = info.getJSONArray("genres");
                String[] genres = new String[json_genres.length()];
                for(int i = 0; i < json_genres.length(); i++)
                    genres[i]=json_genres.getString(i);
                podcast = new Podcast(wt,cid,tid,aname,cname,tname,cname_cens,tname_cens,
                        cvu,furl,turl,a30,a60,a100,a600,cprice,tprice,rdate,c_exp,t_exp,tcount,
                        country,curr,pgenrename,gids,genres);
            } catch( JSONException ex ) {
                Log.e("test","error",ex);
            }
            return podcast;
        } finally {
            in.close();
        }
    }
}
