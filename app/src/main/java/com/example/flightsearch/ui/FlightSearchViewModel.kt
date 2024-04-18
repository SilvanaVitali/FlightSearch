package com.example.flightsearch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.FlightSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlightSearchViewModel(
    private val flightRepository: FlightSearchRepository,
) : ViewModel() {

    var favoriteUiState by mutableStateOf(FavoriteUiState())
        private set

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val favoritesListUiState: StateFlow<FavoriteFlightsUiState> =
        flightRepository.getAllFavorites().map { favoriteList ->
            FavoriteFlightsUiState(favoriteList.map { it.toFavoriteDetails() })
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = FavoriteFlightsUiState()
        )

    fun getAirportBySearch(searchQuery: String): Flow<List<Airport>> =
        flightRepository.getAirportBySearch(searchQuery)

    fun getPossibleFlight(iataCode: String): Flow<List<Airport>> =
        flightRepository.getPossibleFlights(iataCode)

    fun getAirportByCode(iataCode: String): Flow<Airport> =
        flightRepository.getAirportByCode(iataCode)

    suspend fun insertFavorite() {
        flightRepository.insertFavorite(favoriteUiState.favoriteDetails.toFavorite())
    }

    suspend fun deleteFavorite() {
        flightRepository.deleteFavoriteByCode(
            favoriteUiState.favoriteDetails.departureCode,
            favoriteUiState.favoriteDetails.destinationCode
        )
    }

    fun updateFavorite(flightDetails: FavoriteDetails, isFavorite: Boolean) {
        favoriteUiState = FavoriteUiState(favoriteDetails = flightDetails)
        viewModelScope.launch(Dispatchers.IO) {
            if (isFavorite) {
                deleteFavorite()

            } else {
                insertFavorite()
            }
        }
    }

}