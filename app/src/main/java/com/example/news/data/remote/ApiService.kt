package com.example.news.data.remote

import com.example.news.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v2/everything?apiKey=8dbc5221249b46e39f02b96ced9d7cc6")
    suspend fun getArticles(
        @Query("q") topic: String,
        @Query("language") language: String = "en"
    ): Response<ResponseDto>


    @GET("v2/top-headlines?apiKey=8dbc5221249b46e39f02b96ced9d7cc6")
    suspend fun getHeadlines(
        @Query("language") language: String = "en"
    ): Response<ResponseDto>



}