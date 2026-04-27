package com.sinotrans.blankbill.dto;

import lombok.Data;

@Data
public class BlankBillSegmentListItem {
    private Long segmentId;
    private String carrierName;
    private String startNumber;
    private String endNumber;
    private Integer quantity;
    private String remark;
    private String entryDate;
    private Boolean archived;
}
