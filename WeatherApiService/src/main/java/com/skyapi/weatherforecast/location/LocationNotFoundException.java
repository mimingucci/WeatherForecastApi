package com.skyapi.weatherforecast.location;

public class LocationNotFoundException extends RuntimeException {

	public LocationNotFoundException(String locationCode) {
		super("Could not found location with given code: "+locationCode);
		// TODO Auto-generated constructor stub
	}

	public LocationNotFoundException(String countryCode, String cityName) {
         super("Could not found location with given country code: "+countryCode+" and city name: "+cityName);
	}
}
