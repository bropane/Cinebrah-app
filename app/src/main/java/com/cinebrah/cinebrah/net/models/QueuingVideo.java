package com.cinebrah.cinebrah.net.models;

/**
 * Created by Taylor on 3/3/2015.
 */
public class QueuingVideo {
    String registration_id;
    String video_id;
    String video_title;
    String channel_title;
    int duration;

    public QueuingVideo(String registrationId, String videoId, String videoTitle, String channelTitle, int duration) {
        this.registration_id = registrationId;
        this.video_id = videoId;
        this.video_title = videoTitle;
        this.channel_title = channelTitle;
        this.duration = duration;
    }

    public String getRegistrationId() {
        return registration_id;
    }

    public String getVideoTitle() {
        return video_title;
    }

    public String getVideoId() {
        return video_id;
    }

    public String getChannelTitle() {
        return channel_title;
    }

    public int getDuration() {
        return duration;
    }
}
