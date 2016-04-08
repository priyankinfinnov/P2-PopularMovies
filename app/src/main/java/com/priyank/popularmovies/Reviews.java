package com.priyank.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Reviews{
    private List<Review> reviews;

    public Reviews(String fullJson) {
        reviews = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(fullJson);

            JSONObject reviewsJSON = root.getJSONObject("reviews");
            JSONArray movieReviews = reviewsJSON.getJSONArray("results");

            for (int i = 0; i < movieReviews.length(); i++) {
                Review review = new Review();
                JSONObject reviewJSON = movieReviews.getJSONObject(i);
                review.setAuthor(reviewJSON.getString(Constants.JSON_MOVIE_REVIEW_AUTHOR));
                review.setReview(reviewJSON.getString(Constants.JSON_MOVIE_REVIEW_CONTENT));
                reviews.add(i, review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Review> getReviews() {
        return reviews;
    }
}
class Review {

    String author;
    String review;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
