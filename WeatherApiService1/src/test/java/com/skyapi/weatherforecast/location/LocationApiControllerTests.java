package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;


@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {

	private static final String END_POINT_PATH = "/v1/locations";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private LocationService service;

	@Test
	public void testAddShouldReturn400BadRequest() throws Exception {
		Location location = new Location();

		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	public void testAddShouldReturn201Created() throws Exception {
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		location.setEnabled(true);

		Mockito.when(service.add(location)).thenReturn(location);
		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
		.andExpect(status().isCreated())
		.andExpect(content().contentType("application/json"))
		.andExpect(jsonPath("$.code", is("NYC_USA")))
		.andExpect(jsonPath("$.city_name", is("New York City")))
		.andExpect(header().string("location", "/v1/locations/NYC_USA"))
		.andDo(print());
	}
	
	@Test
	public void testValidateRequestBodyLocationCodeNotNull() throws Exception {
		Location location = new Location();
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		location.setEnabled(true);
		
		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType("application/json"))
		.andDo(print());
	}
	
	@Test
	public void testValidateRequestBodyLocationCodeLength() throws Exception {
		Location location = new Location();
		location.setCode("");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		location.setEnabled(true);
		
		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType("application/json"))
		.andDo(print());
	}
	
	@Test
	public void testValidateRequestBodyAllFieldsInvalid() throws Exception {
		LocationDto location = new LocationDto();
		
		location.setRegionName("");
		
		String bodyContent = mapper.writeValueAsString(location);

		MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType("application/json"))
		.andDo(print())
		.andReturn();
		
		
		String responseBody = mvcResult.getResponse().getContentAsString();
		
		assertThat(responseBody).contains("Location code cannot be null");
		assertThat(responseBody).contains("City name cannot be null");
		assertThat(responseBody).contains("Region name must have 3-128 characters");
		assertThat(responseBody).contains("Country name cannot be null");
		assertThat(responseBody).contains("Country code cannot be null");
	
	}
 
	@Test
	@Disabled
	public void testListShouldReturn204NoContent() throws Exception {
		Mockito.when(service.list()).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get(END_POINT_PATH)) 
			.andExpect(status().isNoContent())
			.andDo(print());
	
	}
	
	@Test
	@Disabled
	public void testListShouldReturn200Ok() throws Exception {
		Location location1 = new Location();
		location1.setCode("NYC_USA");
		location1.setCityName("New York City");
		location1.setRegionName("New York");
		location1.setCountryCode("US");
		location1.setCountryName("United State of America");
		location1.setEnabled(true);
		
		Location location2 = new Location();
		location2.setCode("LACA_USA");
		location2.setCityName("Los Angeles");
		location2.setRegionName("California");
		location2.setCountryCode("US");
		location2.setCountryName("United State of America");
		location2.setEnabled(true);
		
		Mockito.when(service.list()).thenReturn(List.of(location1, location2));
		
		mockMvc.perform(get(END_POINT_PATH))
		.andExpect(status().isOk())
		.andExpect(content().contentType("application/json"))
		.andExpect(jsonPath("$[0].code", is("NYC_USA")))
		.andExpect(jsonPath("$[0].city_name", is("New York City")))
		.andDo(print());
	}
	
	@Test
	public void testListByPageShouldReturn204NoContent() throws Exception {
		Mockito.when(service.listByPage(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString())).thenReturn(Page.empty());
		
		mockMvc.perform(get(END_POINT_PATH)) 
			.andExpect(status().isNoContent())
			.andDo(print());
	
	}
	
	@Test
	public void testListByPageShouldReturn200Ok() throws Exception {
		Location location1 = new Location();
		location1.setCode("NYC_USA");
		location1.setCityName("New York City");
		location1.setRegionName("New York");
		location1.setCountryCode("US");
		location1.setCountryName("United State of America");
		location1.setEnabled(true);
		
		Location location2 = new Location();
		location2.setCode("LACA_USA");
		location2.setCityName("Los Angeles");
		location2.setRegionName("California");
		location2.setCountryCode("US");
		location2.setCountryName("United State of America");
		location2.setEnabled(true);
		
		List<Location> listLocations = List.of(location1, location2);
		
		int pageSize = 5;
		int pageNum = 1;
		String sortField = "code";
		int totalElements = listLocations.size();
		
		Sort sort = Sort.by(sortField);
		
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		Page<Location> page = new PageImpl<>(listLocations, pageable, totalElements);
		
		
		Mockito.when(service.listByPage(pageNum - 1, pageSize, sortField)).thenReturn(page);
		
		String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
		
		mockMvc.perform(get(requestURI))
		.andExpect(status().isOk())
		.andExpect(content().contentType("application/hal+json"))
		.andExpect(jsonPath("$._embedded.locations[0].code", is("NYC_USA")))
		.andExpect(jsonPath("$.page.size", is(pageSize)))
		.andExpect(jsonPath("$.page.number", is(pageNum)))
		.andExpect(jsonPath("$.page.total_elements", is(7)))
		.andExpect(jsonPath("$.page.total_pages", is(1)))
		.andDo(print());
	}
	
	@Test
	public void testListByPageShouldReturn400BadRequestInvalidPageNum() throws Exception {
		int pageNum = 0;
		int pageSize = 5;
		String sortField = "code";
		
		String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
		
		Mockito.when(service.listByPage(pageNum, pageSize, sortField)).thenReturn(Page.empty());
		
		mockMvc.perform(get(requestURI)) 
			.andExpect(status().isBadRequest())
			.andDo(print());
	
	}
	
	
	@Test
	public void testShouldReturn405MethodNotAllowed() throws Exception {
		String requestURI = END_POINT_PATH + "/ABCDEF";
		
		mockMvc.perform(post(requestURI))
			.andExpect(status().isMethodNotAllowed())
			.andDo(print());
	}
	
	@Test
	public void testShouldReturn404NotFound() throws Exception {
		
			String code = "ABCDEF";
			
			String requestURI = END_POINT_PATH + "/" + code;
			
			Mockito.when(service.get(code)).thenThrow(LocationNotFoundException.class);
			
			mockMvc.perform(get(requestURI))
				.andExpect(status().isNotFound())
				.andDo(print());
	}
	
	@Test
	public void testShouldReturn200Ok() throws Exception {
		
		String code = "LACA_USA";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Location location = new Location();
		location.setCode("LACA_USA");
		location.setCityName("Los Angeles");
		location.setRegionName("California");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		location.setEnabled(true);
		
		Mockito.when(service.get(code)).thenReturn(location);
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.code", is(code)))
			.andExpect(jsonPath("$.city_name", is("Los Angeles")))
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		LocationDto location = new LocationDto();
		location.setCode("LACA_USA");
		location.setCityName("Los Angeles");
		location.setRegionName("California");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		location.setEnabled(true);
		
		
		Mockito.when(service.update(location)).thenThrow(new LocationNotFoundException("No location found"));
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		Location location = new Location();
		location.setCityName("Los Angeles");
		location.setRegionName("California");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		location.setEnabled(true);
				
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn200Ok() throws Exception {
		LocationDto location = new LocationDto();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United State of America");
		location.setEnabled(true);

		Mockito.when(service.update(location)).thenReturn(location);
		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(put(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.code", is("NYC_USA")))
		.andExpect(jsonPath("$.city_name", is("New York City")))
		.andDo(print());
	}
	
	@Test
	public void testDeleteShouldReturn404NotFound() throws Exception {
		String code = "LACA_USA";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Mockito.doThrow(LocationNotFoundException.class).when(service).delete(code);
		
		mockMvc.perform(delete(requestURI))
			.andExpect(status().isNotFound())
			.andDo(print());
			
	}
	
	@Test
	public void testDeleteShouldReturn204NoContent() throws Exception {
		String code = "LACA_USA";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Mockito.doNothing().when(service).delete(code);
		
		mockMvc.perform(delete(requestURI))
			.andExpect(status().isNoContent())
			.andDo(print());
			
	}
}
