package com.example.flightsearch

import android.app.Application
import com.example.flightsearch.data.FlightSearchDatabase

class FlightSearchApplication: Application() {
    val database: FlightSearchDatabase by lazy {
        FlightSearchDatabase.getDatabase(this)
    }
}