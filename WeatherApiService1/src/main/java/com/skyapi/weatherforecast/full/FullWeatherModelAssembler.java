package com.skyapi.weatherforecast.full;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.skyapi.weatherforecast.GeoLocationException;

@Component
public class FullWeatherModelAssembler implements RepresentationModelAssembler<FullWeatherDto, EntityModel<FullWeatherDto>> {

	@Override
	public EntityModel<FullWeatherDto> toModel(FullWeatherDto dto) {
		EntityModel<FullWeatherDto> entityModel = EntityModel.of(dto);

		try {
			entityModel.add(
					linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).withSelfRel());
		} catch (GeoLocationException e) {
			e.printStackTrace();
		}
		return entityModel;
	}

}
