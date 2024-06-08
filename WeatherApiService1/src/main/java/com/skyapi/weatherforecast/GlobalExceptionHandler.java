package com.skyapi.weatherforecast;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.skyapi.weatherforecast.location.LocationNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorDto handleGenericException(HttpServletRequest request, Exception ex) {
		ErrorDto error = new ErrorDto();
		
		error.setTimestamp(new Date());
		error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.addError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		error.setPath(request.getServletPath());
		
		
		log.error("error", ex);
		return error;
	}
	
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorDto handleBadRequestException(HttpServletRequest request, Exception ex) {
		ErrorDto error = new ErrorDto();
		
		error.setTimestamp(new Date());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.addError(ex.getMessage());
		error.setPath(request.getServletPath());
		
		
		log.error("error", ex);
		return error;
	}
	
	@ExceptionHandler(GeoLocationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorDto handleBadRequestException(HttpServletRequest request, GeoLocationException ex) {
		ErrorDto error = new ErrorDto();
		
		error.setTimestamp(new Date());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.addError(ex.getMessage());
		error.setPath(request.getServletPath());
		
		
		log.error("error", ex);
		return error;
	}

	@ExceptionHandler(LocationNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorDto handleLocationNotFoundException(HttpServletRequest request, Exception ex) {
		ErrorDto error = new ErrorDto();
		
		error.setTimestamp(new Date());
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.addError(ex.getMessage());
		error.setPath(request.getServletPath());
		
		
		log.error("error", ex);
		return error;
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorDto handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
		ErrorDto error = new ErrorDto();
		
		error.setTimestamp(new Date());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setPath(request.getServletPath());
		
		ex.getConstraintViolations().forEach(cv -> {
			error.addError(cv.getMessage());
		});
		
		
		return error;
		
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		ErrorDto error = new ErrorDto();
		
		error.setTimestamp(new Date());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setPath(((ServletWebRequest) request).getRequest().getServletPath());
		
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		
		fieldErrors.forEach(fieldError -> {
			error.addError(fieldError.getDefaultMessage());
		});
		
		log.error("error", ex);
		return new ResponseEntity<>(error, headers, status);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		ErrorDto error = new ErrorDto();
		
		error.setTimestamp(new Date());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setPath(((ServletWebRequest) request).getRequest().getServletPath());
		error.addError("The response body does not contain proper field name");
		
		log.error("error", ex);
		return new ResponseEntity<>(error, headers, status);
		
	}
	
}
