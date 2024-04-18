package com.example.flightsearch.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flightsearch.AppViewModelProvider
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.ui.theme.FlightSearchTheme
import kotlinx.coroutines.flow.Flow

enum class FlightSearchScreens {
    HomeFlight,
    FlightList
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp(
    viewModel: FlightSearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val navController = rememberNavController()
    val favoritesListUiState by viewModel.favoritesListUiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            var query by rememberSaveable { mutableStateOf("") }
            var active by rememberSaveable { mutableStateOf(false) }
            val airportsSearch by viewModel.getAirportBySearch(query).collectAsState(emptyList())
            val updateFavorite = viewModel::updateFavorite
            val onSearch: (String) -> Unit = { active = false }
            SearchBar(
                query = query,
                placeholder = { Text(text = "Enter departure airport") },
                onQueryChange = { query = it },
                onSearch = onSearch,
                active = active,
                onActiveChange = { active = it },
                trailingIcon = {
                    IconButton(
                        onClick = { onSearch(query) },
                        enabled = query.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            ) {
                if (query.isNotEmpty()) {
                    val filteredAirports = airportsSearch
                    LazyColumn {
                        items(
                            items = filteredAirports,
                            key = { airport -> airport.id }
                        ) { airport ->
                            AirportItem(
                                airport,
                                modifier = Modifier
                                    .clickable {
                                        active = false
                                        navController.navigate(
                                            "${FlightSearchScreens.FlightList.name}/${airport.codeIATA}"
                                        )
                                    })
                        }
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = FlightSearchScreens.HomeFlight.name
            ) {
                composable(FlightSearchScreens.HomeFlight.name) {
                    val airport = viewModel::getAirportByCode
                    HomeFlightSearchScreens(
                        airport = airport,
                        favoritesListUiState = favoritesListUiState.favoritesList,
                        favoriteUiState = viewModel.favoriteUiState,
                        updateFavorite = updateFavorite,
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.padding_medium))
                            .fillMaxSize()
                    )
                }
                val airportRouteArgument = "CODE"
                composable(
                    route = FlightSearchScreens.FlightList.name + "/{$airportRouteArgument}",
                    arguments = listOf(navArgument(airportRouteArgument) {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val iataCode = backStackEntry.arguments?.getString(airportRouteArgument)
                        ?: error("airportRouteArgument cannot be null")
                    val departureAirport by viewModel.getAirportByCode(iataCode)
                        .collectAsState(Airport(2, "DUB", "Dublin Airport", 130))
                    val arrivalAirports by viewModel.getPossibleFlight(iataCode)
                        .collectAsState(emptyList())

                    FlightListScreen(
                        departureAirport = departureAirport,
                        arrivalAirports = arrivalAirports,
                        favoritesListUiState = favoritesListUiState.favoritesList,
                        favoriteUiState = viewModel.favoriteUiState,
                        updateFavorite = updateFavorite,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
                    )
                }

            }
        }
    }
}

@Composable
private fun HomeFlightSearchScreens(
    airport: (String) -> Flow<Airport>,
    favoritesListUiState: List<FavoriteDetails>,
    favoriteUiState: FavoriteUiState,
    updateFavorite: (FavoriteDetails, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        if (favoritesListUiState.isNotEmpty()) {
            //Lista favoritos
            Text(
                text = stringResource(R.string.favorite_routes),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))
            LazyColumn(
                modifier = Modifier
            ) {
                items(
                    items = favoritesListUiState,
                    key = { favorite -> favorite.id }
                ) { favorite ->
                    FlightDetails(
                        departureAirport = airport(favorite.departureCode).collectAsState(
                            Airport(0, "", "", 0)
                        ).value,
                        arrivalAirport = airport(favorite.destinationCode).collectAsState(
                            Airport(0, "", "", 0)
                        ).value,
                        favoritesListUiState = favoritesListUiState,
                        favoriteUiState = favoriteUiState.favoriteDetails,
                        updateFavorite = updateFavorite,
                        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small)),
                    )
                }
            }
        }
    }
}

