package com.sinotrans.blankbill.dto;

import lombok.Data;
import java.util.List;

@Data
public class SerialNumberRecoverResponse {
    private boolean success;
    private String message;
    private DataObj data;

    @Data
    public static class DataObj {
        private List<Long> recoveredIds;
        private Integer count;
    }
}
