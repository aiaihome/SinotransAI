package com.sinotrans.blankbill.dto;

import lombok.Data;

@Data
public class SerialNumberListItem {
    private Long id;
    private String billNumber;
    private String status;
    private String entryDate;
    private String remark;
}
