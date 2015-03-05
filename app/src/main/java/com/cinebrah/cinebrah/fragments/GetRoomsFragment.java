package com.cinebrah.cinebrah.fragments;

import android.app.Activity;
import android.os.Bundle;

import com.cinebrah.cinebrah.activities.BaseActivity;
import com.cinebrah.cinebrah.net.models.Room;
import com.cinebrah.cinebrah.net.models.RoomsResult;
import com.cinebrah.cinebrah.net.requests.GetRoomsRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import timber.log.Timber;

public class GetRoomsFragment extends RoomsFragment implements RequestListener<RoomsResult> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    GetRoomsRequest getRoomsRequest;
    BaseActivity baseActivity;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRoomsRequest = new GetRoomsRequest(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getRoomAdapter().getCount() == 0) {
//          BaseApplication.getApiService().getRooms(getCurrentPage());
            baseActivity.getSpiceManager().execute(getRoomsRequest, this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
//        BaseApplication.getApiService().getRooms(page);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Timber.e("Could not Load Rooms", spiceException);
    }

    @Override
    public void onRequestSuccess(RoomsResult roomsResult) {
        ArrayList<Room> rooms = (ArrayList<Room>) roomsResult.getResults();
        getRoomAdapter().addAll(rooms);
    }

    /*@Subscribe
    public void onReceivedRooms(ApiServiceOld.GetRoomsEvent event) {
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

   /* @Subscribe
    public void onUpdateRooms(ApiServiceOld.GetInfoForRoomsEvent event) {
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

    }*/

}
