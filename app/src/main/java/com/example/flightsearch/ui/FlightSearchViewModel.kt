package com.example.flightsearch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FlightSearchRepository
import kotlinx.coroutines.flow.Flow

class FlightSearchViewModel(
//    savedStateHandle: SavedStateHandle,
    private val flightRepository: FlightSearchRepository,
) : ViewModel() {

    var favoriteUiState by mutableStateOf(FavoriteUiState())
        private set

    fun getAllAirports(): Flow<List<Airport>> = flightRepository.getAllAirports()
    fun getAllFavorites(): Flow<List<Favorite>> = flightRepository.getAllFavorites()
    fun getAirportBySearch(searchQuery: String): Flow<List<Airport>> =
        flightRepository.getAirportBySearch(searchQuery)

    fun getPossibleFlight(iataCode: String): Flow<List<Airport>> =
        flightRepository.getPossibleFlights(iataCode)

    fun getAirportByCode(iataCode: String): Flow<Airport> =
        flightRepository.getAirportByCode(iataCode)

    fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite> =
        flightRepository.getFavorite(departureCode, destinationCode)
    suspend fun insertFavorite() {
        flightRepository.insertFavorite(favoriteUiState.favoriteDetails.toFavorite())
    }
    suspend fun deleteFavorite() {
        flightRepository.deleteFavorite(favoriteUiState.favoriteDetails.toFavorite())
    }
    fun updateFavoriteUiState(favoriteDetails: FavoriteDetails, isFavorite: Boolean) {
        favoriteUiState = FavoriteUiState(favoriteDetails, isFavorite)
    }
}