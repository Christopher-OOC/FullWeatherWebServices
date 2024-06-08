package com.skyapi.weatherforecast.location;

public class LocationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LocationNotFoundException(String locationCode) {
		super("No location found with the given code " + locationCode);
	}

	public LocationNotFoundException(String countryCode, String cityName) {
		super("No location found with " + countryCode + " and city name " + cityName);
	}
}
