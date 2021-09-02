package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

enum class PictureOfDayStatus {
    LOADING, DONE, ERROR
}

enum class AsteroidFilter {
    CURRENT_DAY, CURRENT_WEEK, SAVED
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") start: String?,
        @Query("end_date") end: String?,
        @Query("api_key") token: String = API_KEY,
    ): AsteroidContainer
}

interface PictureOfDayApiService {
    @GET("planetary/apod")
    suspend fun get(@Query("api_key") apiKey: String = API_KEY): PictureOfDay
}


object AsteroidApi {
    val asteroids: AsteroidApiService = retrofit.create(AsteroidApiService::class.java)

    val pictureOfOfDay: PictureOfDayApiService = retrofit.create(PictureOfDayApiService::class.java)
}
