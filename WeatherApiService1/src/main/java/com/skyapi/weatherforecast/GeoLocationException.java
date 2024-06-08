package com.skyapi.weatherforecast;

public class GeoLocationException extends Exception {

	private static final long serialVersionUID = 1L;

	public GeoLocationException(String message, Throwable cause) {
		super(message, cause);
	}

	public GeoLocationException(String message) {
		super(message);
	}

	 
}
