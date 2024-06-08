package com.skyapi.weatherforecast.location;

import java.util.Objects;

import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"code", "cityName", "regionName", "countryName", "countryCode", "enabled"})
@Relation(collectionRelation="locations")
public class LocationDto extends CollectionModel<LocationDto> {

	@NotNull(message="Location code cannot be null")
	@Length(min=3, max=12, message="Location code must have 3-12 characters")
	private String code;
	
	@JsonProperty("city_name")
	@NotNull(message="City name cannot be null")
	@Length(min=3, max=128, message="City name must have 3-128 characters")
	private String cityName;
	
	@JsonProperty("region_name")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Length(min=3, max=128, message="Region name must have 3-128 characters")
	private String regionName;
	
	@JsonProperty("country_name")
	@NotNull(message="Country name cannot be null")
	@Length(min=3, max=64, message="Country name must have 3-64 characters")
	private String countryName;
	
	@JsonProperty("country_code")
	@NotNull(message="Country code cannot be null")
	@Length(min=2, max=2, message="Country code must have 2 characters")
	private String countryCode;
	
	private boolean enabled;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocationDto other = (LocationDto) obj;
		return Objects.equals(code, other.code);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(code);
		return result;
	}
	
	
}
