package com.sinotrans.blankbill.controller;

import com.sinotrans.blankbill.dto.*;
import com.sinotrans.blankbill.service.SerialNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/operator-logs")
@RequiredArgsConstructor
@Validated
public class OperatorLogController {

    private final SerialNumberService serialNumberService;

    // T26: 序列号操作日志接口
    @GetMapping("/{id}/logs")
    public ResponseEntity<java.util.List<SerialNumberLogItem>> getLogs(@PathVariable Long id) {
        return ResponseEntity.ok(serialNumberService.getLogs(id));
    }
}
