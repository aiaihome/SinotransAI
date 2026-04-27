package com.sinotrans.blankbill.dto;

import lombok.Data;

@Data
public class SerialNumberLogItem {
    private Long id;
    private String action;
    private String operator;
    private String operateTime;
    private Object beforeSnapshot;
    private Object afterSnapshot;
    private String remark;
}
