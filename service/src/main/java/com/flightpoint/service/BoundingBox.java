package com.flightpoint.service;

/**
 * Defines a bounding box whose points are used to query the OpenSky API
 */
public record BoundingBox(double loMin, double laMin, double loMax, double laMax) {
}
