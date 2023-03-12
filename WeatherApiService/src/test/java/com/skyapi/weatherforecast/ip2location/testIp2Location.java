package com.skyapi.weatherforecast.ip2location;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class testIp2Location {
    public final String DBPath="ip2locdb/IP2LOCATION-LITE-DB3.BIN";

    @Test
    public void testInvalidIpLocation() throws IOException {
        String ip="abc";
        IP2Location ip2Location=new IP2Location();
        ip2Location.Open(DBPath);
        IPResult result= ip2Location.IPQuery(ip);
        assert(result.getStatus()).equals("INVALID_IP_ADDRESS");
        System.out.println(result);
    }

    @Test
    public void testValidIpLocation() throws IOException {
        String ip="210.138.184.59";
        IP2Location ip2Location=new IP2Location();
        ip2Location.Open(DBPath);
        IPResult result= ip2Location.IPQuery(ip);
        assert(result.getCity()).equals("Tokyo");
        System.out.println(result);
    }
}
