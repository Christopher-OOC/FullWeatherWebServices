package com.skyapi.weatherforecast;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.daily.DailyWeatherDto;
import com.skyapi.weatherforecast.full.FullWeatherDto;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDto;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherDto;

@SpringBootApplication
public class WeatherApiService1Application {

	public static void main(String[] args) {
		SpringApplication.run(WeatherApiService1Application.class, args);
	}
	
	@Bean
	ModelMapper getModelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);		
		
		configureMappingForHourlyWeather(mapper);
		
		configureMappingForDailyWeather(mapper);
		
		configureMappingForFullWeather(mapper);
		
		configureMappingForRealtimeWeather(mapper);
		
		return mapper;
	}
	
	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		
		return objectMapper;
	}

	private void configureMappingForRealtimeWeather(ModelMapper mapper) {
		mapper.typeMap(RealtimeWeatherDto.class, RealtimeWeather.class)
			.addMappings(m -> m.skip(RealtimeWeather::setLocation));
	}

	private void configureMappingForFullWeather(ModelMapper mapper) {
		mapper.typeMap(Location.class, FullWeatherDto.class)
			.addMapping(src -> src.toString(), FullWeatherDto::setLocation);
	}

	private void configureMappingForDailyWeather(ModelMapper mapper) {
		mapper.typeMap(DailyWeather.class, DailyWeatherDto.class)
			.addMapping(src -> src.getId().getDayOfMonth(), DailyWeatherDto::setDayOfMonth)
			.addMapping(src -> src.getId().getMonth(), DailyWeatherDto::setMonth);
		
		mapper.typeMap(DailyWeatherDto.class, DailyWeather.class)
			.addMapping(src -> src.getDayOfMonth(), (desc, value) -> desc.getId().setDayOfMonth(value != null ? (int) value : 0))
			.addMapping(src -> src.getMonth(), (desc, value) -> desc.getId().setMonth(value != null ? (int) value : 0));
	}

	private void configureMappingForHourlyWeather(ModelMapper mapper) {
		mapper.typeMap(HourlyWeather.class, HourlyWeatherDto.class)
			.addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDto::setHourOfDay);
		
		mapper.typeMap(HourlyWeatherDto.class, HourlyWeather.class)
			.addMapping(src -> src.getHourOfDay(), (desc, value) -> desc.getId().setHourOfDay(value != null ? (int) value : 0));
	}

}
