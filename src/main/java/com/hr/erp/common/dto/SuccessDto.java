package com.hr.erp.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hr.erp.common.enums.SuccessStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SuccessDto <T> {
    String status;
    T data;

    @JsonCreator
    public SuccessDto(@JsonProperty("status") SuccessStatus successStatus,
                      @JsonProperty("data") T data) {
        this.status = successStatus.name();
        this.data = data;
    }
}
