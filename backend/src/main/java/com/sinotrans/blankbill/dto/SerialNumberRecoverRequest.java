package com.sinotrans.blankbill.dto;

import lombok.Data;
import java.util.List;

@Data
public class SerialNumberRecoverRequest {
    private List<Long> ids;
    private String remark;
}
