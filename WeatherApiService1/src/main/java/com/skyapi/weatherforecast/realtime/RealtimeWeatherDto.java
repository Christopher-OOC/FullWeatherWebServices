package com.skyapi.weatherforecast.realtime;

import java.util.Date;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"location", "temperature", "humidity", "precipitation", "windSpeed", "status", "lastUpdated"})
public class RealtimeWeatherDto extends RepresentationModel<RealtimeWeatherDto> {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String location;
	
	private int temperature;
	private int humidity;
	private int precipitation;
	
	@JsonProperty("wind_speed")
	private int windSpeed;
	
	private String status;
	
	@JsonProperty("last_updated")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date lastUpdated;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RealtimeWeatherDto other = (RealtimeWeatherDto) obj;
		return Objects.equals(lastUpdated, other.lastUpdated) && Objects.equals(location, other.location);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(lastUpdated, location);
		return result;
	}
	
	
	

}
