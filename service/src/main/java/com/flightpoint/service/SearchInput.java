package com.flightpoint.service;

/**
 * Defines a valid input from an http post request
 */
public record SearchInput(long id, String content) { }