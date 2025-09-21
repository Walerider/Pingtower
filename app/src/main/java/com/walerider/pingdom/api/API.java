package com.walerider.pingdom.api;



import com.walerider.pingdom.api.entitys.MessageDTO;
import com.walerider.pingdom.api.entitys.PingtowerResponse;
import com.walerider.pingdom.api.entitys.SiteDTO;
import com.walerider.pingdom.api.entitys.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {
    @GET("/api/sites")
    Call<List<SiteDTO>> getSites();
    @POST("/api/public/check")
    Call<SiteDTO> getSiteInfo(@Body SiteDTO site);
    @POST("api/login")
    Call<UserDTO> login(@Body UserDTO user);
    @POST("api/logout")
    Call<MessageDTO> logout(@Header("Authorization") String token);
    @POST("api/register")
    Call<UserDTO> register(@Body UserDTO user);
    @GET("/api/sites")
    Call<List<SiteDTO>> getUserSites(@Header("Authorization") String token);
    @GET("/api/sites/{id}/stats")
    Call<PingtowerResponse> getSiteStatsByDate(@Header("Authorization") String token, @Path("id")long id, @Query("date_from")String dateFrom, @Query("date_to") String DateTo);
    @GET("/api/sites/{id}/stats")
    Call<PingtowerResponse> getSiteStats(@Header("Authorization") String token, @Path("id")long id);
    @POST("/api/fcm")
    Call<String> sendFcmToken(@Header("Authorization") String token,String FcmToken);
}
