package com.cinebrah.cinebrah.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.activities.drawer.CustomDrawerAdapter;
import com.cinebrah.cinebrah.activities.drawer.DrawerHeader;
import com.cinebrah.cinebrah.activities.drawer.DrawerSelection;
import com.cinebrah.cinebrah.fragments.GetRoomsFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class RoomChooserActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String GET_ROOMS_FRAGMENT_TAG = "getroomsfragment";
    private static final int SIGN_OUT_CODE = 1337;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer)
    ListView mDrawerList;
    CustomDrawerAdapter mDrawerAdapter;
    GetRoomsFragment getRoomsFragment;
    private SignOutHandler mSignOutHandler = new SignOutHandler();
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chooser);
        ButterKnife.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initFragments(savedInstanceState);
        initDrawer();
    }

    private void initFragments(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        getRoomsFragment = (GetRoomsFragment) getSupportFragmentManager().findFragmentByTag(GET_ROOMS_FRAGMENT_TAG);
        if (getRoomsFragment == null) {
            getRoomsFragment = GetRoomsFragment.newInstance();
            Timber.d("New GetRoomsFragment");
            transaction.replace(R.id.fragment_container_room_chooser, getRoomsFragment, GET_ROOMS_FRAGMENT_TAG);
        }
        transaction.commit();
    }

    @Override
    public void onBackStackChanged() {
        int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BaseApplication.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaseApplication.getBus().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_chooser, menu);
        super.onCreateOptionsMenu(menu);
/*
        if (AppConstants.getStoredEmail() != null) {
            menu.add(Menu.NONE, SIGN_OUT_CODE, 99, getString(R.string.sign_out));
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        }
        int id = item.getItemId();
        switch (id) {
            case SIGN_OUT_CODE:
/*                asneFragment.getSocialNetworkManager().getSocialNetwork(GooglePlusSocialNetwork.ID).logout();
                AppConstants.setStoredEmail(null);
                AppConstants.setUserId(null);
                mSignOutHandler.sleep(1000);*/
                break;
            case R.id.menu_search_rooms:
                startActivity(new Intent(this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDrawer() {
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        mDrawerAdapter = new CustomDrawerAdapter(this);
        mDrawerList.setAdapter(mDrawerAdapter);
/*
        if (AppConstants.getStoredEmail() != null) {
            mDrawerAdapter.add(new DrawerSelection(getString(R.string.nav_my_room), getResources().getDrawable(R.drawable.ic_action_home)));
        } else {
            mDrawerAdapter.add(new DrawerSelection(getString(R.string.nav_sign_in), getResources().getDrawable(R.drawable.ic_action_account_circle)));
        }*/
        mDrawerAdapter.add(new DrawerHeader(getString(R.string.nav_browse))); // adding a header to the list
        mDrawerAdapter.add(new DrawerSelection(getString(R.string.nav_popular), getResources().getDrawable(R.drawable.ic_action_trending_up)));
        mDrawerList.setItemChecked(mDrawerAdapter.getCount() - 1, true); //Highlight Popular on init
        mTitle = mDrawerAdapter.getItem(mDrawerAdapter.getCount() - 1).getTitle();
        getSupportActionBar().setTitle(mTitle);
//        dataList.add(new DrawerSelection(getString(R.string.nav_categories),getResources().getDrawable(R.drawable.ic_action_action_list)));
        //mDrawerAdapter.add(new DrawerSelection(getString(R.string.nav_favorites),getResources().getDrawable(R.drawable.ic_action_favorite)));
//        mDrawerAdapter.add(new DrawerSelection(getString(R.string.nav_search),getResources().getDrawable(R.drawable.ic_action_search)));
        mDrawerAdapter.add(new DrawerSelection(getString(R.string.nav_random), getResources().getDrawable(R.drawable.ic_action_theaters)));

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String dItemTitle = mDrawerAdapter.getItem(position).getTitle();
            mTitle = dItemTitle;
            getSupportActionBar().setTitle(mTitle);
            mDrawerLayout.closeDrawer(GravityCompat.START);
            if (dItemTitle == getString(R.string.nav_my_room)) {

            } else if (dItemTitle == getString(R.string.nav_popular)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_room_chooser, getRoomsFragment).commit();
            } else if (dItemTitle == getString(R.string.nav_favorites)) {

            } else if (dItemTitle == getString(R.string.nav_random)) {
//                BaseApplication.getApiService().connectToRoom(AppConstants.getUserId(), "random");
            } else if (dItemTitle == getString(R.string.nav_sign_in)) {

            }
        }
    }

    class SignOutHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(RoomChooserActivity.this, LaunchActivity.class);
            startActivity(intent);
            RoomChooserActivity.this.finish();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }
}
