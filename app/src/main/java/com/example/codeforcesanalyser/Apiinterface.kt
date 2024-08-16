package com.example.codeforcesanalyzer


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("user.rating")
    fun getRatingData(@Query("handle") handle: String): Call<responseDataClass>

    @GET("user.info")
    fun getUserInfo(@Query("handles") handle: String): Call<UserInfoResponse>
}
