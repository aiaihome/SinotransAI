package com.sinotrans.blankbill.service.impl;

import com.sinotrans.blankbill.dto.CarrierResponse;
import com.sinotrans.blankbill.service.CarrierService;
import org.springframework.stereotype.Service;

@Service
public class CarrierServiceImpl implements CarrierService {
    @Override
    public CarrierResponse getCarriers(String keyword) {
        // 模拟船司主数据
        CarrierResponse response = new CarrierResponse();
        response.setCarriers(java.util.List.of(
            new com.sinotrans.blankbill.dto.CarrierDTO(1L, "中远海运", "COSCO"),
            new com.sinotrans.blankbill.dto.CarrierDTO(2L, "马士基", "MAERSK"),
            new com.sinotrans.blankbill.dto.CarrierDTO(3L, "地中海航运", "MSC")
        ));
        // 简单关键字过滤
        if (keyword != null && !keyword.isBlank()) {
            response.setCarriers(
                response.getCarriers().stream()
                    .filter(c -> c.getName().contains(keyword) || c.getCode().contains(keyword))
                    .toList()
            );
        }
        return response;
    }
}
