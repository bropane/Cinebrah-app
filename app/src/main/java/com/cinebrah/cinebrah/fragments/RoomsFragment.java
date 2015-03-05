package com.cinebrah.cinebrah.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cinebrah.cinebrah.BaseApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Taylor on 2/18/2015.
 */
public abstract class RoomsFragment extends ListFragment {

    protected int mCurrentPage = 1;
    RoomsScrollListener scrollListener = new RoomsScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            RoomsFragment.this.onLoadMore(page, totalItemsCount);
            mCurrentPage = page;
        }
    };
    protected RoomAdapter roomAdapter;
    private ScheduledExecutorService mScheduleTaskExecutor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomAdapter = new RoomAdapter();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BaseApplication.getBus().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        roomAdapter.setInflater(inflater);
        setListAdapter(roomAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BaseApplication.getBus().unregister(this);
        setListAdapter(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        getListView().setClipToPadding(false);
        getListView().setVerticalScrollBarEnabled(false);
        getListView().setOnScrollListener(scrollListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        roomAdapter.releaseLoaders();
    }

    @Override
    public void onStart() {
        super.onStart();
        mScheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        mScheduleTaskExecutor.scheduleWithFixedDelay(new UpdateTask(), 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onStop() {
        super.onStop();
        mScheduleTaskExecutor.shutdownNow();
    }

   /* @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ApiCinebrahApiMessagesRoomMessage item = roomAdapter.getItem(position);
        Timber.i("Room Id: %s", item.getRoomId());
        BaseApplication.getApiService().connectToRoom(AppConstants.getUserId(), item.getRoomId());
    }*/

    public abstract void onLoadMore(int page, int totalItemsCount);

    public RoomAdapter getRoomAdapter() {
        return roomAdapter;
    }

    /*public void addRooms(ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms) {
        for (ApiCinebrahApiMessagesRoomMessage room : rooms) {
            getRoomAdapter().add(room);
        }
    }*/

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public RoomsScrollListener getScrollListener() {
        return scrollListener;
    }

    protected class UpdateTask implements Runnable {

        @Override
        public void run() {
            /*ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms = scrollListener.getVisibleRooms();
            BaseApplication.getApiService().getInfoForRooms(rooms);*/
        }


    }
}
