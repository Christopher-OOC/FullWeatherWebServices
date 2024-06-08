package com.skyapi.weatherforecast.hourly;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"location", "hourlyForecast"})
public class HourlyWeatherListDto extends RepresentationModel<HourlyWeatherListDto> {
	
	private String location;
	
	@JsonProperty("hourly_forecast")
	private List<HourlyWeatherDto> hourlyForecast = new ArrayList<>();

	public void addWeatherHourlyDto(HourlyWeatherDto dto) {
		this.hourlyForecast.add(dto);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HourlyWeatherListDto other = (HourlyWeatherListDto) obj;
		return Objects.equals(hourlyForecast, other.hourlyForecast) && Objects.equals(location, other.location);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(hourlyForecast, location);
		return result;
	}
	
	
}
