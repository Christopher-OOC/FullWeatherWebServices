package com.skyapi.weatherforecast.daily;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@WebMvcTest(DailyWeatherApiController.class)
public class DailyWeatherApiControllerTests {

	private static final String END_POINT_PATH = "/v1/daily";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private DailyWeatherService dailyWeatherService;
	
	@MockBean
	private GeoLocationService locationService;
	
	@Test
	public void testGetByIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
		GeoLocationException ex = new GeoLocationException("Geolocation error");
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenThrow(ex);
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
			.andDo(print());
	}
	
	@Test
	public void testGetByIPShouldReturn404NotFound() throws Exception {
		Location location = new Location().code("DELHI_IN");
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		
		var ex = new LocationNotFoundException(location.getCode());
		
		Mockito.when(dailyWeatherService.getByLocation(location)).thenThrow(ex);
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testGetByCodeShouldReturn404NotFound() throws Exception {
		String locationCode = "LACA_US";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		LocationNotFoundException ex = new LocationNotFoundException(locationCode);
		Mockito.when(dailyWeatherService.getByLocationCode(locationCode)).thenThrow(ex);
		
		mockMvc.perform(get(requestURI))
		.andExpect(status().isNotFound())
		.andDo(print());
		
	}
	
	@Test
	public void testGetByCodeShouldReturn204NoContent() throws Exception {
		String locationCode = "DELHI_IN";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		Mockito.when(dailyWeatherService.getByLocationCode(Mockito.anyString())).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isNoContent())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
		String requestURI = END_POINT_PATH + "/NYC_USA";
		
		List<DailyWeatherDto> listDto = Collections.emptyList();
		
		String requestBody = objectMapper.writeValueAsString(listDto);
		
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0]", is("Daily forecast data cannot be empty")))
			.andDo(print());
	}
	
	@Test
	public void testGetByIpShouldReturn200Ok() throws Exception {
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		
		DailyWeather forecast1 = new DailyWeather()
				.location(location)
				.dayOfMonth(16)
				.month(7)
				.minTemp(23)
				.maxTemp(32)
				.precipitation(40)
				.status("Cloudy");
		
		DailyWeather forecast2 = new DailyWeather()
				.location(location)
				.dayOfMonth(17)
				.month(7)
				.minTemp(25)
				.maxTemp(34)
				.precipitation(30)
				.status("Sunny");
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(dailyWeatherService.getByLocation(location)).thenReturn(List.of(forecast1, forecast2));
		
		String expectedLocation = location.toString();
		
		String locationCode = location.getCode();
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/hal+json"))
			.andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/daily")))
			.andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly")))
			.andExpect(jsonPath("$._links.realtime_forecast.href", is("http://localhost/v1/realtime")))
			.andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full")))
			.andDo(print());
		
	}
}
