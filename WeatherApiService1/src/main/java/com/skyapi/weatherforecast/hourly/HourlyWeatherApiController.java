package com.skyapi.weatherforecast.hourly;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.daily.DailyWeatherApiController;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/hourly")
@Validated
public class HourlyWeatherApiController {
	
	private HourlyWeatherService hourlyWeatherService;
	private GeoLocationService locationService;
	private ModelMapper modelMapper;
	
	
	
	public HourlyWeatherApiController(HourlyWeatherService hourlyWeatherService, GeoLocationService locationService,
			ModelMapper modelMapper) {
		super();
		this.hourlyWeatherService = hourlyWeatherService;
		this.locationService = locationService;
		this.modelMapper = modelMapper;
	}

	@GetMapping
	public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request) {
		String ipAddress = CommonUtility.getIPAddress(request);
		
		
		try {
			int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
			
			Location locationFromIp = locationService.getLocation(ipAddress);
			
			List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocation(locationFromIp, currentHour);
		
			if (hourlyForecast.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			
			HourlyWeatherListDto listEntity2Dto = listEntity2Dto(hourlyForecast);
		
			return ResponseEntity.ok(addLinksByIp(listEntity2Dto));
		}
		catch (NumberFormatException | GeoLocationException ex) {
			
			return ResponseEntity.badRequest().build();
		}
		catch (LocationNotFoundException ex) {
			
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/{locationCode}")
	public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable("locationCode") String locationCode, HttpServletRequest request) throws GeoLocationException {
		try {
			int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
			List<HourlyWeather> hourlyWeathers = hourlyWeatherService.getLocationByCode(locationCode, currentHour);
						
			if (hourlyWeathers.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			
			HourlyWeatherListDto listEntity2Dto = listEntity2Dto(hourlyWeathers);
			
			return ResponseEntity.ok(addLinksByCode(listEntity2Dto, locationCode));
		
		} catch (LocationNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
		catch (NumberFormatException ex) {
			return ResponseEntity.badRequest().build();
		}
	}
	
	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateHourlyForecast(@PathVariable("locationCode") String locationCode, @RequestBody @Valid List<HourlyWeatherDto> listDto) throws BadRequestException {
		if (listDto.isEmpty()) {
			throw new BadRequestException("Hourly forecast data cannot be empty");
		}
		
		listDto.forEach(System.out::println);
		
		List<HourlyWeather> listHourlyWeather = listDto2ListEntity(listDto);
		
		listHourlyWeather.forEach(System.out::println);
		
		try {
			List<HourlyWeather> updatedHourlyWeather = hourlyWeatherService.updateByLocationCode(locationCode, listHourlyWeather);
		
			return ResponseEntity.ok(listEntity2Dto(updatedHourlyWeather));
		} catch (LocationNotFoundException e) {
			
			return ResponseEntity.notFound().build();
		}	
	}
	
	private List<HourlyWeather> listDto2ListEntity(List<HourlyWeatherDto> listDto) {
		List<HourlyWeather> listEntity = new ArrayList<>();
		
		listDto.forEach(dto -> {
			listEntity.add(modelMapper.map(dto, HourlyWeather.class));
		});
		
		return listEntity;
	}
	
	private HourlyWeatherListDto listEntity2Dto(List<HourlyWeather> hourlyForecast) {
		Location location = hourlyForecast.get(0).getId().getLocation();
		
		HourlyWeatherListDto listDto = new HourlyWeatherListDto();
		listDto.setLocation(location.toString());
		
		hourlyForecast.forEach(hourlyWeather -> {
			HourlyWeatherDto dto = modelMapper.map(hourlyWeather, HourlyWeatherDto.class);
			listDto.addWeatherHourlyDto(dto);
		});
		
		return listDto;
	}
	
	private HourlyWeatherListDto addLinksByIp(HourlyWeatherListDto dto) throws GeoLocationException {
		dto.add(linkTo(
				methodOn(HourlyWeatherApiController.class)
					.listHourlyForecastByIPAddress(null))
					.withSelfRel());
		dto.add(linkTo(
				methodOn(RealtimeWeatherApiController.class)
					.getRealtimeWeatherByIPAddress(null))
					.withRel("realtime_forecast"));
		dto.add(linkTo(
				methodOn(DailyWeatherApiController.class)
					.listDailyForecastByIPAddress(null))
					.withRel("daily_forecast"));
		dto.add(linkTo(
				methodOn(FullWeatherApiController.class)
					.getFullWeatherByIPAddress(null))
					.withRel("full_forecast"));
		
		return dto;
	}
	
	private HourlyWeatherListDto addLinksByCode(HourlyWeatherListDto dto, String locationCode) throws GeoLocationException {
		dto.add(linkTo(
				methodOn(HourlyWeatherApiController.class)
					.listHourlyForecastByLocationCode(locationCode, null))
					.withSelfRel());
		dto.add(linkTo(
				methodOn(RealtimeWeatherApiController.class)
					.getRealtimeWeatherByLocationCode(locationCode))
					.withRel("realtime_forecast"));
		dto.add(linkTo(
				methodOn(DailyWeatherApiController.class)
					.listDailyForecastByLocationCode(locationCode))
					.withRel("daily_forecast"));
		dto.add(linkTo(
				methodOn(FullWeatherApiController.class)
					.getFullWeatherByLocationCode(locationCode))
					.withRel("full_forecast"));
		
		return dto;
	}

}
