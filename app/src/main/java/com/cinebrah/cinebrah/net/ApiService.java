package com.cinebrah.cinebrah.net;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.appspot.cinebrahs.cinebrahApi.CinebrahApi;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesAdvanceQueueRequest;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesChatMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesGcmRegistrationIdMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesGetRoomsInfoMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesQueueVideoMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesQueuedVideoMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesRegisteredUserMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesRoomActionResponseMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesRoomConnectionMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesRoomInfoMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesRoomMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesRoomsMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesSearchRoomsMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesUserDataMessage;
import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesUserMessage;
import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.net.models.QueueVideo;
import com.cinebrah.cinebrah.utils.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Taylor on 9/4/2014.
 */
@SuppressWarnings("ALL")
public class ApiService {

    private static final String LOG_TAG = "ApiService";

    private boolean isInitialized = false;
    private GoogleAccountCredential mCredential;
    private CinebrahApi mService;
    private YoutubeSearcher youtubeSearcher;

    public ApiService() {
        youtubeSearcher = new YoutubeSearcher();
    }

    private void buildApi(GoogleAccountCredential credential) {
        CinebrahApi.Builder builder = new CinebrahApi.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential);
        builder.setRootUrl("http://192.168.0.136:8000/_ah/api");//If testing on local dev server uncomment
        builder.setGoogleClientRequestInitializer(new ApiRequestInitializer());//If testing on local dev server uncomment
        mService = builder.setApplicationName(BaseApplication.getContext().getString(R.string.app_name)).build();
        this.isInitialized = true;
    }

    public void init() {
        buildApi(null);
    }

    public void init(@Nullable String email) {
        mCredential = GoogleAccountCredential.usingAudience(BaseApplication.getContext(), AppConstants.AUDIENCE);
        if (email != null) {
            mCredential.setSelectedAccountName(email);
        }
        buildApi(mCredential);
    }

    public void registerAnon() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                String userId = null;
                try {
                    while (GcmManager.getRegistrationId() == null || GcmManager.getRegistrationId().isEmpty()) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    ApiCinebrahApiMessagesGcmRegistrationIdMessage message = new ApiCinebrahApiMessagesGcmRegistrationIdMessage();
                    message.setRegistrationId(GcmManager.getRegistrationId());
                    CinebrahApi.Users.RegisterAnon registerAnon = mService.users().registerAnon(message);
                    Timber.i("Registering Anonymous User. GCM ID: %s", GcmManager.getRegistrationId());
                    ApiCinebrahApiMessagesUserMessage responseMessage = registerAnon.execute();
                    userId = responseMessage.getUserId();
                } catch (IOException e) {
                    Timber.e(e, "Could not register anonymous user");
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return userId;
            }

            @Override
            protected void onPostExecute(String userId) {
                super.onPostExecute(userId);
                AppConstants.setUserId(userId);
                if (userId != null) {
                    BaseApplication.getBus().post(new RegisterEvent(true, "Registered Successfully"));
                } else {
                    BaseApplication.getBus().post(new RegisterEvent(false, "Failed to Register"));
                }
            }
        }.execute();
    }

    public void register(final String userId) {
        new AsyncTask<Void, Void, UpgradeEvent>() {
            @Override
            protected UpgradeEvent doInBackground(Void... voids) {
                UpgradeEvent event = new UpgradeEvent();
                try {
                    String registrationId = GcmManager.getRegistrationId();
                    ApiCinebrahApiMessagesUserMessage message = new ApiCinebrahApiMessagesUserMessage();
                    message.setUserId(userId);
                    message.setRegistrationId(registrationId);
                    CinebrahApi.Users.Register register = mService.users().register(message);
                    Timber.i("Registering User. User ID: %s GCM ID: %s", userId, registrationId);
                    ApiCinebrahApiMessagesRegisteredUserMessage registeredUserMessage = register.execute();
                    event.setSuccessful(registeredUserMessage.getWasSuccessful());
                    event.setStatus(registeredUserMessage.getStatus());
                } catch (IOException e) {
                    Timber.e(e, "Could not register User ID: %s", userId);
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return event;
            }

            @Override
            protected void onPostExecute(UpgradeEvent event) {
                super.onPostExecute(event);
                BaseApplication.getBus().post(event);
            }
        }.execute();
    }

    public void login(@Nullable final String userId) {
        new AsyncTask<Void, Void, UserDataEvent>() {//TODO Download user data here

            @Override
            protected UserDataEvent doInBackground(Void... voids) {
                UserDataEvent event = new UserDataEvent();
                try {
                    String registrationId = GcmManager.getRegistrationId();
                    ApiCinebrahApiMessagesUserMessage message = new ApiCinebrahApiMessagesUserMessage();
                    message.setUserId(userId);
                    message.setRegistrationId(registrationId);
                    CinebrahApi.Users.Login login = mService.users().login(message);
                    if (userId == null) {
                        Timber.i("Logging in User : %s with GCM ID: %s", AppConstants.getStoredEmail(), registrationId);
                    } else {
                        Timber.i("Logging in User ID: %s with GCM ID: %s", userId, registrationId);
                    }
                    ApiCinebrahApiMessagesUserDataMessage userDataMessage = login.execute();
                    event.setUserId(userDataMessage.getUserId());
                } catch (IOException e) {
                    Timber.e(e, "Could not login with User ID: %s", userId);
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return event;
            }

            @Override
            protected void onPostExecute(UserDataEvent event) {
                super.onPostExecute(event);
                BaseApplication.getBus().post(event);
            }
        }.execute();
    }

    public void getUserData(@Nullable final String userId) {

    }

    public void getRooms(final long page) {
        new AsyncTask<Void, Void, GetRoomsEvent>() {
            @Override
            protected GetRoomsEvent doInBackground(Void... voids) {
                GetRoomsEvent getRoomsEvent = new GetRoomsEvent();
                try {
                    CinebrahApi.Rooms.GetRooms getRooms = mService.rooms().getRooms().setPage(page); // Got a null pointer here
                    Timber.i("Getting rooms on page: %s", page);
                    ApiCinebrahApiMessagesRoomsMessage roomsMessage = getRooms.execute();
                    if (roomsMessage.getRooms() != null) {
                        getRoomsEvent.addRooms((ArrayList<ApiCinebrahApiMessagesRoomMessage>) roomsMessage.getRooms());
                    }
                } catch (IOException e) {
                    Timber.e(e, "Could not get rooms on page: %s", String.valueOf(page));
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return getRoomsEvent;
            }

            @Override
            protected void onPostExecute(GetRoomsEvent getRoomsEvent) {
                super.onPostExecute(getRoomsEvent);
                BaseApplication.getBus().post(getRoomsEvent);
            }
        }.execute();
    }

    public void searchRooms(final String query, final long page) {
        new AsyncTask<Void, Void, SearchRoomsEvent>() {
            @Override
            protected SearchRoomsEvent doInBackground(Void... voids) {
                SearchRoomsEvent event = new SearchRoomsEvent();
                try {
                    ApiCinebrahApiMessagesSearchRoomsMessage message = new ApiCinebrahApiMessagesSearchRoomsMessage();
                    message.setQuery(query).setPage(page);
                    CinebrahApi.Rooms.SearchRooms searchRooms = mService.rooms().searchRooms(message);
                    Timber.i("Searching rooms - query: %s, page : %s", query, String.valueOf(page));
                    ApiCinebrahApiMessagesRoomsMessage roomsMessage = searchRooms.execute();
                    if (roomsMessage.getRooms() != null) {
                        event.addRooms((ArrayList<ApiCinebrahApiMessagesRoomMessage>) roomsMessage.getRooms());
                    }
                } catch (IOException e) {
                    Timber.e(e, "Could not execute search query : %s page: %s", query, String.valueOf(page));
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return event;
            }

            @Override
            protected void onPostExecute(SearchRoomsEvent searchRoomsEvent) {
                super.onPostExecute(searchRoomsEvent);
                BaseApplication.getBus().post(searchRoomsEvent);
            }
        }.execute();
    }

    public void getInfoForRooms(ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms) {
        GetInfoForRoomsEvent event = new GetInfoForRoomsEvent();
        ArrayList<String> roomIds = new ArrayList<>();
        for (ApiCinebrahApiMessagesRoomMessage room : rooms) {
            roomIds.add(room.getRoomId());
        }
        try {
            ApiCinebrahApiMessagesGetRoomsInfoMessage message = new ApiCinebrahApiMessagesGetRoomsInfoMessage();
            message.setRoomIds(roomIds);
            CinebrahApi.Rooms.GetRoomsInfo getInfoForRooms = mService.rooms().getRoomsInfo(message);
            Timber.i("Getting info for rooms");
            ApiCinebrahApiMessagesRoomsMessage roomsMessage = getInfoForRooms.execute();
            if (roomsMessage.getRooms() != null) {
                event.addRooms((ArrayList<ApiCinebrahApiMessagesRoomMessage>) roomsMessage.getRooms());
            }
        } catch (IOException e) {
            Timber.e(e, "Could not get info for rooms");
            BaseApplication.getBus().post(new IOErrorEvent(e));
        }
        BaseApplication.getBus().post(event);
    }

    public void connectToRoom(@Nullable final String userId, final String roomId) {
        new AsyncTask<String, Void, ConnectRoomEvent>() {
            @Override
            protected ConnectRoomEvent doInBackground(String... args) {
                ConnectRoomEvent event = null;
                try {
                    String registrationId = GcmManager.getRegistrationId();
                    ApiCinebrahApiMessagesRoomConnectionMessage message = new ApiCinebrahApiMessagesRoomConnectionMessage();
                    message.setUserId(userId);
                    message.setRoomId(roomId);
                    message.setRegistrationId(registrationId);
                    CinebrahApi.Room.ConnectUser connectUser = mService.room().connectUser(message);
                    Timber.i("Connecting User ID: %s to room: %s", userId, roomId);
                    ApiCinebrahApiMessagesRoomActionResponseMessage responseMessage = connectUser.execute();
                    event = new ConnectRoomEvent(roomId, responseMessage.getStatus(), responseMessage.getIsSuccessful());
                } catch (IOException e) {
                    Timber.e(e, "Could not connect user to room: %s", roomId);
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return event;
            }

            @Override
            protected void onPostExecute(ConnectRoomEvent event) {
                ConnectRoomEvent connectRoomEvent = event;
                if (connectRoomEvent == null) {
                    connectRoomEvent = new ConnectRoomEvent(roomId, "Could not connect to room", false);
                }
                Timber.i(connectRoomEvent.getStatus());
                BaseApplication.getBus().post(connectRoomEvent);
            }
        }.execute();
    }

    public void disconnectFromRoom(@Nullable final String userId, final String roomId) {
        new AsyncTask<Void, Void, DisconnectRoomEvent>() {
            @Override
            protected DisconnectRoomEvent doInBackground(Void... voids) {
                DisconnectRoomEvent event = null;
                try {
                    String registrationId = GcmManager.getRegistrationId();
                    ApiCinebrahApiMessagesRoomConnectionMessage message = new ApiCinebrahApiMessagesRoomConnectionMessage();
                    message.setUserId(userId);
                    message.setRoomId(roomId);
                    message.setRegistrationId(registrationId);
                    CinebrahApi.Room.DisconnectUser disconnectUser = mService.room().disconnectUser(message);
                    Timber.i("Disconnecting User ID: %s from room: %s", userId, roomId);
                    ApiCinebrahApiMessagesRoomActionResponseMessage responseMessage = disconnectUser.execute();
                    event = new DisconnectRoomEvent(responseMessage.getStatus(), responseMessage.getIsSuccessful());
                } catch (IOException e) {
                    Timber.e(e, "Could not disconnect user from room: %s", roomId);
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return event;
            }

            @Override
            protected void onPostExecute(DisconnectRoomEvent event) {
                DisconnectRoomEvent disconnectRoomEvent = event;
                if (disconnectRoomEvent == null) {
                    disconnectRoomEvent = new DisconnectRoomEvent("Could not disconnect from room", false);
                }
                Timber.i(disconnectRoomEvent.getStatus());
                BaseApplication.getBus().post(disconnectRoomEvent);
            }
        }.execute();
    }

    public void getRoomInfo(final String roomId) {
        new AsyncTask<Void, Void, RoomInfoEvent>() {
            @Override
            protected RoomInfoEvent doInBackground(Void... voids) {
                RoomInfoEvent event = null;
                try {
                    CinebrahApi.Room.GetRoomInfo getRoomInfo = mService.room().getRoomInfo(roomId);
                    Timber.i("Retrieving room info for room ID: %s", roomId);
                    ApiCinebrahApiMessagesRoomInfoMessage responseMessage = getRoomInfo.execute();
                    ArrayList<ApiCinebrahApiMessagesQueuedVideoMessage> queuedVideos;
                    if (responseMessage.getQueuedVideoList() == null) {
                        queuedVideos = new ArrayList<>();
                    } else {
                        queuedVideos = (ArrayList<ApiCinebrahApiMessagesQueuedVideoMessage>) responseMessage.getQueuedVideoList();
                    }
                    event = new RoomInfoEvent(responseMessage.getRoomId(), responseMessage.getRoomName(), responseMessage.getCurrentVideoId(),
                            responseMessage.getCurrentVideoDuration(), responseMessage.getCurrentVideoTime(), queuedVideos);
                } catch (IOException e) {
                    Timber.e(e, "Could not get room info for room ID: %s", roomId);
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return event;
            }

            @Override
            protected void onPostExecute(RoomInfoEvent roomInfoEvent) {
                Timber.i("Room Name: %s, Current Video: %s", roomInfoEvent.getRoomName(), roomInfoEvent.getCurrentVideoId());
                BaseApplication.getBus().post(roomInfoEvent);
            }
        }.execute();
    }

    public void queueVideo(final QueueVideo video, final String roomId) {
        new AsyncTask<Void, Void, QueuedVideoEvent>() {
            @Override
            protected QueuedVideoEvent doInBackground(Void... voids) {
                try {
                    ApiCinebrahApiMessagesQueueVideoMessage message = new ApiCinebrahApiMessagesQueueVideoMessage();
                    message.setTitle(video.getVideoTitle())
                            .setVideoId(video.getVideoId())
                            .setChannelTitle(video.getChannelTitle())
                            .setThumbnailUrl(video.getThumbnailUrl())
                            .setDuration(video.getDuration())
                            .setRoomId(roomId);
                    CinebrahApi.Room.QueueVideo queue = mService.room().queueVideo(message);
                    Timber.i("Queuing Video: %s", video.getVideoId());
                    ApiCinebrahApiMessagesRoomActionResponseMessage responseMessage = queue.execute();
                    return new QueuedVideoEvent(responseMessage.getStatus());
                } catch (IOException e) {
                    Timber.e(e, "Could not queue video: %s", video.getVideoId());
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return new QueuedVideoEvent("queueError");
            }

            @Override
            protected void onPostExecute(QueuedVideoEvent queuedVideoEvent) {
                Timber.i("Queued Video: %s", video.getVideoId());
                BaseApplication.getBus().post(queuedVideoEvent);
            }
        }.execute();
    }

    public void requestAdvanceQueue(final String videoId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ApiCinebrahApiMessagesAdvanceQueueRequest request = new ApiCinebrahApiMessagesAdvanceQueueRequest();
                    request.setVideoId(videoId);
                    CinebrahApi.Room.VoteAdvanceQueue advanceQueue = mService.room().voteAdvanceQueue(request);
                    Timber.i("Requested queue to advance video: %s", videoId);
                    advanceQueue.execute();
                } catch (IOException e) {
                    Timber.e(e, "Could not advance video: %s", videoId);
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return null;
            }
        }.execute();
    }

    public void voteSkip(final String videoId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ApiCinebrahApiMessagesAdvanceQueueRequest request = new ApiCinebrahApiMessagesAdvanceQueueRequest();
                    request.setVideoId(videoId);
                    CinebrahApi.Room.VoteSkip voteSkip = mService.room().voteSkip(request);
                    Timber.i("Requested vote skip on video: %s", videoId);
                    voteSkip.execute();
                } catch (IOException e) {
                    Timber.e(e, "Could not skip video: %s", videoId);
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return null;
            }
        }.execute();
    }

    public void sendChatMessage(final String roomId, final String userId, final String message) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ApiCinebrahApiMessagesChatMessage requestMessage = new ApiCinebrahApiMessagesChatMessage();
                    requestMessage.setMessage(message)
                            .setRoomId(roomId)
                            .setUserId(userId);
                    Timber.i("Sending chat message: %s", message);
                    mService.room().sendChatMessage(requestMessage).execute();
                } catch (IOException e) {
                    Timber.e(e, "Could not send chat message: %s", message);
                    BaseApplication.getBus().post(new IOErrorEvent(e));
                    this.cancel(true);
                }
                return null;
            }
        }.execute();
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public YoutubeSearcher getYoutubeSearcher() {
        return youtubeSearcher;
    }

    public static class GetRoomsEvent {
        ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms;

        public GetRoomsEvent() {
            rooms = new ArrayList<ApiCinebrahApiMessagesRoomMessage>();
        }

        public ArrayList<ApiCinebrahApiMessagesRoomMessage> getRooms() {
            return rooms;
        }

        public void addRooms(ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms) {
            this.rooms.addAll(rooms);
        }
    }

    public static class SearchRoomsEvent {
        ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms;

        public SearchRoomsEvent() {
            rooms = new ArrayList<ApiCinebrahApiMessagesRoomMessage>();
        }

        public ArrayList<ApiCinebrahApiMessagesRoomMessage> getRooms() {
            return rooms;
        }

        public void addRooms(ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms) {
            this.rooms.addAll(rooms);
        }
    }

    public static class GetInfoForRoomsEvent {

        ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms;

        public GetInfoForRoomsEvent() {
            rooms = new ArrayList<ApiCinebrahApiMessagesRoomMessage>();
        }

        public ArrayList<ApiCinebrahApiMessagesRoomMessage> getRooms() {
            return rooms;
        }

        public void addRooms(ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms) {
            this.rooms.addAll(rooms);
        }
    }

    public static class IOErrorEvent {
        // Cast to GoogleJsonException to get http code and more details
        IOException exception;

        int code = -1;

        public IOErrorEvent() {

        }

        public IOErrorEvent(IOException e) {
            this.exception = e;
        }

        public IOException getException() {
            return exception;
        }

        public void setException(IOException exception) {
            this.exception = exception;
        }

    }

    public static class RegisterEvent {
        boolean isSuccessful;

        String status;

        public RegisterEvent() {
        }

        public RegisterEvent(boolean isSuccessful, String status) {
            this.isSuccessful = isSuccessful;
            this.status = status;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public void setSuccessful(boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }

    public static class UpgradeEvent {
        boolean isSuccessful;
        String status;

        public UpgradeEvent() {
        }

        public UpgradeEvent(boolean isSuccessful, String status) {
            this.isSuccessful = isSuccessful;
            this.status = status;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public void setSuccessful(boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class UserDataEvent {
        //TODO Downloaded user data goes here (fav channels, following, etc.)
        String userId;

        public UserDataEvent() {
        }

        public UserDataEvent(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class ConnectRoomEvent {
        String status, roomId;
        boolean isSuccessful;

        ConnectRoomEvent(String roomId, String status, boolean isSuccessful) {
            this.roomId = roomId;
            this.status = status;
            this.isSuccessful = isSuccessful;
        }

        public String getRoomId() {
            return roomId;
        }

        public String getStatus() {
            return status;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }
    }

    //Same as ConnectRoomEvent but named differently for readability. U mad?
    public static class DisconnectRoomEvent {
        String status;
        boolean isSuccessful;

        public DisconnectRoomEvent(String status, boolean isSuccessful) {
            this.status = status;
            this.isSuccessful = isSuccessful;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public void setSuccessful(boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
        }
    }

    public static class RoomInfoEvent {
        String roomId, roomName, currentVideoId;
        long duration, currentVideoTime;
        ArrayList<ApiCinebrahApiMessagesQueuedVideoMessage> queuedVideos;

        public RoomInfoEvent() {

        }

        public RoomInfoEvent(String roomId, String roomName, String currentVideoId, long duration,
                             long currentVideoTime, ArrayList<ApiCinebrahApiMessagesQueuedVideoMessage> queuedVideos) {
            this.roomId = roomId;
            this.roomName = roomName;
            this.currentVideoId = currentVideoId;
            this.duration = duration;
            this.currentVideoTime = currentVideoTime;
            this.queuedVideos = queuedVideos;
        }

        public String getRoomId() {
            return roomId;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getCurrentVideoId() {
            return currentVideoId;
        }

        public void setCurrentVideoId(String currentVideoId) {
            this.currentVideoId = currentVideoId;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public long getCurrentVideoTime() {
            return currentVideoTime;
        }

        public void setCurrentVideoTime(long currentVideoTime) {
            this.currentVideoTime = currentVideoTime;
        }

        public List<ApiCinebrahApiMessagesQueuedVideoMessage> getQueuedVideos() {
            return queuedVideos;
        }

        public void setQueuedVideos(ArrayList<ApiCinebrahApiMessagesQueuedVideoMessage> queuedVideos) {
            this.queuedVideos = queuedVideos;
        }
    }

    public static class QueuedVideoEvent {
        String status;

        public QueuedVideoEvent(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    private class ApiRequestInitializer implements GoogleClientRequestInitializer {
        @Override
        public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
            request.setDisableGZipContent(true);
        }
    }

}
