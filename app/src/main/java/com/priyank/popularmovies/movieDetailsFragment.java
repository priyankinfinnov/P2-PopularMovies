package com.priyank.popularmovies;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.priyank.popularmovies.database.MoviesColumns;
import com.priyank.popularmovies.database.MoviesProvider;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class movieDetailsFragment extends Fragment {

    View view;
    String mID;
    RecyclerAdapter trailerAdapter;
    Trailers trailers;
    JSONArray movieReviews;
    RecyclerView trailerRecyclerView;
    LinearLayout reviewsView;
    Movie mMovie;
    private ShareActionProvider mShareActionProvider;
    Context mContext;


    public movieDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if (arguments.getString(Constants.JSON_TAG) != null) {
            mMovie = new Movie(arguments.getString(Constants.JSON_TAG));
        }

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ImageView backdrop = (ImageView) view.findViewById(R.id.backdropView);
        ImageView poster = (ImageView) view.findViewById(R.id.posterView);
        TextView titleView = (TextView) view.findViewById(R.id.titleView);
        TextView releasedView = (TextView) view.findViewById(R.id.releasedView);
        TextView starText = (TextView) view.findViewById(R.id.starText);
        TextView overviewText = (TextView) view.findViewById(R.id.overviewView);
        trailerRecyclerView = (RecyclerView) view.findViewById(R.id.trailer_recyclerView);
        reviewsView = (LinearLayout) view.findViewById(R.id.reviewLinearLayout);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.favorite);


        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trailerRecyclerView.setLayoutManager(layoutManager);

//        mMovie = new Movie(getIntent().getStringExtra(Constants.JSON_TAG));
        Cursor cursor = getActivity().getContentResolver().query(
                MoviesProvider.Movies.CONTENT_URI,
                new String[]{MoviesColumns._ID},
                MoviesColumns._ID + " = ?",
                new String[]{mMovie.getID()},
                null);
        if(cursor.moveToFirst())
            mMovie.setFavourite(true);
        else
            mMovie.setFavourite(false);

        if(mMovie == null)
            return;

        if(mMovie.isFavourite())
            floatingActionButton.setImageResource(R.drawable.heart);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMovie.isFavourite()) {
                    removeFavourite(mMovie);
                    mMovie.setFavourite(false);

                    floatingActionButton.setImageResource(R.drawable.heart_outline);
                } else {
                    mMovie.setFavourite(true);
                    insertFavourite(mMovie);
                    floatingActionButton.setImageResource(R.drawable.heart);
                }

            }
        });

        mID = mMovie.getID();

        Picasso.with(getActivity())
                .load(mMovie.getBackdrop())
                .fit()
                .into(backdrop);

        Picasso.with(getActivity())
                .load(mMovie.getPoster())
                .fit()
                .into(poster);

        final Intent intent = new Intent(getActivity(), fullImageActivity.class);
        intent.putExtra(Constants.MOVIE_TAG, mMovie.getTitle());

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(Constants.IMG_TAG, mMovie.getPoster());
                startActivity(intent);
            }
        });

        backdrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(Constants.IMG_TAG, mMovie.getBackdrop());
                startActivity(intent);
            }
        });

        titleView.setText(mMovie.getTitle());
        releasedView.setText(mMovie.getReleaseDate());
        starText.setText(mMovie.getVoteAvg());
        overviewText.setText(mMovie.getOverview());
