package com.cinebrah.cinebrah.net.models;

/**
 * Created by Taylor on 3/2/2015.
 */
public class QueuedVideo {
    String youtube_id;
    String title;
    int duration;
    String channel_title;
    String user;
    int start_time;
    int queue_time;
    int current_play_time;

    public String getYoutubeId() {
        return youtube_id;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public String getChannelTitle() {
        return channel_title;
    }

    public String getUser() {
        return user;
    }

    public int getStartTime() {
        return start_time;
    }

    public int getQueueTime() {
        return queue_time;
    }

    public int getCurrentPlayTime() {
        return current_play_time;
    }

    public String getDurationText() {
        //Formats duration from seconds into 00:00:00 or 0:00
        int hours = (int) duration / 3600;
        int remainder = (int) duration - hours * 3600;
        int minutes = remainder / 60;
        remainder = remainder - minutes * 60;
        int seconds = remainder;
        if (hours == 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return String.format("%d:%d:%02d", hours, minutes, seconds);
        }
    }
}
