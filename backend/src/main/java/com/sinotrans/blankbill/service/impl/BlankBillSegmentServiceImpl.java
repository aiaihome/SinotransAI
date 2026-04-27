package com.sinotrans.blankbill.service.impl;


import com.sinotrans.blankbill.dto.*;
import com.sinotrans.blankbill.entity.BlankBillSegment;
import com.sinotrans.blankbill.repository.BlankBillSegmentRepository;
import com.sinotrans.blankbill.entity.SerialNumber;
import com.sinotrans.blankbill.repository.SerialNumberRepository;
import com.sinotrans.blankbill.service.BlankBillSegmentService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BlankBillSegmentServiceImpl implements BlankBillSegmentService {
    private final BlankBillSegmentRepository blankBillSegmentRepository;
    private final SerialNumberRepository serialNumberRepository;
    @Override
    public BlankBillSegmentResponse createSegment(BlankBillSegmentRequest request) {
        BlankBillSegmentResponse resp = new BlankBillSegmentResponse();
        // 1. 参数校验
        if (request == null || request.getCarrierId() == null ||
            request.getStartNumber() == null || request.getEndNumber() == null ||
            request.getQuantity() == null || request.getQuantity() <= 0) {
            resp.setSuccess(false);
            resp.setMessage("参数不完整");
            return resp;
        }

        // 2. 号码段交叉校验
        BlankBillSegmentCheckRequest checkReq = new BlankBillSegmentCheckRequest();
        checkReq.setCarrierId(request.getCarrierId());
        checkReq.setStartNumber(request.getStartNumber());
        checkReq.setEndNumber(request.getEndNumber());
        BlankBillSegmentCheckResponse checkResp = checkSegment(checkReq);
        if (checkResp.isConflict()) {
            resp.setSuccess(false);
            resp.setMessage("号码段与已有号段存在交叉");
            // 可选：如需返回冲突区间，建议前端用 checkSegment 单独校验
            return resp;
        }

        // 3. 保存号码段
        BlankBillSegment segment = new BlankBillSegment();
        segment.setCarrierId(request.getCarrierId());
        segment.setStartNumber(request.getStartNumber());
        segment.setEndNumber(request.getEndNumber());
        segment.setQuantity(request.getQuantity());
        segment.setRemark(request.getRemark());
        // 必填字段 portCode 赋值，优先取 request，若无则给默认值
        if (request instanceof com.sinotrans.blankbill.dto.BlankBillSegmentRequest) {
            // String portCode = ((com.sinotrans.blankbill.dto.BlankBillSegmentRequest)request).getPortCode();
            segment.setPortCode("DEFAULT");
            UUID uuid = UUID.randomUUID();
            segment.setSegmentCode(uuid.toString());
            segment.setUsedQuantity(1);
            segment.setVoidQuantity(1);
        } else {
            // segment.setPortCode("DEFAULT");
        }
        // entryDate 字符串转 LocalDate
        if (request.getEntryDate() != null && !request.getEntryDate().isEmpty()) {
            segment.setEntryDate(java.time.LocalDate.parse(request.getEntryDate()));
        } else {
            segment.setEntryDate(null);
        }
        segment.setStatus("ACTIVE");
        blankBillSegmentRepository.save(segment);


        // 4. 生成序列号明细并批量入库
        long start = Long.parseLong(request.getStartNumber());
        long end = Long.parseLong(request.getEndNumber());
        java.util.List<String> serialNumbers = new java.util.ArrayList<>();
        java.util.List<SerialNumber> serialEntities = new java.util.ArrayList<>();
        String carrierCode = request.getCarrierId() != null ? request.getCarrierId().toString() : "";
        for (long i = start; i <= end; i++) {
            String rawNumber = String.format("%08d", i);
            String serialNumber = carrierCode.isEmpty() ? rawNumber : carrierCode + "-" + rawNumber;
            serialNumbers.add(serialNumber);

            SerialNumber sn = new SerialNumber();
            sn.setSegmentId(segment.getSegmentId());
            sn.setShippingCompanyId(request.getCarrierId());
            sn.setSerialNumber(serialNumber);
            sn.setRawNumber(rawNumber);
            sn.setStatus("NEW");
            sn.setEntryDate(segment.getEntryDate());
            sn.setRemark(segment.getRemark());
            serialEntities.add(sn);
        }
        serialNumberRepository.saveAll(serialEntities);

        // 5. 返回结果
        resp.setSuccess(true);
        resp.setMessage("入库成功");
        resp.setQuantity(serialNumbers.size());
        resp.setSerialNumbers(serialNumbers);
        return resp;
    }

    @Override
    public BlankBillSegmentCheckResponse checkSegment(BlankBillSegmentCheckRequest request) {
        // 号码段交叉校验逻辑
        BlankBillSegmentCheckResponse resp = new BlankBillSegmentCheckResponse();
        if (request == null || request.getCarrierId() == null ||
            request.getStartNumber() == null || request.getEndNumber() == null) {
            resp.setConflict(false);
            resp.setConflictSegments(java.util.Collections.emptyList());
            return resp;
        }

        // 查询同一船司下所有号码段
        java.util.List<BlankBillSegment> segments = blankBillSegmentRepository.findByCarrierId(request.getCarrierId());
        java.util.List<BlankBillSegmentCheckResponse.ConflictSegment> conflictSegments = new java.util.ArrayList<>();

        long reqStart = Long.parseLong(request.getStartNumber());
        long reqEnd = Long.parseLong(request.getEndNumber());

        for (BlankBillSegment seg : segments) {
            long segStart = Long.parseLong(seg.getStartNumber());
            long segEnd = Long.parseLong(seg.getEndNumber());
            // 判断是否有交叉（区间重叠）
            if (!(reqEnd < segStart || reqStart > segEnd)) {
                BlankBillSegmentCheckResponse.ConflictSegment cs = new BlankBillSegmentCheckResponse.ConflictSegment();
                cs.setStartNumber(seg.getStartNumber());
                cs.setEndNumber(seg.getEndNumber());
                conflictSegments.add(cs);
            }
        }
        resp.setConflict(!conflictSegments.isEmpty());
        resp.setConflictSegments(conflictSegments);
        return resp;
    }

    @Override
    public BlankBillSegmentEditResponse editSegment(Long segmentId, BlankBillSegmentEditRequest request) {
        // TODO: 实现号码段编辑逻辑，仅允许备注、入库日期修改，记录操作日志
        return new BlankBillSegmentEditResponse();
    }

    @Override
    public BlankBillSegmentArchiveResponse archiveSegments() {
        // TODO: 实现自动归档逻辑，满足归档条件时自动归档，幂等
        return new BlankBillSegmentArchiveResponse();
    }

    @Override
    public BlankBillSegmentListResponse getSegments(BlankBillSegmentListQuery query) {
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() - 1 : 0;
        int size = query.getSize() != null && query.getSize() > 0 ? query.getSize() : 20;
        Sort sort = Sort.by(Sort.Direction.DESC, "segmentId");
        Pageable pageable = PageRequest.of(page, size, sort);

        // 这里只做简单分页，复杂筛选可用Specification或QueryDSL扩展
        Page<BlankBillSegment> segmentPage = blankBillSegmentRepository.findAll(pageable);

        BlankBillSegmentListResponse resp = new BlankBillSegmentListResponse();
        resp.setTotal((int) segmentPage.getTotalElements());
        resp.setSegments(segmentPage.getContent().stream().map(seg -> {
            BlankBillSegmentListItem item = new BlankBillSegmentListItem();
            item.setSegmentId(seg.getSegmentId());
            // carrierName 需联表查，这里仅填ID或留空
            item.setCarrierName(seg.getCarrierId() != null ? String.valueOf(seg.getCarrierId()) : "");
            item.setStartNumber(seg.getStartNumber());
            item.setEndNumber(seg.getEndNumber());
            item.setQuantity(seg.getQuantity());
            item.setRemark(seg.getRemark());
            item.setEntryDate(seg.getEntryDate() != null ? seg.getEntryDate().toString() : "");
            item.setArchived("ARCHIVED".equals(seg.getStatus()));
            return item;
        }).toList());
        return resp;
    }
}
