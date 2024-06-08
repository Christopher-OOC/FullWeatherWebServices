package com.skyapi.weatherforecast.daily;

import java.util.Objects;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({"dayOfMonth", "month", "minTemp", "maxTemp", "precipitation", "status"})
@Data
@NoArgsConstructor
public class DailyWeatherDto {
	
	@JsonProperty("day_of_month")
	@Range(min=1, max=31, message="Day of month must be between 1-31")
	private int dayOfMonth;
	
	@Range(min=1, max=12, message="Month must be between 1-12")
	private int month;
	
	@JsonProperty("min_temp")
	@Range(min=-50, max=50, message="Minimum temperature must be in the range od -50 to 50 Celsius degree")
	private int minTemp;
	
	@JsonProperty("max_temp")
	@Range(min=-50, max=50, message="Minimum temperature must be in the range od -50 to 50 Celsius degree")
	private int maxTemp;
	
	@Range(min=0, max=100, message="Precipitation must be in the range of 0 to 100 percentage")
	private int precipitation;
	
	@Length(min=3, max=50, message="Status must be in between 3-50 characters")
	private String status;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DailyWeatherDto other = (DailyWeatherDto) obj;
		return dayOfMonth == other.dayOfMonth && maxTemp == other.maxTemp && minTemp == other.minTemp;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dayOfMonth, maxTemp, minTemp);
	}
	
	

}
