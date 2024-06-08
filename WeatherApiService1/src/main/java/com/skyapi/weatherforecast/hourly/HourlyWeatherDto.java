package com.skyapi.weatherforecast.hourly;

import java.util.Objects;

import org.hibernate.validator.constraints.Length;

import org.hibernate.validator.constraints.Range;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonPropertyOrder({"hourOfDay", "temperature", "precipitation", "status"})
public class HourlyWeatherDto {
	
	@JsonProperty("hour_of_day")
	private int hourOfDay;
	
	@Range(min=-50, max=50, message="Temperature must be in the range -50 to 50")
	private int temperature;
	
	@Range(min=0, max=100, message="Precipitation must be in the range 0 to 100 percentage")
	private int precipitation;
	
	@NotBlank(message="Status must not be empty")
	@Length(min=0, max=200, message="Status must be in between 3-50 chracters")
	private String status;
	
	public HourlyWeatherDto precipitation(int precipitation) {
		setPrecipitation(precipitation);
		
		return this;
	}
	
	public HourlyWeatherDto status(String status) {
		setStatus(status);
		
		return this;
	}
	
	public HourlyWeatherDto hourOfDay(int hour) {
		setHourOfDay(hour);
		
		return this;
	}
	
	public HourlyWeatherDto temperature(int temp) {
		setTemperature(temp);
		
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HourlyWeatherDto other = (HourlyWeatherDto) obj;
		return hourOfDay == other.hourOfDay && precipitation == other.precipitation
				&& Objects.equals(status, other.status) && temperature == other.temperature;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(hourOfDay, precipitation, status, temperature);
		return result;
	}
	
	

}
