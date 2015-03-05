package com.cinebrah.cinebrah.net.models;

/**
 * Created by Taylor on 3/3/2015.
 */
public class SentMessage {
    private String registration_id;
    private String message;

    public SentMessage(String registrationId, String message) {
        this.registration_id = registrationId;
        this.message = message;
    }

    public String getRegistrationId() {
        return registration_id;
    }

    public void setRegistrationId(String registrationId) {
        this.registration_id = registration_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
