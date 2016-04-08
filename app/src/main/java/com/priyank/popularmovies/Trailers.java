package com.priyank.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Trailers {
    private List<String> mLinks;

    public Trailers(String fullJson) {
        mLinks = new ArrayList<>();
        try {
        JSONObject root = new JSONObject(fullJson);

        JSONObject allTrailers = root.getJSONObject("trailers");
        JSONArray links = allTrailers.getJSONArray("youtube");

        for(int i = 0; i < links.length(); i++)
        {
                mLinks.add(((JSONObject)links.get(i)).getString(Constants.JSON_MOVIE_VID_SOURCE));
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLinks() {
        return mLinks;
    }
}
