package com.nabeel.udhaarkhata.service;

import com.nabeel.udhaarkhata.response.DefaultResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface Service {

    @Headers("-AppInternalNoAuth:true")
    @GET("getOtp")
    Call<DefaultResponse> getOTP(@Query("phoneNo") String grand_type);

}

