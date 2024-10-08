package com.flightpoint.service;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightSearchController {

    //private final AtomicLong counter = new AtomicLong();

    @PostMapping(path="/flightsearch", consumes="application/json", produces="application/json")
    public SearchOutput flightSearch(@RequestBody SearchInput searchInput) {
        
        //Extract data directly from searchInput object.
        double deviceHeading = searchInput.deviceHeading();
        double deviceLat = searchInput.deviceLat();
        double deviceLng = searchInput.deviceLng();
        double deviceAzimuth = searchInput.deviceAzimuth();

        //SearchOutput searchOutput = SearchUtils.search(deviceLat, deviceLng, deviceHeading);
    
        return new SearchOutput();
    }
}
