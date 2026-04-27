package com.sinotrans.blankbill.controller;

import com.sinotrans.ai.common.ApiResponse;
import com.sinotrans.ai.dto.SerialNumberVoidRequest;
import com.sinotrans.ai.dto.SerialNumberVoidResponse;
import com.sinotrans.blankbill.dto.*;
import com.sinotrans.blankbill.service.SerialNumberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/serial-numbers")
@RequiredArgsConstructor
@Validated
public class SerialNumberController {

    private final SerialNumberService serialNumberService;

    // T21: 序列号多条件查询接口
    @GetMapping
    public ResponseEntity<SerialNumberListResponse> getSerialNumbers(SerialNumberListQuery query) {
        return ResponseEntity.ok(serialNumberService.getSerialNumbers(query));
    }

    // T30: 批量回收接口
    @PostMapping("/recover")
    public ResponseEntity<SerialNumberRecoverResponse> recover(@RequestBody SerialNumberRecoverRequest request) {
        return ResponseEntity.ok(serialNumberService.recover(request));
    }

    // T25: 序列号详情接口
    @GetMapping("/{id}")
    public ResponseEntity<SerialNumberDetailResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(serialNumberService.getDetail(id));
    }

    // T26: 序列号操作日志接口
    @GetMapping("/{id}/logs")
    public ResponseEntity<java.util.List<SerialNumberLogItem>> getLogs(@PathVariable Long id) {
        return ResponseEntity.ok(serialNumberService.getLogs(id));
    }

    @PostMapping("/void")
    public ApiResponse<SerialNumberVoidResponse> voidSerialNumbers(@Valid @RequestBody SerialNumberVoidRequest request) {
        SerialNumberVoidResponse resp = serialNumberService.voidSerialNumbers(request);
        return ApiResponse.success(resp);
    }

    //     @GetMapping("/void/export")
    // public void exportVoidList(@RequestParam Map<String, Object> params, HttpServletResponse response) throws IOException {
    //     // TODO: 实现作废清单导出，生成Excel并写入response输出流
    //     response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    //     response.setHeader("Content-Disposition", "attachment; filename=void-list.xlsx");
    //     // response.getOutputStream().write(...)
    // }

}
