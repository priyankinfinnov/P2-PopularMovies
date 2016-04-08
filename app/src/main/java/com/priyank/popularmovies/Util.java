package com.priyank.popularmovies;

import android.database.Cursor;

import com.priyank.popularmovies.database.MoviesColumns;

import org.json.JSONException;
import org.json.JSONObject;

public class Util {

    public static JSONObject cursorToJsonObj(Cursor cursor){

        JSONObject movieJson = new JSONObject();

        try {
            movieJson.put(Constants.JSON_MOVIE_ID,cursor.getString(MoviesColumns.COL_ID));
            movieJson.put(Constants.JSON_BACKDROP_PATH,cursor.getString(MoviesColumns.COL_BACKDROP));
            movieJson.put(Constants.JSON_MOVIE_OVERVIEW,cursor.getString(MoviesColumns.COL_OVERVIEW));
            movieJson.put(Constants.JSON_MOVIE_RELEASE_DATE,cursor.getString(MoviesColumns.COL_DATE));
            movieJson.put(Constants.JSON_MOVIE_TITLE,cursor.getString(MoviesColumns.COL_TITLE));
            movieJson.put(Constants.JSON_MOVIE_VOTE_AVERAGE,cursor.getString(MoviesColumns.COL_RATING));
            movieJson.put(Constants.JSON_POSTER_PATH,cursor.getString(MoviesColumns.COL_POSTER));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieJson;
    }
}
