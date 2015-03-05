package com.cinebrah.cinebrah.net.models;

/**
 * Created by Taylor on 3/3/2015.
 */
public class AdvanceVideo {

    private String registration_id;
    private String video_id;

    public AdvanceVideo(String registrationId, String videoId) {
        this.registration_id = registrationId;
        this.video_id = videoId;
    }

    public String getRegistrationId() {
        return registration_id;
    }

    public String getVideoId() {
        return video_id;
    }
}
