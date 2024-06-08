package com.skyapi.weatherforecast.daily;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
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
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/daily")
@Validated
public class DailyWeatherApiController {

	private DailyWeatherService dailyWeatherService;

	private GeoLocationService locationService;

	private ModelMapper modelMapper;

	public DailyWeatherApiController(DailyWeatherService dailyWeatherService, GeoLocationService locationService,
			ModelMapper modelMapper) {
		super();
		this.dailyWeatherService = dailyWeatherService;
		this.locationService = locationService;
		this.modelMapper = modelMapper;
	}

	@GetMapping
	public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) throws GeoLocationException {
		String ipAddress = CommonUtility.getIPAddress(request);

		Location locationFromIP = locationService.getLocation(ipAddress);

		List<DailyWeather> dailyForecast = dailyWeatherService.getByLocation(locationFromIP);

		if (dailyForecast.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		DailyWeatherListDto dto = listEntity2Dto(dailyForecast);

		return ResponseEntity.ok(addLinksByIp(dto));
	}

	@GetMapping("/{locationCode}")
	public ResponseEntity<?> listDailyForecastByLocationCode(@PathVariable("locationCode") String locationCode)
			throws GeoLocationException {
		List<DailyWeather> dailyForecast = dailyWeatherService.getByLocationCode(locationCode);

		if (dailyForecast.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		DailyWeatherListDto dto = listEntity2Dto(dailyForecast);

		return ResponseEntity.ok(addLinksByLocation(dto, locationCode));
	}

	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateDailyForecast(@PathVariable("locationCode") String code,
			@RequestBody @Valid List<DailyWeatherDto> listDto) throws BadRequestException, GeoLocationException {
		if (listDto.isEmpty()) {
			throw new BadRequestException("Daily forecast data cannot be empty");
		}

		listDto.forEach(System.out::println);

		List<DailyWeather> dailyWeather = listDto2ListEntity(listDto);

		System.out.println("========================");

		dailyWeather.forEach(System.out::println);

		List<DailyWeather> updatedForecast = dailyWeatherService.updateByLocationCode(code, dailyWeather);

		DailyWeatherListDto dto = listEntity2Dto(updatedForecast);

		return ResponseEntity.ok(addLinksByLocation(dto, code));
	}

	private DailyWeatherListDto listEntity2Dto(List<DailyWeather> dailyForecast) {
		Location location = dailyForecast.get(0).getId().getLocation();

		DailyWeatherListDto listDto = new DailyWeatherListDto();

		listDto.setLocation(location.toString());

		dailyForecast.forEach(dailyWeather -> {
			listDto.addDailyWeatherDto(modelMapper.map(dailyWeather, DailyWeatherDto.class));
		});

		return listDto;
	}

	private List<DailyWeather> listDto2ListEntity(List<DailyWeatherDto> listDto) {
		List<DailyWeather> listEntity = new ArrayList<>();

		listDto.forEach(dto -> {
			listEntity.add(modelMapper.map(dto, DailyWeather.class));
		});

		return listEntity;
	}

	private EntityModel<DailyWeatherListDto> addLinksByIp(DailyWeatherListDto dto) throws GeoLocationException {
		EntityModel<DailyWeatherListDto> entityModel = EntityModel.of(dto);

		entityModel.add(
				linkTo(methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).withSelfRel());
		entityModel.add(linkTo(methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIPAddress(null))
				.withRel("realtime_forecast"));
		entityModel.add(linkTo(methodOn(HourlyWeatherApiController.class).listHourlyForecastByIPAddress(null))
				.withRel("hourly_forecast"));
		entityModel.add(linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null))
				.withRel("full_forecast"));

		return entityModel;
	}

	private EntityModel<DailyWeatherListDto> addLinksByLocation(DailyWeatherListDto dto, String locationCode)
			throws GeoLocationException {
		EntityModel<DailyWeatherListDto> entityModel = EntityModel.of(dto);

		entityModel.add(linkTo(methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(locationCode))
				.withSelfRel());
		entityModel
				.add(linkTo(methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(locationCode))
						.withRel("realtime_forecast"));
		entityModel.add(
				linkTo(methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(locationCode, null))
						.withRel("hourly_forecast"));
		entityModel.add(linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode))
				.withRel("full_forecast"));

		return entityModel;
	}

}
