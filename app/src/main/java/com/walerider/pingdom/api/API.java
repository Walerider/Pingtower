package com.walerider.pingdom.api;



import com.walerider.pingdom.api.entitys.SiteDTO;
import com.walerider.pingdom.api.entitys.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {
    @GET("/api/sites")
    Call<List<SiteDTO>> getSites();
    @POST("api/login")
    Call<UserDTO> login(@Body UserDTO user);
    @POST("api/register")
    Call<UserDTO> register(@Body UserDTO user);

}
