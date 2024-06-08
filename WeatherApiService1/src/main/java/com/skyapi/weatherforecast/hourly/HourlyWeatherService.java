package com.skyapi.weatherforecast.hourly;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class HourlyWeatherService {
	
	private HourlyWeatherRepository hourlyWeatherRepo;
	private LocationRepository locationRepo;
	
	public HourlyWeatherService(HourlyWeatherRepository hourlyWeatherRepo, LocationRepository locationRepo) {
		super();
		this.hourlyWeatherRepo = hourlyWeatherRepo;
		this.locationRepo = locationRepo;
	}
	
	public List<HourlyWeather> getByLocation(Location location, int currentHour) throws LocationNotFoundException {
		String countryCode = location.getCountryCode();
		String cityName = location.getCityName();
		
		Location locationInDb = locationRepo.findByCountryCodeAndCityName(countryCode, cityName);
		
		if (locationInDb == null) {
			throw new LocationNotFoundException("No location found with the given country code and city name");
		}
		
		return hourlyWeatherRepo.findByLocationCode(locationInDb.getCode(), currentHour);
	}
	
	public List<HourlyWeather> getLocationByCode(String code, int currentHour) throws LocationNotFoundException {
		Location location = locationRepo.findByCode(code);
		
		if (location == null) {
			throw new LocationNotFoundException("No location found with the given code: " + code);
		}
		
		return hourlyWeatherRepo.findByLocationCode(code, currentHour);
	}
	
	public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyWeatherInRequest) throws LocationNotFoundException {
		Location location = locationRepo.findByCode(locationCode);
		
		if (location == null) {
			throw new LocationNotFoundException("No location found with the given code: " + locationCode);
		}
		
		for (HourlyWeather item : hourlyWeatherInRequest) {
			item.location(location);
		}
		
		List<HourlyWeather> hourlyWeatherInDb = location.getListHourlyWeather();
		List<HourlyWeather> hourlyWeatherToBeRemoved = new ArrayList<>();		
		
		for (HourlyWeather item : hourlyWeatherInDb) {
			if (!hourlyWeatherInRequest.contains(item)) {
				hourlyWeatherToBeRemoved.add(item.getShallowCopy()); 
			}
			
		}
		
		for (HourlyWeather item : hourlyWeatherToBeRemoved) {
			hourlyWeatherInDb.remove(item);
		}
		
		return (List<HourlyWeather>) hourlyWeatherRepo.saveAll(hourlyWeatherInRequest);
	}
}
