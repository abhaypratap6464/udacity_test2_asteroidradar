package com.udacity.asteroidradar.api


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.database.AsteroidEntity
import com.udacity.asteroidradar.domain.Asteroid

@JsonClass(generateAdapter = true)
data class AsteroidContainer(
    @Json(name = "near_earth_objects")
    val asteroids: Map<String, List<NetworkAsteroid>>
)

@JsonClass(generateAdapter = true)
data class NetworkAsteroid(
    val id: Long,

    @Json(name = "name")
    val codename: String,

    @Json(name = "absolute_magnitude_h")
    val absoluteMagnitude: Double,

    @Json(name = "estimated_diameter")
    val estimatedDiameter: EstimatedDiameter,

    @Json(name = "close_approach_data")
    val closeApproachData: List<CloseApproachData>,

    @Json(name = "is_potentially_hazardous_asteroid")
    val isPotentiallyHazardous: Boolean
)

@JsonClass(generateAdapter = true)
data class CloseApproachData(

    @Json(name = "close_approach_date")
    val closeApproachDate: String,

    @Json(name = "relative_velocity")
    val relativeVelocity: RelativeVelocity,

    @Json(name = "miss_distance")
    val missDistance: MissDistance
)

@JsonClass(generateAdapter = true)
data class RelativeVelocity(@Json(name = "kilometers_per_second") val kilometersPerSecond: Double)

@JsonClass(generateAdapter = true)
data class MissDistance(val astronomical: Double)

@JsonClass(generateAdapter = true)
data class EstimatedDiameter(@Json(name = "kilometers") val diameter: EstimatedDiameterMinMax)

@JsonClass(generateAdapter = true)
data class EstimatedDiameterMinMax(

    @Json(name = "estimated_diameter_min")
    val min: Double,

    @Json(name = "estimated_diameter_max")
    val max: Double
)


fun AsteroidContainer.all(): List<Asteroid> {
    return asteroids.values.flatMap { it.asDomainModels() }
}

fun List<AsteroidEntity>.asDomainModels(): List<Asteroid> {
    return map {
        it.asDomainModel()
    }
}

fun List<Asteroid>.asDatabaseModels(): List<AsteroidEntity> {
    return map {
        it.asDatabaseModel()
    }
}

@JvmName("asDomainModelsNetworkAsteroid")
fun List<NetworkAsteroid>.asDomainModels(): List<Asteroid> {
    return map {
        it.asDomainModel()
    }
}

@JvmName("asDatabaseModelsNetworkAsteroid")
fun List<NetworkAsteroid>.asDatabaseModels(): List<AsteroidEntity> {
    return map {
        it.asDatabaseModel()
    }
}


fun AsteroidEntity.asDomainModel(): Asteroid {
    return Asteroid(
        id = id,
        codename = codename,
        closeApproachDate = closeApproachDate,
        absoluteMagnitude = absoluteMagnitude,
        estimatedDiameter = estimatedDiameter,
        relativeVelocity = relativeVelocity,
        distanceFromEarth = distanceFromEarth,
        isPotentiallyHazardous = isPotentiallyHazardous
    )
}

fun Asteroid.asDatabaseModel(): AsteroidEntity {
    return AsteroidEntity(
        id = id,
        codename = codename,
        closeApproachDate = closeApproachDate,
        absoluteMagnitude = absoluteMagnitude,
        estimatedDiameter = estimatedDiameter,
        relativeVelocity = relativeVelocity,
        distanceFromEarth = distanceFromEarth,
        isPotentiallyHazardous = isPotentiallyHazardous
    )
}

fun NetworkAsteroid.asDomainModel(): Asteroid {
    return Asteroid(
        id = id,
        codename = codename,
        closeApproachDate = closeApproachData[0].closeApproachDate,
        absoluteMagnitude = absoluteMagnitude,
        estimatedDiameter = estimatedDiameter.diameter.max,
        relativeVelocity = closeApproachData[0].relativeVelocity.kilometersPerSecond,
        distanceFromEarth = closeApproachData[0].missDistance.astronomical,
        isPotentiallyHazardous = isPotentiallyHazardous
    )
}

fun NetworkAsteroid.asDatabaseModel(): AsteroidEntity {
    return AsteroidEntity(
        id = id,
        codename = codename,
        closeApproachDate = closeApproachData[0].closeApproachDate,
        absoluteMagnitude = absoluteMagnitude,
        estimatedDiameter = estimatedDiameter.diameter.max,
        relativeVelocity = closeApproachData[0].relativeVelocity.kilometersPerSecond,
        distanceFromEarth = closeApproachData[0].missDistance.astronomical,
        isPotentiallyHazardous = isPotentiallyHazardous
    )
}



