package com.sinotrans.blankbill.dto;

import lombok.Data;
import java.util.List;

@Data
public class SerialNumberDetailResponse {
    private Long id;
    private String billNumber;
    private String status;
    private String entryDate;
    private String remark;
    private List<SerialNumberLogItem> history;

    @Data
    public static class SerialNumberLogItem {
        private Long id;
        private String action;
        private String operator;
        private String operateTime;
        private Object beforeSnapshot;
        private Object afterSnapshot;
        private String remark;
    }
}
