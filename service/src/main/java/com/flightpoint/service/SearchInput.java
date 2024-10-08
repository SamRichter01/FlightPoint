package com.flightpoint.service;

/**
 * Defines a valid input from an http post request
 */
public record SearchInput(double deviceHeading, double deviceAzimuth, double deviceLat, double deviceLng) { }