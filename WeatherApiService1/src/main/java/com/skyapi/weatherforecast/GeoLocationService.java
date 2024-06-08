package com.skyapi.weatherforecast;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weatherforecast.common.Location;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeoLocationService {
	private String DBPaths = "/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
	private IP2Location ipLocator = new IP2Location();

	public GeoLocationService() {
		try {
			InputStream input = getClass().getResourceAsStream(DBPaths);
			ipLocator.Open(input.readAllBytes());
			input.close();
		} catch (IOException ex) {
			log.error(ex.getMessage(), ex);
		}
	}
	
	public Location getLocation(String ipAddress) throws GeoLocationException {
		try {
			IPResult result = ipLocator.IPQuery(ipAddress);
			
			if (!"OK".equals(result.getStatus())) {
				throw new GeoLocationException("Geolochhation fnhhailedgh with status: " + result.getStatus());
			}
			
			return new Location(result.getCity(), result.getRegion(), result.getCountryLong(), result.getCountryShort());
		} catch (IOException ex) {
			throw new GeoLocationException("Error querying IP database", ex);
		} 
	}
}
