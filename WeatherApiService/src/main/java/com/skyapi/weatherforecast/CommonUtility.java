package com.skyapi.weatherforecast;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtility {
    private static final Logger LOGGER= LoggerFactory.getLogger(CommonUtility.class);
    public static String getIPAddress(HttpServletRequest request){
        String ip=request.getHeader("X-FORWARDED-FOR");
        if(ip==null || ip.isEmpty()){
            ip=request.getRemoteAddr();
        }
        LOGGER.info("Client's IP Address: "+ip);
        return ip;
    }
}
