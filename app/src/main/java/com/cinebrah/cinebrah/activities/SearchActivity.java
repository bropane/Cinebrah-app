package com.cinebrah.cinebrah.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.fragments.SearchRoomsFragment;

import timber.log.Timber;

public class SearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener, View.OnFocusChangeListener {

    private final static String SEARCH_FRAGMENT_TAG = "searchfragment";
    private final static String QUERY_KEY = "query";
    private final static String IS_SEARCH_FOCUSED_KEY = "issearchfocused";

    private SearchRoomsFragment searchRoomsFragment;
    private SearchView mSearchView;

    private String mCurrentQuery;
    private boolean isSearchFocused = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mCurrentQuery = savedInstanceState.getString(QUERY_KEY);
            isSearchFocused = savedInstanceState.getBoolean(IS_SEARCH_FOCUSED_KEY, true);
        }

        searchRoomsFragment = (SearchRoomsFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
        if (searchRoomsFragment == null) {
            searchRoomsFragment = SearchRoomsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_search, searchRoomsFragment, SEARCH_FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search_rooms);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setIconified(false);
        if (!isSearchFocused) {
            mSearchView.clearFocus();
        }
        mSearchView.setQuery(mCurrentQuery, false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setOnQueryTextFocusChangeListener(this);

        int searchPlateId = mSearchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = mSearchView.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(getResources().getColor(R.color.cinebrah_red));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (view.getId() == mSearchView.getId()) {
            isSearchFocused = hasFocus;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Timber.d("Searching Rooms: %s", query);
        /*StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.search_action_title)).append(": \"").append(query).append("\"");
        getSupportActionBar().setTitle(sb.toString());  */
        searchRoomsFragment.search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mCurrentQuery = null;
        } else {
            mCurrentQuery = newText;
        }
        return false;
    }

    @Override
    public boolean onClose() {
        invalidateOptionsMenu();

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY_KEY, mCurrentQuery);
        outState.putBoolean(IS_SEARCH_FOCUSED_KEY, isSearchFocused);
    }
}
