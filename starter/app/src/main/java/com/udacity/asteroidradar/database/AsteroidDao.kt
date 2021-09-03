package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroids: AsteroidEntity)


    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate ASC")
    fun getByDateBetweenAsLiveData(
        startDate: String,
        endDate: String
    ): LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate = :date ORDER BY closeApproachDate ASC")
    fun getByDateAsLiveData(
        date: String
    ): LiveData<List<AsteroidEntity>>


}