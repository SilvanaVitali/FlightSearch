package com.example.flightsearch.ui

import com.example.flightsearch.data.Favorite

data class FavoriteUiState(
    val favoriteDetails: FavoriteDetails = FavoriteDetails()
)

data class FavoriteDetails(
    val id: Int = 0,
    val departureCode: String = "",
    val destinationCode: String = "",
)
data class FavoriteFlightsUiState(
    val favoritesList: List<FavoriteDetails> = listOf()
)

fun FavoriteDetails.toFavorite(): Favorite = Favorite(
    id = id,
    departureCode = departureCode,
    destinationCode = destinationCode
)

fun Favorite.toFavoriteDetails(): FavoriteDetails = FavoriteDetails(
    id = id,
    departureCode = departureCode,
    destinationCode = destinationCode
)