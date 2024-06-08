package com.skyapi.weatherforecast.full;

import com.skyapi.weatherforecast.realtime.RealtimeWeatherDto;

public class RealtimeWeatherFieldFilter {
	
	public boolean equals(Object object) {
		
		if (object instanceof RealtimeWeatherDto) {
			RealtimeWeatherDto dto = (RealtimeWeatherDto) object;
			return dto.getStatus() == null;
		}
		
		return false;
	}

}
