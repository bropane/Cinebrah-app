package com.cinebrah.cinebrah.net.models;

/**
 * Created by Taylor on 3/3/2015.
 */
public class RoomConnectionResponse {

    private String room_id;
    private String action;
    private boolean was_successful;

    public String getRoomId() {
        return room_id;
    }

    public String getAction() {
        return action;
    }

    public boolean wasSuccessful() {
        return was_successful;
    }
}
