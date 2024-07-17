package com.example.codeforcesanalyser

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface Apiinterface {

    @GET("/user.rating?handle=Fefer_Ivan")
    fun getData():Call<responseDataClass>

}