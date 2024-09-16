package com.kibuti.flowercounterbackend.GlobeResponseBody;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class GlobalJsonResponseBody {
    private Boolean status;
    private HttpStatus httpStatus;
    private String message;
    private Date action_time;
    private Object data;
}
