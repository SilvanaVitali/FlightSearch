package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface FlightSearchDao {

    @Query("SELECT * FROM airport ORDER BY iata_code ASC")
    fun getAllAirports(): Flow<List<Airport>>

    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Query("SELECT * FROM airport WHERE iata_code LIKE '%:searchQuery%' OR name LIKE '%' || :searchQuery || '%'")
    fun getAirportBySearch(searchQuery: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code != :iataCode")
    fun getPossibleFlights(iataCode: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    fun getAirportByCode(iataCode: String): Flow<Airport>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: List<Favorite>)
}