package com.cinebrah.cinebrah.fragments;


import android.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRoomsFragment extends RoomsFragment {

    private static final String KEY_QUERY = "query";

    private String mQuery;

    public SearchRoomsFragment() {

    }

    public static SearchRoomsFragment newInstance() {
        SearchRoomsFragment fragment = new SearchRoomsFragment();
/*        Bundle args = new Bundle();
        args.putString(KEY_QUERY, query);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
//        BaseApplication.getApiService().searchRooms(mQuery, page);
    }

    public void search(String query) {
        mQuery = query;
        if (getRoomAdapter() != null) {
            getRoomAdapter().clear();
        }
//        BaseApplication.getApiService().searchRooms(query, getCurrentPage());
    }

    /*@Subscribe
    public void onReceivedRoomResults(ApiServiceOld.SearchRoomsEvent event) {
        addRooms(event.getRooms());
    }*/

    /*@Subscribe
    public void onConnectedToRoom(ApiServiceOld.ConnectRoomEvent event) {
        Timber.d("Connected to Room Subscribe");
        if (event.isSuccessful()) {
            Intent intent = new Intent(getActivity(), CinemaActivity.class);
            intent.putExtra(CinemaActivity.KEY_ROOM_ID, event.getRoomId());
            getActivity().startActivity(intent);
        }
    }*/
}