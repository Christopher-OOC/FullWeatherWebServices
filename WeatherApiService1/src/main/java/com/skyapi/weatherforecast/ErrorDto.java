package com.skyapi.weatherforecast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ErrorDto {
	private Date timestamp;
	private int status;
	private String path;
	private List<String> errors = new ArrayList<>();
	
	public void addError(String message) {
		this.errors.add(message);
	}

}
