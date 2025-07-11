package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GeminiApiService {
    @Headers("Content-Type: application/json")
    @POST
    Call<GeminiResponse> generateContent(@Url String url, @Body GeminiRequest request);
}