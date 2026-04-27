package com.sinotrans.blankbill.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BlankBillSegmentCheckRequest {
    @NotNull
    private Long carrierId;
    @NotBlank
    @Pattern(regexp = "^\\d{8}$")
    private String startNumber;
    @NotBlank
    @Pattern(regexp = "^\\d{8}$")
    private String endNumber;
}
