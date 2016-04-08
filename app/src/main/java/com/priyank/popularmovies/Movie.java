package com.priyank.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;


public class Movie {

    private String ID;
    private String title;
    private String releaseDate;
    private String poster;
    private String backdrop;
    private String overview;
    private String voteAvg;
    private Reviews reviews;
    private Trailers trailers;
    private Boolean Favourite;


    public Movie(){}



    public Movie(String movieText) {
        try {
            JSONObject movieJSON = new JSONObject(movieText);

            ID = movieJSON.getString(Constants.JSON_MOVIE_ID);

            backdrop = Constants.IMG_BASE_URL + Constants.FULL_IMG_SIZE + movieJSON.getString(Constants.JSON_BACKDROP_PATH);
            poster = Constants.IMG_BASE_URL + Constants.FULL_IMG_SIZE + movieJSON.getString(Constants.JSON_POSTER_PATH);

            title = movieJSON.getString(Constants.JSON_MOVIE_TITLE);
            releaseDate = (movieJSON.getString(Constants.JSON_MOVIE_RELEASE_DATE)).substring(0, 4);
            voteAvg = movieJSON.getString(Constants.JSON_MOVIE_VOTE_AVERAGE);
            overview = movieJSON.getString(Constants.JSON_MOVIE_OVERVIEW);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public Movie(Cursor cursor) {
//
//        ID = cursor.getString(0);
//
//        backdrop = cursor.getString(cursor.getColumnIndex("backdrop"));
//        poster = cursor.getString(cursor.getColumnIndex("poster"));
//
//        title = cursor.getString(cursor.getColumnIndex("title"));
//        releaseDate = cursor.getString(cursor.getColumnIndex("date")).substring(0,4);
//        voteAvg = cursor.getString(cursor.getColumnIndex("rating"));
//        overview = cursor.getString(cursor.getColumnIndex("overview"));
//    }

    public Boolean isFavourite() {
        return Favourite;
    }

    public void setFavourite(Boolean favourite) {
        Favourite = favourite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAvg() {
        return voteAvg;
    }

    public void setVoteAvg(String voteAvg) {
        this.voteAvg = voteAvg;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Reviews getReviews() {
        return reviews;
    }

    public void setReviews(Reviews reviews) {
        this.reviews = reviews;
    }

    public Trailers getTrailers() {
        return trailers;
    }

    public void setTrailers(Trailers trailers) {
        this.trailers = trailers;
    }
}
