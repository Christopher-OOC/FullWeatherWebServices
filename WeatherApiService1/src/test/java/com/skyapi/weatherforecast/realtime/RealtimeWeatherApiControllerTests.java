package com.skyapi.weatherforecast.realtime;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@WebMvcTest(RealtimeWeatherApiController.class)
public class RealtimeWeatherApiControllerTests {

	private static final String END_POINT_PATH = "/v1/realtime";

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;


	@MockBean
	private RealtimeWeatherService realtimeWeatherService;

	@MockBean
	private GeoLocationService locationService;

	@Test
	public void testGetShouldReturnStatus400BadRequest() throws Exception {

		Mockito.when(locationService.getLocation(Mockito.anyString())).thenThrow(GeoLocationException.class);

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	public void testGetShouldReturnStatus404NotFound() throws Exception {
		Location location = new Location();
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(realtimeWeatherService.getByLocation(location)).thenThrow(LocationNotFoundException.class);
		
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturnStatus200Ok() throws Exception {
		Location location = new Location();
		location.setCode("SFCA_USA");
		location.setCityName("San Francisco");
		location.setRegionName("California");
		location.setCountryName("United States of America");
		location.setCountryCode("US");
		
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setStatus("Cloudy");
		realtimeWeather.setWindSpeed(5);
		
		realtimeWeather.setLocation(location);
		location.setRealtimeWeather(realtimeWeather);
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(realtimeWeatherService.getByLocation(location)).thenReturn(realtimeWeather);
		
		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/hal+json"))
			//.andExpect(jsonPath("$.location", is(expectedLocation)))
			.andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/realtime")))
			.andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly")))
			.andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily")))
			.andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full")))
			.andDo(print());
	}
	
	@Test
	public void testGetByLocationCodeShouldReturnStatus404NotFound() throws Exception {
		String locationCode = "ABC_US";
		
		Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenThrow(LocationNotFoundException.class);
		
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isNotFound())
			.andDo(print());
		
	}
	
	@Test
	public void testGetByLocationCodeShouldReturnStatus200Ok() throws Exception {
		String locationCode = "NYC_USA";
		
		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		
		realtimeWeather.setTemperature(-2);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setPrecipitation(42);
		realtimeWeather.setStatus("Snowy");
		realtimeWeather.setWindSpeed(12);
		realtimeWeather.setLastUpdated(new Date());
		
		realtimeWeather.setLocation(location);
		location.setRealtimeWeather(realtimeWeather);
		
		Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenReturn(realtimeWeather);
		
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
		
		mockMvc.perform(get(requestURI))
		.andExpect(status().isOk())
		.andExpect(content().contentType("application/hal+json"))
//		.andExpect(jsonPath("$.location", is(expectedLocation)))
		.andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/realtime/" + locationCode)))
		.andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly/" + locationCode)))
		.andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + locationCode)))
		.andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + locationCode)))
		.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		String locationCode = "ABC_US";
		
		String requestURI = END_POINT_PATH + "/" + locationCode;
	
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(120);
		realtimeWeather.setHumidity(132);
		realtimeWeather.setPrecipitation(142);
		realtimeWeather.setStatus("Snowy");
		realtimeWeather.setWindSpeed(502);
		
		String bodyContent = mapper.writeValueAsString(realtimeWeather);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		String locationCode = "ABC_US";
		
		String requestURI = END_POINT_PATH + "/" + locationCode;
	
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setPrecipitation(80);
		realtimeWeather.setStatus("Cloudy");
		realtimeWeather.setWindSpeed(15);
		
		Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenThrow(LocationNotFoundException.class);
		
		String bodyContent = mapper.writeValueAsString(realtimeWeather);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
			.andExpect(status().isNotFound())
			.andDo(print());
		
	}
	
	@Test
	public void testUpdateShouldReturn200Ok() throws Exception {
		String locationCode = "NYC_USA";
		
		String requestURI = END_POINT_PATH + "/" + locationCode;
	
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setLocationCode(locationCode);
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setPrecipitation(80);
		realtimeWeather.setStatus("Cloudy");
		realtimeWeather.setWindSpeed(15);
		realtimeWeather.setLastUpdated(new Date());
		
		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		
		realtimeWeather.setLocation(location);
		location.setRealtimeWeather(realtimeWeather);
		
		Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenReturn(realtimeWeather);
		
		String bodyContent = mapper.writeValueAsString(realtimeWeather);
		
		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.location", is(expectedLocation)))
			.andExpect(content().contentType("application/hal+json"))
			.andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/realtime/" + locationCode)))
			.andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly/" + locationCode)))
			.andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + locationCode)))
			.andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + locationCode)))
			.andDo(print());
		
	}

}
