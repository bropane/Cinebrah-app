package com.cinebrah.cinebrah.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cinebrah.cinebrah.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CinemaFragment extends Fragment {

    private static final String LOG_TAG = "CinemaFragment";

    ChatFragment chatFragment;
    QueuedVideosFragment queuedVideosFragment;

    boolean isQueueListRevealed = false;

    public CinemaFragment() {
        chatFragment = ChatFragment.newInstance();
        queuedVideosFragment = QueuedVideosFragment.newInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cinema, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.layout_cinema_fragment_container, chatFragment).commit();

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.button_queue_list)
    void openQueueList() {
        revealQueueList(isQueueListRevealed);
    }

    /*@SuppressWarnings("unused")
    @OnClick(R.id.button_next)
    void skipVideo() {
        if (currentRoomInfo.getCurrentVideoId() != null) {
            BaseApplication.getApiService().voteSkip(currentRoomInfo.getCurrentVideoId());
            Crouton.makeText(getActivity(), R.string.vote_skip,
                    Style.INFO, R.id.cinema_fragment_container).show();
        } else {
            Crouton.makeText(getActivity(), R.string.no_videos_vote_skip,
                    Style.INFO, R.id.cinema_fragment_container).show();
        }
    }*/

    public void revealQueueList(boolean isRevealed) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (isRevealed) {
            ft.replace(R.id.layout_cinema_fragment_container, chatFragment);
            ft.commit();
        } else {
            ft.replace(R.id.layout_cinema_fragment_container, queuedVideosFragment);
            ft.commit();
        }
        isQueueListRevealed = !isQueueListRevealed;
    }


}
