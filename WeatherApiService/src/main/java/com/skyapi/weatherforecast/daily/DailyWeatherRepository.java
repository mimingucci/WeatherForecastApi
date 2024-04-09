package com.skyapi.weatherforecast.daily;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.DailyWeatherId;
@Repository
public interface DailyWeatherRepository extends CrudRepository<DailyWeather, DailyWeatherId>{

	@Query("""
			SELECT d FROM DailyWeather d WHERE d.id.location.code=?1 AND d.id.location.trashed=false
			""")
	public List<DailyWeather> findByLocationCode(String locationCode);
}
