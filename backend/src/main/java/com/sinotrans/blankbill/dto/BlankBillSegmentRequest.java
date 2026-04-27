package com.sinotrans.blankbill.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BlankBillSegmentRequest {
    @NotNull
    private Long carrierId;
    @NotBlank
    @Pattern(regexp = "^\\d{8}$")
    private String startNumber;
    @NotBlank
    @Pattern(regexp = "^\\d{8}$")
    private String endNumber;
    @NotNull
    @Min(1)
    @Max(99999)
    private Integer quantity;
    private String remark;
    @NotBlank
    private String entryDate;
}
