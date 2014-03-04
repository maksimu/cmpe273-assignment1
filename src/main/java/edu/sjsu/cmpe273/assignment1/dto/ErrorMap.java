package edu.sjsu.cmpe273.assignment1.dto;

import java.util.HashMap;

/**
 * User: maksim
 * Date: 3/2/14 - 2:59 PM
 */
public class ErrorMap extends HashMap<String, Object> {

    public ErrorMap(String errMessage, String httpStatus){
        this.put("error", errMessage);
        this.put("errorStatus", httpStatus);
    }
}
