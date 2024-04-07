package com.example.flightsearch.ui

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.ui.theme.FlightSearchTheme

enum class FlightSearchScreens {
    HomeFlight,
    FlightList
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp(
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory)
) {
    val navController = rememberNavController()
    val airports by viewModel.getAllAirports().collectAsState(emptyList())
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
            SearchBar(
                query = "",
                placeholder = { Text(text = "Enter departure airport") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                onQueryChange = {},
                onSearch = {},
                active = false,
                onActiveChange = {},
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            ) {

            }

            NavHost(
                navController = navController,
                startDestination = FlightSearchScreens.HomeFlight.name
            ) {
                composable(FlightSearchScreens.HomeFlight.name) {
                    HomeFlightSearchScreens(
                        airports = airports,
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.padding_medium))
                            .fillMaxSize(),
                        onAirportClick = { airportCode ->
                            navController.navigate(
                                "${FlightSearchScreens.FlightList.name}/code"
                            )

                        }
                    )
                }
                composable(
                    route = FlightSearchScreens.FlightList.name + "/code"
                ) { backStackEntry ->
                    FlightListScreen()
                }

            }
        }
    }
}

@Composable
private fun HomeFlightSearchScreens(
    airports: List<Airport>,
    modifier: Modifier = Modifier,
    onAirportClick: ((String) -> Unit)
) {
    Column(
        modifier = modifier
    ) {
        //Sugerencias busqueda
        LazyColumn(
            modifier = modifier
        ) { items(
            items = airports,
            key = { airport -> airport.id}
        ) { airport ->
            AirportItem(code = airport.codeIATA, name = "Dublin Airport", onAirportClick = onAirportClick)
        }
        }
//        AirportItem(code = "FCO", name = "Leonardo da Vinci International Airport", onAirportClick = onAirportClick)

        //Lista favoritos
        Text(
            text = "Favorite routes",
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))
        FlightDetails()
        FlightDetails()
    }
}

@Composable
private fun FlightListScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Flight from XXX",
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))
        FlightDetails()
        FlightDetails()
        FlightDetails()
    }

}

@Composable
private fun FlightDetails(
    modifier: Modifier = Modifier
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
                modifier = Modifier
            ) {
                Text(text = stringResource(R.string.depart))
                AirportItem(code = "FCO", name = "Leonardo da Vinci International Airport")
                Spacer(Modifier.size(dimensionResource(R.dimen.padding_small)))
                Text(text = stringResource(R.string.arrive))
                AirportItem(code = "DUB", name = "Dublin Airport")
            }
            Spacer(Modifier.weight(1f))
            var isFavorite by remember { mutableStateOf(false) }
            IconToggleButton(
                checked = isFavorite,
                onCheckedChange = { isFavorite = it }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun AirportItem(code: String, name: String, onAirportClick: ((String) -> Unit)? = null) {
    Row(
        modifier = Modifier
            .padding(vertical = dimensionResource(R.dimen.padding_extra_small))
            .clickable(enabled = onAirportClick != null) {
                onAirportClick?.invoke("CODE")
            }
    ) {
        Text(
            text = code,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.size(dimensionResource(R.dimen.padding_small)))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = false)
@Composable
fun FlightDetailsPreview() {
    FlightSearchTheme {
        FlightDetails()
    }
}

@Preview(showBackground = true)
@Composable
fun HomeFlightPreview() {
    FlightSearchTheme {
        HomeFlightSearchScreens(airports = emptyList(), onAirportClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun FlightListPreview() {
    FlightSearchTheme {
        FlightListScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun FlightSearchAppPreview() {
    FlightSearchTheme {
        FlightSearchApp()
    }
}