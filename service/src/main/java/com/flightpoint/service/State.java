package com.flightpoint.service;

public class State {
    public String icao24;
    public String callsign;
    public String originCountry;
    public int timePosition;
    public int lastContact;
    public double longitude;
    public double latitude;
    public double baroAltitude;
    public boolean onGround;
    public double velocity;
    public double trueTrack;
    public double verticalRate;
    public int[] sensors;
    public double geoAltitude;
    public String squawk;
    public boolean spi;
    public int positionSource;
    public int category;

    public State() { }

    public State( String icao24,
     String callsign,
     String originCountry,
     int timePosition,
     int lastContact,
     double longitude,
     double latitude,
     double baroAltitude,
     boolean onGround,
     double velocity,
     double trueTrack,
     double verticalRate,
     int[] sensors,
     double geoAltitude,
     String squawk,
     boolean spi,
     int positionSource,
     int category) {}


    public String getIcao24() {
        return this.icao24;
    }

    public void setIcao24(String icao24) {
        this.icao24 = icao24;
    }

    public String getCallsign() {
        return this.callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getOriginCountry() {
        return this.originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public int getTimePosition() {
        return this.timePosition;
    }

    public void setTimePosition(int timePosition) {
        this.timePosition = timePosition;
    }

    public int getLastContact() {
        return this.lastContact;
    }

    public void setLastContact(int lastContact) {
        this.lastContact = lastContact;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getBaroAltitude() {
        return this.baroAltitude;
    }

    public void setBaroAltitude(double baroAltitude) {
        this.baroAltitude = baroAltitude;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean getOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public double getVelocity() {
        return this.velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getTrueTrack() {
        return this.trueTrack;
    }

    public void setTrueTrack(double trueTrack) {
        this.trueTrack = trueTrack;
    }

    public double getVerticalRate() {
        return this.verticalRate;
    }

    public void setVerticalRate(double verticalRate) {
        this.verticalRate = verticalRate;
    }

    public int[] getSensors() {
        return this.sensors;
    }

    public void setSensors(int[] sensors) {
        this.sensors = sensors;
    }

    public double getGeoAltitude() {
        return this.geoAltitude;
    }

    public void setGeoAltitude(double geoAltitude) {
        this.geoAltitude = geoAltitude;
    }

    public String getSquawk() {
        return this.squawk;
    }

    public void setSquawk(String squawk) {
        this.squawk = squawk;
    }

    public boolean isSpi() {
        return this.spi;
    }

    public boolean getSpi() {
        return this.spi;
    }

    public void setSpi(boolean spi) {
        this.spi = spi;
    }

    public int getPositionSource() {
        return this.positionSource;
    }

    public void setPositionSource(int positionSource) {
        this.positionSource = positionSource;
    }

    public int getCategory() {
        return this.category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
    
}