@Composable
private fun FlightListScreen(
    departureAirport: Airport,
    arrivalAirports: List<Airport>,
    favoritesListUiState: List<FavoriteDetails>,
    favoriteUiState: FavoriteUiState,
    updateFavorite: (FavoriteDetails, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.flight_from, departureAirport.codeIATA),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))
        LazyColumn(
            modifier = Modifier
        ) {
            items(
                items = arrivalAirports,
                key = { arrivalAirport -> arrivalAirport.id }
            ) { arrivalAirport ->
                FlightDetails(
                    departureAirport = departureAirport,
                    arrivalAirport = arrivalAirport,
                    favoritesListUiState = favoritesListUiState,
                    favoriteUiState = favoriteUiState.favoriteDetails,
                    updateFavorite = updateFavorite,
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small))
                )
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun FlightDetails(
    departureAirport: Airport,
    arrivalAirport: Airport,
    favoritesListUiState: List<FavoriteDetails>,
    favoriteUiState: FavoriteDetails,
    updateFavorite: (FavoriteDetails, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(5f)
            ) {
                Text(text = stringResource(R.string.depart))
                AirportItem(departureAirport)
                Spacer(Modifier.size(dimensionResource(R.dimen.padding_small)))
                Text(text = stringResource(R.string.arrive))
                AirportItem(arrivalAirport)
            }
            Spacer(Modifier.weight(0.3f))
            var isFavorite by remember { mutableStateOf(false) }

            val favoriteDb = favoritesListUiState.filter {
                it.departureCode.contains(departureAirport.codeIATA) && it.destinationCode.contains(
                    arrivalAirport.codeIATA
                )
            }
            isFavorite = favoriteDb.isNotEmpty()

            IconToggleButton(
                checked = isFavorite,
                onCheckedChange = {
                    updateFavorite(
                        favoriteUiState.copy(
                            departureCode = departureAirport.codeIATA,
                            destinationCode = arrivalAirport.codeIATA
                        ),
                        isFavorite
                    )
                    isFavorite = !isFavorite
                }
            ) {
                Icon(
                    tint = if (isFavorite) Color(239, 184, 16) else Color.LightGray,
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun AirportItem(
    airport: Airport,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(vertical = dimensionResource(R.dimen.padding_extra_small))
    ) {
        Text(
            text = airport.codeIATA,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.size(dimensionResource(R.dimen.padding_small)))
        Text(
            text = airport.name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = false)
@Composable
fun FlightDetailsPreview() {
    FlightSearchTheme {
        val viewModel: FlightSearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val departureAirport = Airport(1, "FCO", "Leonardo da Vinci International Airport", 13)
        val arrivalAirport = Airport(2, "DUB", "Dublin Airport", 130)
        val favoriteList = listOf(
            FavoriteDetails(1, "FCO", "DUB"),
            FavoriteDetails(2, "FCO", "AOA"))
        FlightDetails(
            departureAirport = departureAirport,
            arrivalAirport = arrivalAirport,
            favoritesListUiState = favoriteList,
            favoriteUiState = favoriteList[0],
            updateFavorite = viewModel::updateFavorite
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeFlightPreview() {
    FlightSearchTheme {
        val viewModel: FlightSearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val airport = viewModel::getAirportByCode
        val favoriteList = listOf(
            FavoriteDetails(1, "FCO", "DUB"),
            FavoriteDetails(2, "FCO", "AOA"))

        HomeFlightSearchScreens(
            airport = airport,
            favoritesListUiState = favoriteList,
            favoriteUiState = FavoriteUiState(favoriteList[0]),
            updateFavorite = viewModel::updateFavorite
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FlightListPreview() {
    FlightSearchTheme {
        val viewModel: FlightSearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val departureAirport = Airport(1, "FCO", "Leonardo da Vinci International Airport", 13)
        val arrivalAirports = listOf(
            Airport(2, "DUB", "Dublin Airport", 130),
            Airport(3, "AOA", "Dublin Airport", 130)
        )
        val favoriteList = listOf(
            FavoriteDetails(1, departureAirport.codeIATA, arrivalAirports[0].codeIATA),
            FavoriteDetails(2, departureAirport.codeIATA, arrivalAirports[1].codeIATA))

        FlightListScreen(
            departureAirport = departureAirport,
            arrivalAirports = arrivalAirports,
            favoritesListUiState = favoriteList,
            favoriteUiState = FavoriteUiState(favoriteList[0]),
            updateFavorite =  viewModel::updateFavorite
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FlightSearchAppPreview() {
    FlightSearchTheme {
        FlightSearchApp()
    }
}