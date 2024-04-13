package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface FlightSearchDao {

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    fun getAllAirports(): Flow<List<Airport>>

    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Query("SELECT * FROM airport WHERE iata_code LIKE '%' || :searchQuery || '%' OR name LIKE '%' || :searchQuery || '%' ORDER BY passengers DESC")
    fun getAirportBySearch(searchQuery: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code != :iataCode ORDER BY passengers DESC")
    fun getPossibleFlights(iataCode: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    fun getAirportByCode(iataCode: String): Flow<Airport>

    @Query("SELECT * FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)
}