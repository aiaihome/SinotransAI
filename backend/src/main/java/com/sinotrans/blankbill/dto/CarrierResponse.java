package com.sinotrans.blankbill.dto;

import lombok.Data;
import java.util.List;

@Data
public class CarrierResponse {
    private List<CarrierDTO> carriers;

    @lombok.Data
    public static class Carrier {
        private Long id;
        private String name;
    }
}
