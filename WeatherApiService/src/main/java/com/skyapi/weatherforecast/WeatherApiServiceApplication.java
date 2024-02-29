package com.skyapi.weatherforecast;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;

@SpringBootApplication
@ComponentScan(basePackages={"com.skyapi.weatherforecast"})
public class WeatherApiServiceApplication {
	@Bean
	public ModelMapper getModelMapper(){
		ModelMapper mapper=new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		var typeMap1=mapper.typeMap(HourlyWeather.class, HourlyWeatherDTO.class);
		typeMap1.addMapping(src->src.getId().getHourOfDay(), HourlyWeatherDTO::setHourOfDay);
		var typeMap2=mapper.typeMap(HourlyWeatherDTO.class, HourlyWeather.class);
		typeMap2.addMapping(src->src.getHourOfDay(), (desc, value)->desc.getId().setHourOfDay(value!=null ? (int)value : 0));
		return mapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(WeatherApiServiceApplication.class, args);
	}

}
