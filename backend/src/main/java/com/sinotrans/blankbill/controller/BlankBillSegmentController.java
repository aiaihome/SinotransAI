package com.sinotrans.blankbill.controller;

import com.sinotrans.blankbill.dto.*;
import com.sinotrans.blankbill.service.BlankBillSegmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/blank-bill-segments")
@RequiredArgsConstructor
@Validated
public class BlankBillSegmentController {

    private final BlankBillSegmentService blankBillSegmentService;

    // T17: 号码段分页与筛选接口
    @GetMapping
    public ResponseEntity<BlankBillSegmentListResponse> getSegments(BlankBillSegmentListQuery query) {
        return ResponseEntity.ok(blankBillSegmentService.getSegments(query));
    }

    // T4: 入库接口
    @PostMapping
    public ResponseEntity<BlankBillSegmentResponse> createSegment(@RequestBody @Validated BlankBillSegmentRequest request) {
        return ResponseEntity.ok(blankBillSegmentService.createSegment(request));
    }

    // T5: 号码段交叉校验
    @PostMapping("/check")
    public ResponseEntity<BlankBillSegmentCheckResponse> checkSegment(@RequestBody @Validated BlankBillSegmentCheckRequest request) {
        return ResponseEntity.ok(blankBillSegmentService.checkSegment(request));
    }

    // T10: 编辑号码段（仅允许备注、入库日期修改）
    @PutMapping("/{segmentId}")
    public ResponseEntity<BlankBillSegmentEditResponse> editSegment(
            @PathVariable Long segmentId,
            @RequestBody @Validated BlankBillSegmentEditRequest request) {
        return ResponseEntity.ok(blankBillSegmentService.editSegment(segmentId, request));
    }

    // T13: 自动归档接口
    @PatchMapping("/archive")
    public ResponseEntity<BlankBillSegmentArchiveResponse> archiveSegments() {
        return ResponseEntity.ok(blankBillSegmentService.archiveSegments());
    }
}
