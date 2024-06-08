package com.skyapi.weatherforecast.base;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"locationUrl", "locationByCode", "realtimeWeatherByIpUrl", "realtimeWeatherByCodeUrl",
					"hourlyForecastByIpUrl", "hourlyForecastByCodeUrl", "dailyForecastByIpUrl",
					"dailyForecastByCodeUrl", "fullWeatherByIpUrl", "fullWeatherByCodeUrl"})
public class RootEntity {
	
	@JsonProperty("locations_url")
	private String locationUrl;
	
	@JsonProperty("location_by_code_url")
	private String locationByCode;
	
	@JsonProperty("realtime_weather_by_ip_url")
	private String realtimeWeatherByIpUrl;
	
	@JsonProperty("realtime_weather_by_code_url")
	private String realtimeWeatherByCodeUrl;
	
	@JsonProperty("hourly_forecast_by_ip_url")
	private String hourlyForecastByIpUrl;
	
	@JsonProperty("hourly_forecast_by_code_url")
	private String hourlyForecastByCodeUrl;
	
	@JsonProperty("daily_forecast_by_ip_url")
	private String dailyForecastByIpUrl;
	
	@JsonProperty("daily_forecast_by_code_url")
	private String dailyForecastByCodeUrl;
	
	@JsonProperty("full_weather_by_ip_url")
	private String fullWeatherByIpUrl;
	
	@JsonProperty("full_weather_by_code_url")
	private String fullWeatherByCodeUrl;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RootEntity other = (RootEntity) obj;
		return Objects.equals(locationByCode, other.locationByCode) && Objects.equals(locationUrl, other.locationUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(locationByCode, locationUrl);
	}
	
	

}
