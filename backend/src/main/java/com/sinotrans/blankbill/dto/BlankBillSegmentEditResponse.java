package com.sinotrans.blankbill.dto;

import lombok.Data;

@Data
public class BlankBillSegmentEditResponse {
    private boolean success;
    private String message;
    private DataObj data;

    @lombok.Data
    public static class DataObj {
        private Long segmentId;
        private String remark;
        private String entryDate;
    }
}
