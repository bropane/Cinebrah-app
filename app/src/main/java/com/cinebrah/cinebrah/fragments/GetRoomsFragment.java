package com.cinebrah.cinebrah.fragments;

import android.content.Intent;
import android.os.Bundle;

import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesRoomMessage;
import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.activities.CinemaActivity;
import com.cinebrah.cinebrah.net.ApiService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import timber.log.Timber;

public class GetRoomsFragment extends RoomsFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GetRoomsFragment() {
    }

    // TODO: Rename and change types of parameters
    public static GetRoomsFragment newInstance() {
        GetRoomsFragment fragment = new GetRoomsFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getRoomAdapter().getCount() == 0) {
            BaseApplication.getApiService().getRooms(getCurrentPage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        BaseApplication.getApiService().getRooms(page);
    }

    @Subscribe
    public void onReceivedRooms(ApiService.GetRoomsEvent event) {
        addRooms(event.getRooms());

    }

    @Subscribe
    public void onConnectedToRoom(ApiService.ConnectRoomEvent event) {
        Timber.d("Connected to Room Subscribe");
        if (event.isSuccessful()) {
            Intent intent = new Intent(getActivity(), CinemaActivity.class);
            intent.putExtra(CinemaActivity.KEY_ROOM_ID, event.getRoomId());
            getActivity().startActivity(intent);
        }
    }

    @Subscribe
    public void onUpdateRooms(ApiService.GetInfoForRoomsEvent event) {
        ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms = event.getRooms();
        for (ApiCinebrahApiMessagesRoomMessage room : rooms) {
            ApiCinebrahApiMessagesRoomMessage rm = getRoomAdapter().getItem(room.getRoomId());
            rm.setRoomName(room.getRoomName())
                    .setUserCount(room.getUserCount())
                    .setVideoId(room.getVideoId())
                    .setVideoName(room.getVideoName());
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getRoomAdapter().notifyDataSetChanged();
            }
        });

    }

}
