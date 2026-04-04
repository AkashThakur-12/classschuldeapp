package com.akash.classschuldeapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ScheduleApiService {
    @GET("api/schedule/{branch}")
    suspend fun getSchedule(@Path("branch") branch: String): List<schulde>
}

object RetrofitClient {
    private val BASE_URL: String
        get() = if (BuildConfig.BASE_URL.isNotBlank()) BuildConfig.BASE_URL else "http://172.70.96.178:3000/"

    val instance: ScheduleApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ScheduleApiService::class.java)
    }
}
