package com.cinebrah.cinebrah.net;

import com.cinebrah.cinebrah.net.models.AccountCredential;
import com.cinebrah.cinebrah.net.models.AdvanceVideo;
import com.cinebrah.cinebrah.net.models.QueuedVideoResponse;
import com.cinebrah.cinebrah.net.models.QueuingVideo;
import com.cinebrah.cinebrah.net.models.Registered;
import com.cinebrah.cinebrah.net.models.RegistrationId;
import com.cinebrah.cinebrah.net.models.RoomActionResponse;
import com.cinebrah.cinebrah.net.models.RoomConnectionResponse;
import com.cinebrah.cinebrah.net.models.RoomDetailed;
import com.cinebrah.cinebrah.net.models.RoomsResult;
import com.cinebrah.cinebrah.net.models.SentMessage;
import com.cinebrah.cinebrah.net.models.Token;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Taylor on 3/2/2015.
 */
public interface CinebrahEndpoints {

    @GET("/rooms?format=json")
    RoomsResult getRooms(@Query("page") int page);

    @GET("/rooms/{roomId}?format=json")
    RoomDetailed getRoomDetailed(@Path("roomId") String roomId);

    @POST("/rooms/{roomId}/connect?format=json")
    RoomConnectionResponse connectToRoom(@Path("roomId") String roomId,
                                         @Body RegistrationId registrationId);

    @POST("/rooms/{roomId}/connect?format=json")
    RoomConnectionResponse connectToRoom(@Path("roomId") String roomId,
                                         @Body RegistrationId registrationId,
                                         @Header("Authorization") String formattedToken);

    @POST("/rooms/random/connect?format=json")
    RoomConnectionResponse connectToRandomRoom(@Body RegistrationId registrationId);

    @POST("/rooms/random/connect?format=json")
    RoomConnectionResponse connectToRandomRoom(@Body RegistrationId registrationId,
                                               @Header("Authorization") String formattedToken);

    @POST("/rooms/{roomId}/disconnect?format=json")
    RoomConnectionResponse disconnectFromRoom(@Path("roomId") String roomId,
                                              @Body RegistrationId registrationId);

    @POST("/rooms/{roomId}/disconnect?format=json")
    RoomConnectionResponse disconnectFromRoom(@Path("roomId") String roomId,
                                              @Body RegistrationId registrationId,
                                              @Header("Authorization") String formattedToken);

    @POST("/rooms/{roomId}/advance?format=json")
    RoomActionResponse requestAdvanceCurrentVideo(@Path("roomId") String roomId,
                                                  @Body AdvanceVideo advanceVideo);

    @POST("/rooms/{roomId}/advance?format=json")
    RoomActionResponse requestAdvanceCurrentVideo(@Path("roomId") String roomId,
                                                  @Body AdvanceVideo advanceVideo,
                                                  @Header("Authorization") String formattedToken);

    @POST("/rooms/{roomId}/skip?format=json")
    RoomActionResponse requestSkipCurrentVideo(@Path("roomId") String roomId,
                                               @Body AdvanceVideo advanceVideo);

    @POST("/rooms/{roomId}/skip?format=json")
    RoomActionResponse requestSkipCurrentVideo(@Path("roomId") String roomId,
                                               @Body AdvanceVideo advanceVideo,
                                               @Header("Authorization") String formattedToken);

    @POST("/rooms/{roomId}/queue?format=json")
    QueuedVideoResponse queueVideo(@Path("roomId") String roomId, @Body QueuingVideo video);

    @POST("/rooms/{roomId}/queue?format=json")
    QueuedVideoResponse queueVideo(@Path("roomId") String roomId, @Body QueuingVideo video,
                                   @Header("Authorization") String formattedToken);

    @POST("/rooms/{roomId}/chat?format=json")
    void sendChatMessage(@Path("roomId") String roomId, @Body SentMessage message,
                         @Header("Authorization") String formattedToken);

    @POST("/auth/login")
    Token login(@Body AccountCredential credential);

    @POST("/auth/logout")
    void logout(@Header("Authorization") String formattedToken);

    @POST("/auth/register")
    Registered register(@Body AccountCredential credential);


}
