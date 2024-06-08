package com.skyapi.weatherforecast.realtime;

import org.modelmapper.ModelMapper;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.daily.DailyWeatherApiController;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/realtime")
@Slf4j
public class RealtimeWeatherApiController {
	
	private GeoLocationService locationService;
	private RealtimeWeatherService realtimeWeatherService;
	private ModelMapper modelMapper;
	
	
	public RealtimeWeatherApiController(GeoLocationService locationService,
			RealtimeWeatherService realtimeWeatherService, ModelMapper modelMapper) {
		super();
		this.locationService = locationService;
		this.realtimeWeatherService = realtimeWeatherService;
		this.modelMapper = modelMapper;
	}

	@GetMapping
	public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request) {
		String ipAddress = CommonUtility.getIPAddress(request);
		
		try {
			Location locationFromIP = locationService.getLocation(ipAddress);
			RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);
		
			RealtimeWeatherDto dto = modelMapper.map(realtimeWeather, RealtimeWeatherDto.class);
			
			return ResponseEntity.ok(addLinksByIp(dto));
		} catch (GeoLocationException e) {
			log.error(e.getMessage(), e);
			
			return ResponseEntity.badRequest().build();
		} catch (LocationNotFoundException e) {
			log.error(e.getMessage(), e);
			
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/{locationCode}")
	public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode) throws GeoLocationException {
		try {
			RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
			RealtimeWeatherDto dto = entity2Dto(realtimeWeather);
			
			
			return ResponseEntity.ok(addLinksByLocation(dto, locationCode));
			
		} catch (LocationNotFoundException e) {
			log.error(e.getMessage(), e);
			
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode, @RequestBody @Valid RealtimeWeather realtimeWeatherInRequest) throws GeoLocationException {
		
		realtimeWeatherInRequest.setLocationCode(locationCode);
		try {
			RealtimeWeather updatedRealtimeWeather = realtimeWeatherService.update(locationCode, realtimeWeatherInRequest);
			
			RealtimeWeatherDto dto = entity2Dto(updatedRealtimeWeather);
			
			
			return ResponseEntity.ok(addLinksByLocation(dto, locationCode));
		}
		catch (LocationNotFoundException ex) {
			return ResponseEntity.notFound().build();
		}
	}
	
	private RealtimeWeatherDto entity2Dto(RealtimeWeather realtimeWeather) {
		RealtimeWeatherDto dto = modelMapper.map(realtimeWeather, RealtimeWeatherDto.class);
		
		return dto;
	}
	
	private RealtimeWeatherDto addLinksByIp(RealtimeWeatherDto dto) throws GeoLocationException {
		dto.add(linkTo(
						methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIPAddress(null))
							.withSelfRel());
		dto.add(linkTo(
						methodOn(HourlyWeatherApiController.class).listHourlyForecastByIPAddress(null))
							.withRel("hourly_forecast"));
		dto.add(linkTo(
				methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null))
					.withRel("daily_forecast"));
		dto.add(linkTo(
				methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null))
					.withRel("full_forecast"));
		
		return dto;  
	}
	
	private RealtimeWeatherDto addLinksByLocation(RealtimeWeatherDto dto, String locationCode) throws GeoLocationException {
		dto.add(linkTo(
						methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(locationCode))
							.withSelfRel());
		dto.add(linkTo(
						methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(locationCode, null))
							.withRel("hourly_forecast"));
		dto.add(linkTo(
				methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(locationCode))
					.withRel("daily_forecast"));
		dto.add(linkTo(
				methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode))
					.withRel("full_forecast"));
		
		return dto;  
	}
}
