package com.walerider.pingdom.api;



import com.walerider.pingdom.api.entitys.MessageDTO;
import com.walerider.pingdom.api.entitys.SiteDTO;
import com.walerider.pingdom.api.entitys.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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
}
