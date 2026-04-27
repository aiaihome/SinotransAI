package com.sinotrans.blankbill.dto;

import lombok.Data;

@Data
public class SerialNumberListQuery {
    private Long segmentId;
    private String billNumber;
    private String entryDateFrom;
    private String entryDateTo;
    private String status;
    private Integer page = 1;
    private Integer size = 20;
}
