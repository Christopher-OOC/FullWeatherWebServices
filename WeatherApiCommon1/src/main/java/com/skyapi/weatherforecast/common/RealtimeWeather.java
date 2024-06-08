package com.skyapi.weatherforecast.common;

import java.util.Date;
import java.util.Objects;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name="realtime_weather")
@Data
public class RealtimeWeather {
	
	@Id
	@Column(name="location_code")
	@JsonIgnore
	private String locationCode;
	
	@Range(min=-50, max=50, message="Temperature must be in the range -50 to 50")
	private int temperature;
	
	@Range(min=0, max=100, message="Humidity must be in the range 0 to 100 percentage")
	private int humidity;
	
	@Range(min=0, max=100, message="Precipitation must be in the range 0 to 100 percentage")
	private int precipitation;
	
	@Range(min=0, max=200, message="Wind speed must be in the range 0 to 200 km/h")
	@JsonProperty("wind_speed")
	private int windSpeed;
	
	@NotBlank(message="Status must not be empty")
	@Length(min=0, max=200, message="Status must be in between 3-50 chracters")
	@Column(length=50)
	private String status;
	
	@JsonProperty("last_updated")
	@JsonIgnore 
	private Date lastUpdated;
	
	@OneToOne
	@JoinColumn(name="location_code")
	@MapsId
	@JsonIgnore
	private Location location;
	
	public void setLocation(Location location) {
		this.locationCode = location.getCode();
		this.location = location;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RealtimeWeather other = (RealtimeWeather) obj;
		return Objects.equals(locationCode, other.locationCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(locationCode);
	}
	
	

}
