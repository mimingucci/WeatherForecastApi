package com.skyapi.weatherforecast;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weatherforecast.common.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeolocationService {
    private static final Logger LOGGER= LoggerFactory.getLogger(GeolocationService.class);
    private final String DBPath="ip2locdb/IP2LOCATION-LITE-DB3.BIN";
    private IP2Location ip2Location=new IP2Location();

    public GeolocationService(){
        try {
            ip2Location.Open(DBPath);
        } catch (IOException e) {
            LOGGER.error("Could not initially ip2Location!!!");
        }
    }

    public Location getLocation(String ipAddress) throws GeolocationException {
       try {
           IPResult result = ip2Location.IPQuery(ipAddress);
           if(!result.getStatus().equals("OK")){
               throw new GeolocationException("Geolacation failed with status: "+result.getStatus());
           }
           return new Location(result.getCity(), result.getRegion(), result.getCountryLong(), result.getCountryShort());
       } catch (IOException e) {
           throw new GeolocationException("Could not find address from id "+ipAddress);
       }
    }
}
