package com.sinotrans.blankbill.dto;

import lombok.Data;

@Data
public class BlankBillSegmentListQuery {
    private Long carrierId;
    private String startNumber;
    private String endNumber;
    private String entryDateFrom;
    private String entryDateTo;
    private Integer page = 1;
    private Integer size = 20;
    private String sort;
}
