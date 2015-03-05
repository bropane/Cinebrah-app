package com.cinebrah.cinebrah.net.models;

/**
 * Created by Taylor on 9/10/2014.
 */
public class QueueVideoDepreciated {
    String videoId;
    String videoTitle;
    String channelTitle;
    String thumbnailUrl;
    String queuedBy;
    long duration;

    public QueueVideoDepreciated(String videoId, String videoTitle, String channelTitle, String thumbnailUrl, long duration, String queuedBy) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.channelTitle = channelTitle;
        this.thumbnailUrl = thumbnailUrl;
        this.duration = duration;
        this.queuedBy = queuedBy;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public long getDuration() {
        return duration;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getQueuedBy() {
        return queuedBy;
    }

}
