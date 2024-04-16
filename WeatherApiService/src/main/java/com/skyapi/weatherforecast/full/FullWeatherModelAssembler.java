package com.skyapi.weatherforecast.full;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.daily.DailyWeatherApiController;
import com.skyapi.weatherforecast.daily.DailyWeatherListDTO;

@Component
public class FullWeatherModelAssembler implements RepresentationModelAssembler<FullWeatherDTO, EntityModel<FullWeatherDTO>> {

	@Override
	public EntityModel<FullWeatherDTO> toModel(FullWeatherDTO dto) {
		EntityModel<FullWeatherDTO> entityModel=EntityModel.of(dto);
		try {
			entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).withSelfRel());
		} catch (GeolocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return entityModel;
	}

}
