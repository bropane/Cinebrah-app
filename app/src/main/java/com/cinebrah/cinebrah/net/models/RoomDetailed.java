package com.cinebrah.cinebrah.net.models;

import java.util.List;

/**
 * Created by Taylor on 3/3/2015.
 */
public class RoomDetailed {
    private String room_name;
    private String room_id;
    private int user_count;
    private List<QueuedVideo> queued_videos;
    private String owner;

    public String getRoomName() {
        return room_name;
    }

    public String getRoomId() {
        return room_id;
    }

    public List<QueuedVideo> getQueuedVideos() {
        return queued_videos;
    }

    public int getUserCount() {
        return user_count;
    }

    public String getOwner() {
        return owner;
    }
}
