package com.skyapi.weatherforecast.full;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyapi.weatherforecast.daily.DailyWeatherDto;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDto;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherDto;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FullWeatherDto {
	
	private String location;
	
	@JsonProperty("realtime_weather")
	@JsonInclude(value=JsonInclude.Include.CUSTOM, valueFilter=RealtimeWeatherFieldFilter.class)
	@Valid
	private RealtimeWeatherDto realtimeWeather = new RealtimeWeatherDto();

	@JsonProperty("hourly_forecast")
	@Valid
	private List<HourlyWeatherDto> listHourlyWeather = new ArrayList<>();
	
	@JsonProperty("daily_forecast")
	@Valid
	private List<DailyWeatherDto> listDailyWeather = new ArrayList<>();
	
	
}
