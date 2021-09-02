package com.udacity.asteroidradar.repository

import android.content.Context
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.all
import com.udacity.asteroidradar.api.asDatabaseModels
import com.udacity.asteroidradar.api.asDomainModels
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AsteroidRepository(private val database: AsteroidDatabase) {

    /**
     * Represent the saved asteroids given the clean-refresh cycle of the data workers
     */
    val asteroids =
        Transformations.map(
            database.asteroidDao().getByDateBetweenAsLiveData(currentDay(), plusDays(6))
        ) {
            it.asDomainModels()
        }

    val currentDayAsteroids =
        Transformations.map(database.asteroidDao().getByDateAsLiveData(currentDay())) {
            it.asDomainModels()
        }

    val currentWeekAsteroids =
        Transformations.map(
            database.asteroidDao().getByDateBetweenAsLiveData(firstDayOfWeek(), lastDayOfWeek())
        ) {
            it.asDomainModels()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroids =
                AsteroidApi.asteroids.getAsteroids(
                    currentDay(),
                    plusDays(6)
                )

            database.asteroidDao().insertAll(*asteroids.all().asDatabaseModels().toTypedArray())
        }
    }

    suspend fun cleanAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao().deleteByDateBelow(currentDay())
        }
    }

    suspend fun getPictureOfDay(): PictureOfDay {
        return AsteroidApi.pictureOfOfDay.get()
    }

    companion object {
        fun from(appContext: Context): AsteroidRepository {
            return AsteroidRepository(getDatabase(appContext))
        }
    }


}