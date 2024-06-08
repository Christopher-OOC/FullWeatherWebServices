package com.skyapi.weatherforecast.daily;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DailyWeatherListDto {
	
	private String location;
	
	@JsonProperty("daily_forecast")
	private List<DailyWeatherDto> dailyForecast = new ArrayList<>();

	public void addDailyWeatherDto(DailyWeatherDto dto) {
		this.dailyForecast.add(dto);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DailyWeatherListDto other = (DailyWeatherListDto) obj;
		return Objects.equals(location, other.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(location);
	}
	
	

}
