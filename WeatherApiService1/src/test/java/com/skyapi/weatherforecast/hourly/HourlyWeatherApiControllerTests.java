package com.skyapi.weatherforecast.hourly;

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
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@WebMvcTest(HourlyWeatherApiController.class)
public class HourlyWeatherApiControllerTests {
	private static final String X_CURRENT_HOUR = "X-Current-Hour";

	private static final String END_POINT_PATH = "/v1/hourly";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private HourlyWeatherService hourlyWeatherService;
	
	@MockBean
	private GeoLocationService locationService;
	
	@Test 
	public void testGetByIPShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test 
	public void testGetByIPShouldReturn400BadRequestBecauseGeoLocationException() throws Exception {
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenThrow(GeoLocationException.class);
		
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "9"))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test 
	public void testGetByIPShouldReturn204NoContent() throws Exception {
		int currentHour = 9;
		Location location = new Location().code("DELHI_IN");
				
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(new ArrayList<>());
		
		
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
			.andExpect(status().isNoContent())
			.andDo(print());
	}
	
	@Test 
	public void testGetByIPShouldReturn200Ok() throws Exception {
		int currentHour = 9;

		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		

		HourlyWeather forecast1 = new HourlyWeather()
			.location(location)
			.hourOfDay(10)
			.temperature(13)
			.precipitation(70)
			.status("Cloudy");
		
		HourlyWeather forecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(11)
				.temperature(15)
				.precipitation(60)
				.status("Sunny");
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(List.of(forecast1, forecast2));
		
		String expectedLocation = location.toString();
		
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.location", is(expectedLocation)))
			.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
			.andDo(print());
	}
	
	@Test
	public void testGetByLocationCodeShouldReturn404NotFound() throws Exception {
		String locationCode = "ABC_USA";
		int currentHour = 9;
		
		Mockito.when(hourlyWeatherService.getLocationByCode(Mockito.anyString(), Mockito.anyInt())).thenThrow(LocationNotFoundException.class);
		
		mockMvc.perform(get(END_POINT_PATH + "/" + locationCode).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
		.andExpect(status().isNotFound())
		.andDo(print());
	}
	
	@Test
	public void testGetByLocationCodeShouldReturn200Ok() throws Exception {
		String locationCode = "NYC_USA";
		int currentHour = 9;
		
		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		

		HourlyWeather forecast1 = new HourlyWeather()
			.location(location)
			.hourOfDay(10)
			.temperature(13)
			.precipitation(70)
			.status("Cloudy");
		
		HourlyWeather forecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(11)
				.temperature(15)
				.precipitation(60)
				.status("Sunny");
		
		var hourlyForecast = List.of(forecast1, forecast2);
		
		Mockito.when(hourlyWeatherService.getLocationByCode(Mockito.anyString(), Mockito.anyInt())).thenReturn(hourlyForecast);
		
		String expectedLocation = location.toString();
		
		mockMvc.perform(get(END_POINT_PATH + "/" + locationCode).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.location", is(expectedLocation)))
		.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
		.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
		String requestURI = END_POINT_PATH + "/NYC_USA";
		
		List<HourlyWeatherDto> listDto = Collections.emptyList();
		
		String requestBody = objectMapper.writeValueAsString(listDto);
		
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0]", is("Hourly forecast data cannot be empty")))
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequestBBecauseInvalidData() throws Exception {
		String requestURI = END_POINT_PATH + "/NYC_USA";
				
		HourlyWeatherDto dto1 = new HourlyWeatherDto()
				.hourOfDay(10)
				.temperature(133)
				.precipitation(700)
				.status("Cloudy");
			
		HourlyWeatherDto dto2 = new HourlyWeatherDto()
				.hourOfDay(11)
				.temperature(155)
				.precipitation(60)
				.status("");
			
		List<HourlyWeatherDto> listDto = List.of(dto1, dto2);
		
		String requestBody = objectMapper.writeValueAsString(listDto);
		
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		String locationCode = "NYC_USA";
		
		String requestURI = END_POINT_PATH + "/" + locationCode;
				
		HourlyWeatherDto dto1 = new HourlyWeatherDto()
				.hourOfDay(10)
				.temperature(13)
				.precipitation(70)
				.status("Cloudy");
	
			
		List<HourlyWeatherDto> listDto = List.of(dto1);
		
		String requestBody = objectMapper.writeValueAsString(listDto);
		
		Mockito.when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList())).thenThrow(LocationNotFoundException.class);
		
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn200Ok() throws Exception {
		String locationCode = "NYC_USA";
		
		String requestURI = END_POINT_PATH + "/" + locationCode;
				
		HourlyWeatherDto dto1 = new HourlyWeatherDto()
				.hourOfDay(10)
				.temperature(13)
				.precipitation(70)
				.status("Cloudy"); 
	
		
		HourlyWeatherDto dto2 = new HourlyWeatherDto()
				.hourOfDay(11)
				.temperature(155)
				.precipitation(60)
				.status("");
		
		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		

		HourlyWeather forecast1 = new HourlyWeather()
			.location(location)
			.hourOfDay(10)
			.temperature(13)
			.precipitation(70)
			.status("Cloudy");
		
		HourlyWeather forecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(11)
				.temperature(15)
				.precipitation(60)
				.status("Sunny");
		
			
		List<HourlyWeatherDto> listDto = List.of(dto1, dto2);
		
		var hourlyForecast = List.of(forecast1, forecast2);
		
		String requestBody = objectMapper.writeValueAsString(listDto);
		
		Mockito.when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList())).thenReturn(hourlyForecast);
		
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.location", is(location.toString())))
			.andExpect(content().contentType("application/hal+json"))
			.andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/realtime/" + locationCode)))
			.andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly/" + locationCode)))
			.andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + locationCode)))
			.andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + locationCode)))
			.andDo(print());
	}
	 
}
