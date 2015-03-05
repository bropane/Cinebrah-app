package com.cinebrah.cinebrah.net;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.net.models.QueueVideoDepreciated;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Taylor on 9/5/2014.
 */
public class GcmIntentService extends IntentService {

    private static final String LOG_TAG = "GcmIntentService";

    private static final String KEY_ACTION = "action";

    private static final String NEXT_VIDEO_ACTION = "nextVideo";
    private static final String CHAT_ACTION = "chat";
    private static final String NEW_QUEUED_VIDEO_ACTION = "newQueuedVideo";
    private static final String USER_COUNT_CHANGE = "user_count_change";

    private static final String KEY_YOUTUBE_ID = "youtube_id";
    private static final String KEY_CHAT_MESSAGE_TYPE = "chat_message_type";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_MESSAGE_SENDER = "sender";
    private static final String KEY_VIDEO_TITLE = "title";
    private static final String KEY_VIDEO_CHANNEL_TITLE = "channel_title";
    private static final String KEY_VIDEO_DURATION = "duration";
    private static final String KEY_VIDEO_THUMBNAIL = "thumbnail";
    private static final String KEY_VIDEO_QUEUED_BY = "queued_by";
    private static final String KEY_USER_COUNT = "user_count";


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        Log.d(LOG_TAG, "Received GCM Message");
        if (!extras.isEmpty()) {
            if (messageType != null && messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
                if (extras.getString(KEY_ACTION) != null) {
                    String action = extras.getString(KEY_ACTION);
                    Log.i(LOG_TAG, "Action: " + action);
                    if (action.equals(NEXT_VIDEO_ACTION)) {
                        BaseApplication.getBus().post(new NextVideoEvent(extras.getString(KEY_YOUTUBE_ID, null)));
                        Log.i(LOG_TAG, action + " : " + extras.getString(KEY_YOUTUBE_ID, null));
                    } else if (action.equals(CHAT_ACTION)) {
                        BaseApplication.getBus().post(new ChatMessageEvent(extras.getString(KEY_MESSAGE),
                                extras.getString(KEY_CHAT_MESSAGE_TYPE), extras.getString(KEY_MESSAGE_SENDER)));
                        Log.i(LOG_TAG, action + "\nSender: " + extras.getString(KEY_MESSAGE_SENDER)
                                + "\nMessage Type: " + extras.getString(KEY_CHAT_MESSAGE_TYPE)
                                + "\nMessage: " + extras.getString(KEY_MESSAGE));
                    } else if (action.equals(NEW_QUEUED_VIDEO_ACTION)) {
                        QueueVideoDepreciated video = new QueueVideoDepreciated(null, extras.getString(KEY_VIDEO_TITLE),
                                extras.getString(KEY_VIDEO_CHANNEL_TITLE), extras.getString(KEY_VIDEO_THUMBNAIL),
                                Integer.parseInt(extras.getString(KEY_VIDEO_DURATION)), extras.getString(KEY_VIDEO_QUEUED_BY));
                        BaseApplication.getBus().post(new NewVideoQueuedEvent(video));
                        Log.i(LOG_TAG, "Video: " + extras.getString(KEY_VIDEO_TITLE) + " added to queue");
                    } else if (action.equals(USER_COUNT_CHANGE)) {
                        BaseApplication.getBus().post(new UserCountChangeEvent(extras.getString(KEY_USER_COUNT)));
                        Log.i(LOG_TAG, "User Count: " + extras.getString(KEY_USER_COUNT));
                    }
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public static class NextVideoEvent {
        String youtubeId;

        public NextVideoEvent(String youtubeId) {
            this.youtubeId = youtubeId;
        }

        public String getYoutubeId() {
            return youtubeId;
        }
    }

    public static class ChatMessageEvent {
        String chatMessage, sender, messageType;

        public ChatMessageEvent(String chatMessage, String messageType, String sender) {
            this.chatMessage = chatMessage;
            this.messageType = messageType;
            this.sender = sender;
        }

        public String getChatMessage() {
            return chatMessage;
        }

        public String getMessageType() {
            return messageType;
        }

        public String getSender() {
            return sender;
        }
    }

    public static class NewVideoQueuedEvent {
        QueueVideoDepreciated video;

        public NewVideoQueuedEvent(QueueVideoDepreciated video) {
            this.video = video;
        }

        public QueueVideoDepreciated getVideo() {
            return video;
        }
    }

    public static class UserCountChangeEvent {
        String userCount;

        public UserCountChangeEvent(String userCount) {
            this.userCount = userCount;
        }

        public String getUserCount() {
            return userCount;
        }
    }
}
