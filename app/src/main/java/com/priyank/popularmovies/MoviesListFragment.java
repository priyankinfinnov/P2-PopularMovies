package com.priyank.popularmovies;


import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.priyank.popularmovies.database.MoviesProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesListFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIES_LOADER = 0;

    JSONArray main;
    gridAdapter gridAdapter;
    cursorGridAdapter cursorGridAdapter;
    GridView gridView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    View view;

    public MoviesListFragment() {
        // Required empty public constructor
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Cursor cursor = getActivity().getContentResolver().query(
                MoviesProvider.Movies.CONTENT_URI,
                null,
                null,
                null,
                null);

        return new CursorLoader(getActivity(),
                MoviesProvider.Movies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        cursorGridAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        cursorGridAdapter.swapCursor(null);
    }

    public static String getFirst() {
        return null;
    }


    public interface CallbackOnItemSelected {
        /**
         * MovieListFragment for when an item has been selected.
         * @param object
         */
        void onItemSelected(String object);
    }
    public interface CallbackOnLoad {
        /**
         * MovieListFragment for when an item has been selected.
         */
        void onLoad(String object);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        gridView = (GridView) view.findViewById(R.id.gridView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);


        cursorGridAdapter = new cursorGridAdapter(getActivity(), null, 0);
        gridAdapter = new gridAdapter(MainActivity.mContext);


        gridView.setAdapter(gridAdapter);
        update();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ((CallbackOnItemSelected) getActivity())
                        .onItemSelected(view.getTag().toString());

            }
        });

        getLoaderManager().initLoader(MOVIES_LOADER, null, this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movies_list, container, false);
        return view;
    }


    public class  fetchData extends AsyncTask<Void,Void,String> {
        String fullJson;

        @Override
        protected void onPostExecute(String s) {
            Log.d(Constants.TAG, fullJson +"");
            try {
                if(s == null)
                    return;
                JSONObject root = new JSONObject(fullJson);

                main = root.getJSONArray(Constants.JSON_RESULTS_ARRAY);

                for (int i = 0; i < main.length(); i++) {
                    JSONObject movieJSON = main.getJSONObject(i);
                    gridAdapter.add(movieJSON.getString(Constants.JSON_POSTER_PATH),movieJSON);
                }

                ((CallbackOnLoad) getActivity())
                        .onLoad(main.getJSONObject(0).toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            gridAdapter.notifyDataSetChanged();
            gridAdapter.notifyDataSetInvalidated();
            gridView.smoothScrollToPosition(0);
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(Constants.BASE_URL + "sort_by=" + Constants.SORT_BY
                        + "&vote_count.gte=" + Constants.MIN_VOTES + "&api_key=" + Constants.API_KEY);

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

    private void update() {
        gridView.setAdapter(null);
        gridView.setAdapter(gridAdapter);
        gridAdapter.clean();
        mSwipeRefreshLayout.setRefreshing(true);
        new fetchData().execute();
    }

    public void favourites(){
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        gridView.setAdapter(cursorGridAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_movies_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_popularity) {
            Constants.SORT_BY = Constants.POPULARITY_DESCENDING;
            update();
            return true;
        }
        else if (id == R.id.action_sort_rating){
            Constants.SORT_BY = Constants.VOTE_AVERAGE_DESCENDING;
            update();
            return true;
        }
        else if(id == R.id.action_favourites) {
            favourites();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
