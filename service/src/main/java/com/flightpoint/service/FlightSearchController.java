package com.flightpoint.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightSearchController {

    //private final AtomicLong counter = new AtomicLong();

    @PostMapping(path="/flightsearch", consumes="application/json", produces="application/json")
    public SearchOutput flightSearch(@RequestBody SearchInput searchInput) {
        
        //Extract data directly from searchInput object.
        //Double deviceHeading = searchInput.deviceHeading;
        //Double deviceLat = searchInput.deviceLat;
        //Double deviceLng = searchInput.deviceLng;
        //Double deviceAzimuth = searchInput.deviceAzimuth;

        //SearchOutput searchOutput = SearchUtils.search(deviceLat, deviceLng, deviceHeading);
    
        return new SearchOutput();
    }
}
