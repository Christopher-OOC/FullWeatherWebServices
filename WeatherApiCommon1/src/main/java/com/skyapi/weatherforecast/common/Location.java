package com.skyapi.weatherforecast.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="locations")
@Data
@NoArgsConstructor
public class Location {
	
	@Column(length=12, nullable=false, unique=true)
	@Id
	private String code;
	
	@Column(length=128, nullable=false)
	private String cityName;
	
	@Column(length=128)
	private String regionName;
	
	@Column(length=64, nullable=false)
	private String countryName;
	
	@Column(length=2, nullable=false)
	private String countryCode;
	
	private boolean enabled;
	
	private boolean trashed;
	
	@OneToOne(mappedBy="location", cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn
	private RealtimeWeather realtimeWeather;
	
	@OneToMany(mappedBy="id.location", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<HourlyWeather> listHourlyWeather = new ArrayList<>();

	@OneToMany(mappedBy="id.location", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<DailyWeather> listDailyWeather = new ArrayList<>();
	
	public Location(String cityName, String regionName, String countryName, String countryCode) {
			
		super();
		this.cityName = cityName;
		this.regionName = regionName;
		this.countryName = countryName;
		this.countryCode = countryCode;
	}
	

	public Location(String code, String cityName, String regionName, String countryName, String countryCode,
			boolean enabled) {
		super();
		this.code = code;
		this.cityName = cityName;
		this.regionName = regionName;
		this.countryName = countryName;
		this.countryCode = countryCode;
		this.enabled = enabled;
	}
	
	public Location code(String code) {
		setCode(code);
		return this;
	}
	
	public String toString() {
		return cityName + ", " + (regionName != null ? regionName + ", " : "") + ", " + countryName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return Objects.equals(code, other.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}	

}
