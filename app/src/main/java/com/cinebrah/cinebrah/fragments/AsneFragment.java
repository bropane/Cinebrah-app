package com.cinebrah.cinebrah.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.googleplus.GooglePlusSocialNetwork;

/**
 * A simple {@link Fragment} subclass.
 */
public class AsneFragment extends Fragment {

    public static final String SOCIAL_NETWORK_TAG = "social_network_manager";
    SocialNetwork socialNetwork;
    private SocialNetworkManager mSocialNetworkManager;

    public AsneFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();
            GooglePlusSocialNetwork googlePlusSocialNetwork = new GooglePlusSocialNetwork(this);
            mSocialNetworkManager.addSocialNetwork(googlePlusSocialNetwork);
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
        } else {
            if (!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
//                mSocialNetworkManager.getSocialNetwork(GooglePlusSocialNetwork.ID).
            }
        }
    }

    public SocialNetworkManager getSocialNetworkManager() {
        return mSocialNetworkManager;
    }
}
