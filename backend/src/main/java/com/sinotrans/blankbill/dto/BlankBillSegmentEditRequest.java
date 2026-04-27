package com.sinotrans.blankbill.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlankBillSegmentEditRequest {
    private String remark;
    @NotBlank
    private String entryDate;
}
