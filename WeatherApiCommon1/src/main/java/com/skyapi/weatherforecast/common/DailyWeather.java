package com.skyapi.weatherforecast.common;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="weather_daily")
public class DailyWeather {
	
	@EmbeddedId
	private DailyWeatherId id = new DailyWeatherId();

	private int minTemp;
	private int maxTemp;
	private int precipitation;
	
	@Column(length=50)
	private String status;
	
	public DailyWeather getShallowCopy() {
		DailyWeather copy = new DailyWeather();
		copy.setId(this.getId());
		
		return copy;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DailyWeather other = (DailyWeather) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	public DailyWeather precipitation(int precipitation) {
		setPrecipitation(precipitation);
		
		return this;
	}
	
	public DailyWeather status(String status) {
		setStatus(status);
		
		return this;
	}
	
	public DailyWeather location(Location location) {
		this.id.setLocation(location);
		
		return this;
	}
	
	public DailyWeather dayOfMonth(int day) {
		this.id.setDayOfMonth(day);
		
		return this;
	}
	
	public DailyWeather month(int month) {
		this.id.setMonth(month);
		
		return this;
	}
	
	public DailyWeather minTemp(int temp) {
		this.setMinTemp(temp);
		
		return this;
	}
	
	public DailyWeather maxTemp(int temp) {
		this.setMaxTemp(temp);
		
		return this;
	}
}
