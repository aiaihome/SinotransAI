package com.sinotrans.blankbill.controller;

import com.sinotrans.blankbill.dto.CarrierResponse;
import com.sinotrans.blankbill.service.CarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carriers")
@RequiredArgsConstructor
public class CarrierController {

    private final CarrierService carrierService;

    // T6: 船司主数据查询
    @GetMapping
    public ResponseEntity<CarrierResponse> getCarriers(@RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(carrierService.getCarriers(keyword));
    }
}
