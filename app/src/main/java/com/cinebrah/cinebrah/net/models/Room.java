package com.cinebrah.cinebrah.net.models;

/**
 * Created by Taylor on 3/2/2015.
 */
public class Room {
    String room_name;
    String room_id;
    int user_count;
    QueuedVideo queued_video;
    String owner;

    public String getRoomId() {
        return room_id;
    }

    public String getRoomName() {
        return room_name;
    }

    public int getUserCount() {
        return user_count;
    }

    public QueuedVideo getCurrentQueuedVideo() {
        return queued_video;
    }

    public String getOwner() {
        return owner;
    }
}
