package com.skyapi.weatherforecast.full;

import org.modelmapper.ModelMapper;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
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
import com.skyapi.weatherforecast.common.Location;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/full")
public class FullWeatherApiController {
	
	private GeoLocationService locationService;
	
	private FullWeatherService weatherService;
	
	private ModelMapper modelMapper;
	
	private FullWeatherModelAssembler modelAssembler;

	public FullWeatherApiController(GeoLocationService locationService, FullWeatherService weatherService, 
			ModelMapper modelMapper, FullWeatherModelAssembler modelAssembler) {
		super();
		this.locationService = locationService;
		this.weatherService = weatherService;
		this.modelMapper = modelMapper;
		this.modelAssembler = modelAssembler;
	}
	
	@GetMapping
	public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request) throws GeoLocationException {
		String ipAddress = CommonUtility.getIPAddress(request);
		
		Location loactionFromIP = locationService.getLocation(ipAddress);
		Location locationInDb = weatherService.getByLocation(loactionFromIP);
		
		FullWeatherDto dto = entity2Dto(locationInDb);
		
		return ResponseEntity.ok(modelAssembler.toModel(dto));
	}
	
	@GetMapping("/{locationCode}")
	public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable("locationCode") String locationCode) {
		Location locationInDb = weatherService.get(locationCode);
		
		FullWeatherDto dto = entity2Dto(locationInDb);
		
		return ResponseEntity.ok(addLinksByLocation(dto, locationCode));
	}
	
	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateFullWeather(@PathVariable("locationCode") String locationCode, @RequestBody FullWeatherDto dto) throws BadRequestException {
		if (dto.getListHourlyWeather().isEmpty()) {
			throw new BadRequestException("Hourly weather data cannot be empty");
		}
		
		Location locationInRequest = dto2Entity(dto);
		
		Location updatedLocation = weatherService.update(locationCode, locationInRequest);
		
		FullWeatherDto updatedDto = entity2Dto(updatedLocation);
		
		return ResponseEntity.ok(addLinksByLocation(updatedDto, locationCode));
	}
	
	private FullWeatherDto entity2Dto(Location entity) {
		FullWeatherDto dto = modelMapper.map(entity, FullWeatherDto.class);
		dto.getRealtimeWeather().setLocation(null);
	
		return dto;
	}
	
	private Location dto2Entity(FullWeatherDto dto) {
		return modelMapper.map(dto, Location.class);
	}
	
	private EntityModel<FullWeatherDto> addLinksByLocation(FullWeatherDto dto, String locationCode) {
		return EntityModel.of(dto)
				.add(linkTo(
						methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode)).withSelfRel());
	}

}
