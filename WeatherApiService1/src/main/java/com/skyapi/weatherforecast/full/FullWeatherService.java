package com.skyapi.weatherforecast.full;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class FullWeatherService {
	
	private LocationRepository repo;

	public FullWeatherService(LocationRepository repo) {
		super();
		this.repo = repo;
	}
	
	public Location getByLocation(Location locationFromIP) {
		String cityName = locationFromIP.getCityName();
		String countryCode = locationFromIP.getCountryCode();
		
		Location locationInDb = repo.findByCountryCodeAndCityName(countryCode, cityName);
	
		if (locationInDb == null) {
			throw new LocationNotFoundException(countryCode, cityName);
		}
		
		return locationInDb;
	}
	
	public Location get(String locationCode) {
		Location location = repo.findByCode(locationCode);
		
		if (location == null) {
			throw new LocationNotFoundException(locationCode);
		}
		
		return location;
	}
	
	public Location update(String locationCode, Location locationInRequest) {
		Location locationInDb = repo.findByCode(locationCode);
		
		if (locationInDb == null) {
			throw new LocationNotFoundException(locationCode);
		}
		
		RealtimeWeather realtimeWeather = locationInRequest.getRealtimeWeather();
		realtimeWeather.setLocation(locationInDb);
		realtimeWeather.setLastUpdated(new Date());
		
		if (locationInDb.getRealtimeWeather() == null) {
			locationInDb.setRealtimeWeather(realtimeWeather);
			repo.save(locationInDb);
		}
		
		List<DailyWeather> listDailyWeather = locationInRequest.getListDailyWeather();
		listDailyWeather.forEach(dw -> dw.getId().setLocation(locationInDb));
		
		List<HourlyWeather> listHourlyWeather = locationInRequest.getListHourlyWeather();
		listHourlyWeather.forEach(hw -> hw.getId().setLocation(locationInDb));
		
		locationInRequest.setCode(locationInDb.getCode());
		locationInRequest.setCityName(locationInDb.getCityName());
		locationInRequest.setCountryCode(locationInDb.getCountryCode());
		locationInRequest.setCountryName(locationInDb.getCountryName());
		locationInRequest.setRegionName(locationInDb.getRegionName());
		locationInRequest.setEnabled(locationInDb.isEnabled());
		locationInRequest.setTrashed(locationInDb.isTrashed());
		
		return repo.save(locationInRequest);
	}
}
