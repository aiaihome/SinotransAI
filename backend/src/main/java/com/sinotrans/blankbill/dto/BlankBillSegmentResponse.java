package com.sinotrans.blankbill.dto;

import lombok.Data;
import java.util.List;

@Data
public class BlankBillSegmentResponse {
    private boolean success;
    private String message;
    private Integer quantity;
    private List<String> serialNumbers;
}
