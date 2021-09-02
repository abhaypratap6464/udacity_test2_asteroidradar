package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidFilter
import com.udacity.asteroidradar.api.PictureOfDayStatus
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private var asteroidRepository: AsteroidRepository
) : ViewModel() {

    // Internally, we use a MutableLiveData to handle navigation to the selected asteroid
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the asteroid
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    // Internally, we use a MediatorLiveData, because we will be updating the List of Asteroid
    // with new values
    //https://stackoverflow.com/questions/50943919/convert-livedata-to-mutablelivedata
    private var _asteroid = MediatorLiveData<List<Asteroid>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val asteroid: LiveData<List<Asteroid>>
        get() = _asteroid

    private val _pictureOfDayStatus = MutableLiveData<PictureOfDayStatus>()
    val pictureOfDayStatus: LiveData<PictureOfDayStatus>
        get() = _pictureOfDayStatus

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private var asteroidFilter = AsteroidFilter.SAVED


    init {
        loadPictureOfDay()
        refreshAsteroids()
        showAsteroid()

    }

    private fun showAsteroid() {
        _asteroid.addSource(asteroidRepository.asteroids) {
            if (asteroidFilter == AsteroidFilter.SAVED) {
                _asteroid.value = it
            }
        }

        _asteroid.addSource(asteroidRepository.currentWeekAsteroids) {
            if (asteroidFilter == AsteroidFilter.CURRENT_WEEK) {
                _asteroid.value = it
            }
        }

        _asteroid.addSource(asteroidRepository.currentDayAsteroids) {
            if (asteroidFilter == AsteroidFilter.CURRENT_DAY) {
                _asteroid.value = it
            }
        }
    }

    private fun refreshAsteroids() {
        viewModelScope.launch {
            try {
                asteroidRepository.refreshAsteroids()
            } catch (e: Exception) {
                e.printStackTrace()
                e.localizedMessage
            }

        }
    }

    private fun loadPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDayStatus.value = PictureOfDayStatus.LOADING

                _pictureOfDay.value = asteroidRepository.getPictureOfDay()

                _pictureOfDayStatus.value = PictureOfDayStatus.DONE

            } catch (e: Exception) {
                _pictureOfDayStatus.value = PictureOfDayStatus.ERROR

            }
        }
    }


    fun updateFilter(filter: AsteroidFilter) {
        asteroidFilter = filter
        when (asteroidFilter) {
            AsteroidFilter.SAVED -> asteroidRepository.asteroids.let {
                _asteroid.value = it.value
            }
            AsteroidFilter.CURRENT_WEEK -> asteroidRepository.currentWeekAsteroids.let {
                _asteroid.value = it.value
            }
            AsteroidFilter.CURRENT_DAY -> asteroidRepository.currentDayAsteroids.let {
                _asteroid.value = it.value
            }
        }
    }


}