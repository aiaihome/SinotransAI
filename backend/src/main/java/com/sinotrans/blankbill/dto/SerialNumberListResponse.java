package com.sinotrans.blankbill.dto;

import lombok.Data;
import java.util.List;

@Data
public class SerialNumberListResponse {
    private Integer total;
    private Integer page;
    private Integer size;
    private List<SerialNumberListItem> serialNumbers;
}