//        getActivity().getActionBar().setTitle(mMovie.getTitle());

        new fetchTrailers().execute();
        new fetchReviews().execute();
    }


    public class fetchTrailers extends AsyncTask<Void,Void,String> {
        String fullJson;

        @Override
        protected void onPostExecute(String s) {
            if(s==null)
                return;
            trailers = new Trailers(fullJson);
            mMovie.setTrailers(trailers);

            trailerAdapter = new RecyclerAdapter(getActivity(), trailers.getLinks());
            trailerRecyclerView.setAdapter(trailerAdapter);

            if(mShareActionProvider != null)
                mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(Constants.TRAILER_BASE_URL + mID
                        + "?api_key=" + Constants.API_KEY
                        + "&append_to_response=trailers");

                Log.d("hey", url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream == null)
                    return null;

                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                if(buffer.length() == 0)
                    return null;

                fullJson = buffer.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (reader != null){
                    try {
                        reader.close();
                    }catch (final IOException e){
                        e.printStackTrace();
                    }
                }
            }
            return fullJson;
        }
    }

    public class fetchReviews extends AsyncTask<Void,Void,String> {
        String fullJson;

        @Override
        protected void onPostExecute(String s) {
            if(s == null)
                return;
            Reviews reviews = new Reviews(fullJson);
            mMovie.setReviews(reviews);
            setReviewsUI(reviews.getReviews());
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(Constants.TRAILER_BASE_URL + mID
                        + "?api_key=" + Constants.API_KEY
                        + "&append_to_response=reviews");

                Log.d("hey",url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream == null)
                    return null;

                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                if(buffer.length() == 0)
                    return null;

                fullJson = buffer.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (reader != null){
                    try {
                        reader.close();
                    }catch (final IOException e){
                        e.printStackTrace();
                    }
                }
            }
            return fullJson;
        }
    }

    private void setReviewsUI(List<Review> reviewsList) {
        reviewsView.removeAllViews();
        LayoutInflater inflater =((Activity)mContext).getLayoutInflater();

        for(Review review : reviewsList){
            ViewGroup reviewContainer = (ViewGroup) inflater.inflate(R.layout.review_item,reviewsView,false);

            ((TextView)reviewContainer.findViewById(R.id.reviewAuthorText)).setText(review.getAuthor());
            ((TextView)reviewContainer.findViewById(R.id.reviewText)).setText(review.getReview());

            reviewsView.addView(reviewContainer);
        }
        if(reviewsList.size() == 0){
            ViewGroup reviewContainer = (ViewGroup) inflater.inflate(R.layout.review_item,reviewsView,false);

            ((TextView)reviewContainer.findViewById(R.id.reviewAuthorText)).setText("No Reviews");
            reviewsView.addView(reviewContainer);
        }

    }

    private void removeFavourite(Movie movie) {

        getActivity().getContentResolver().delete(MoviesProvider.Movies.CONTENT_URI,
                MoviesColumns._ID + "= ?",
                new String[]{movie.getID()});
        Toast.makeText(getActivity(), "Removed from Favourites", Toast.LENGTH_LONG).show();
    }

    private void insertFavourite(Movie movie) {

        ContentValues cv = new ContentValues();

        cv.put(MoviesColumns.BACKDROP,movie.getBackdrop());
        cv.put(MoviesColumns.DATE,movie.getReleaseDate());
        cv.put(MoviesColumns.OVERVIEW,movie.getOverview());
        cv.put(MoviesColumns.POSTER,movie.getPoster());
        cv.put(MoviesColumns.TITLE, movie.getTitle());
        cv.put(MoviesColumns.RATING, movie.getVoteAvg());
        cv.put(MoviesColumns._ID, movie.getID());

        getActivity().getContentResolver().insert(MoviesProvider.Movies.CONTENT_URI, cv);
        Toast.makeText(getActivity(),"Added to Favourites",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movies_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        if(mMovie.getTrailers().getLinks().size() > 0)
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + mMovie.getTrailers().getLinks().get(0));
        else
            shareIntent.putExtra(Intent.EXTRA_TEXT, "No trailer to share.");
        return shareIntent;
    }

}

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    Context mContext;
    List<String> trailers;

    public RecyclerAdapter(Context mContext, List<String> trailerLinks) {
        this.mContext = mContext;
        trailers = trailerLinks;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailers_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, final int position) {
        Picasso.with(mContext)
                .load("http://img.youtube.com/vi/" + trailers.get(position) + "/0.jpg")
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String link = "https://www.youtube.com/watch?v=" + trailers.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public void add(int position,String object) {
        trailers.add(object);
        notifyDataSetChanged();
    }
}
