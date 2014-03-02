package edu.sjsu.cmpe273.assignment1.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * User: maksim
 * Date: 2/22/14 - 2:49 PM
 */

@JsonFormat(shape= JsonFormat.Shape.OBJECT)
public enum BookStatus {


    AVAILABLE("available"),
    CHECKED_OUT("checked-out"),
    IN_QUEUE("in-queue"),
    LOST("lost");

    private String id;

    private BookStatus(String id) {
        this.id = id;
    }

    @JsonValue()
    public String getId() {
        return id;
    }
}
