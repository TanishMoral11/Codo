package com.example.codeforcesanalyser

import responseDataClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Apiinterface {
    @GET("user.rating")
    fun getData(@Query("handle") handle: String): Call<responseDataClass>
}