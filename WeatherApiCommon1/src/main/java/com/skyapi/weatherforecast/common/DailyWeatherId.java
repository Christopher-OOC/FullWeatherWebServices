package com.skyapi.weatherforecast.common;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DailyWeatherId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int dayOfMonth;
	private int month;
	
	@ManyToOne
	@JoinColumn(name="location_code")
	private Location location;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DailyWeatherId other = (DailyWeatherId) obj;
		return dayOfMonth == other.dayOfMonth && Objects.equals(location, other.location) && month == other.month;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dayOfMonth, location, month);
	}

	
}
