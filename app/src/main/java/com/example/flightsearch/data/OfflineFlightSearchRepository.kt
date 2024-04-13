package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class OfflineFlightSearchRepository(private val flightSearchDao: FlightSearchDao) : FlightSearchRepository  {
    override fun getAllAirports(): Flow<List<Airport>> = flightSearchDao.getAllAirports()

    override fun getAllFavorites(): Flow<List<Favorite>> = flightSearchDao.getAllFavorites()

    override fun getAirportBySearch(searchQuery: String): Flow<List<Airport>> = flightSearchDao.getAirportBySearch(searchQuery)

    override fun getPossibleFlights(iataCode: String): Flow<List<Airport>> = flightSearchDao.getPossibleFlights(iataCode)

    override fun getAirportByCode(iataCode: String): Flow<Airport> = flightSearchDao.getAirportByCode(iataCode)

    override fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite> = flightSearchDao.getFavorite(departureCode, destinationCode)

    override suspend fun insertFavorite(favorite: Favorite) = flightSearchDao.insertFavorite(favorite)

    override suspend fun deleteFavorite(favorite: Favorite) = flightSearchDao.deleteFavorite(favorite)
}