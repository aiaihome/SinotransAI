package com.sinotrans.ai.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class SerialNumberVoidRequest {
    @NotEmpty(message = "作废ID列表不能为空")
    private List<Long> ids;

    @NotEmpty(message = "作废原因不能为空")
    private String reason;

    private String remark;
}
