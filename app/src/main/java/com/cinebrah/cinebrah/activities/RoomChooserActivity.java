package com.cinebrah.cinebrah.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.fragments.GetRoomsFragment;
import com.cinebrah.cinebrah.utils.AppConstants;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class RoomChooserActivity extends BaseActivity {

    private static final String GET_ROOMS_FRAGMENT_TAG = "getroomsfragment";
    private static final int SIGN_OUT_CODE = 1337;

    private static final int NAV_POPULAR_ID = 1;
    private static final int NAV_CREATE_ROOM_ID = 2;
    private static final int NAV_JOIN_RANDOM_ID = 3;
    private static final int NAV_SETTINGS_ID = 4;
    private static final int NAV_ABOUT_ID = 5;
    Drawer.OnDrawerItemClickListener drawerListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
            if (drawerItem != null) {
                int identifier = drawerItem.getIdentifier();
                switch (identifier) {
                    case NAV_POPULAR_ID:
                        handlePopular();
                        break;
                    case NAV_CREATE_ROOM_ID:
                        handleCreateRoom();
                        break;
                    case NAV_JOIN_RANDOM_ID:
                        handleJoinRandom();
                        break;
                    case NAV_SETTINGS_ID:
                        handleSettings();
                        break;
                    case NAV_ABOUT_ID:
                        handleAbout();
                        break;
                }
            }
        }
    };
    GetRoomsFragment getRoomsFragment;
    Drawer.Result drawer;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    ActionBarDrawerToggle mActionBarToggle;
    private SignOutHandler mSignOutHandler = new SignOutHandler();
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chooser);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        initFragments(savedInstanceState);
        initDrawer(savedInstanceState);
    }

    private void initFragments(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getRoomsFragment = (GetRoomsFragment) getSupportFragmentManager().findFragmentByTag(GET_ROOMS_FRAGMENT_TAG);
        if (getRoomsFragment == null) {
            getRoomsFragment = GetRoomsFragment.newInstance();
            Timber.d("New GetRoomsFragment");
        }
        transaction.replace(R.id.fragment_container_room_chooser, getRoomsFragment, GET_ROOMS_FRAGMENT_TAG);
        transaction.commit();
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

    void initDrawer(Bundle savedInstanceState) {
        drawer = new Drawer()
                .withActivity(this)
                .withTranslucentActionBarCompatibility(false)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.nav_popular).withIcon(R.drawable.nav_popular).withIdentifier(1),
                        new PrimaryDrawerItem().withName(getString(R.string.nav_create_room)).withIcon(R.drawable.nav_create_room).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.nav_random).withIcon(R.drawable.nav_random).withIdentifier(3),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.action_settings).withIcon(R.drawable.nav_settings).withIdentifier(4),
                        new SecondaryDrawerItem().withName(getString(R.string.nav_about)).withIcon(R.drawable.nav_about).withIdentifier(5)
                ).withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(drawerListener)
                .build();
    }

    private void handlePopular() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getRoomsFragment = (GetRoomsFragment) getSupportFragmentManager().findFragmentByTag(GET_ROOMS_FRAGMENT_TAG);
        if (getRoomsFragment == null) {
            getRoomsFragment = GetRoomsFragment.newInstance();
        }
        transaction.replace(R.id.fragment_container_room_chooser, getRoomsFragment, GET_ROOMS_FRAGMENT_TAG);
        transaction.commit();
    }

    private void handleCreateRoom() {

    }

    private void handleJoinRandom() {

    }

    private void handleSettings() {
        // TODO Settings activity
    }

    private void handleAbout() {
        AppConstants.startAboutActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_chooser, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_search_rooms:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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
