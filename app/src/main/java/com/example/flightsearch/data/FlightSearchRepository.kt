package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {
    fun getAllAirports(): Flow<List<Airport>>

    fun getAllFavorites(): Flow<List<Favorite>>

    fun getAirportBySearch(searchQuery: String): Flow<List<Airport>>

    fun getPossibleFlights(iataCode: String): Flow<List<Airport>>

    fun getAirportByCode(iataCode:String): Flow<Airport>

    fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite>

    suspend fun insertFavorite(favorite: Favorite)

    suspend fun deleteFavorite(favorite: Favorite)
}