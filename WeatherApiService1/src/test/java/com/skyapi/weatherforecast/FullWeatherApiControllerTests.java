package com.skyapi.weatherforecast;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.full.FullWeatherService;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@WebMvcTest(FullWeatherApiController.class)
public class FullWeatherApiControllerTests {
	
	private static final String END_POINT_PATH = "/v1/full";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private FullWeatherService weatherService;
	
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
		
		Mockito.when(weatherService.getByLocation(location)).thenThrow(ex);
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
}
