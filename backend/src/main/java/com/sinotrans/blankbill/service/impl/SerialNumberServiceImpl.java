package com.sinotrans.blankbill.service.impl;


import com.sinotrans.ai.dto.SerialNumberVoidRequest;
import com.sinotrans.ai.dto.SerialNumberVoidResponse;
import com.sinotrans.blankbill.dto.*;
import com.sinotrans.blankbill.entity.SerialNumber;
import com.sinotrans.blankbill.repository.SerialNumberRepository;
import com.sinotrans.blankbill.repository.OperationLogRepository;
import com.sinotrans.blankbill.service.SerialNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SerialNumberServiceImpl implements SerialNumberService {
    private final SerialNumberRepository serialNumberRepository;
    private final OperationLogRepository operationLogRepository;
    @Override
    public SerialNumberListResponse getSerialNumbers(SerialNumberListQuery query) {
        // 简单分页+条件查询实现（可扩展为Specification）
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() - 1 : 0;
        int size = query.getSize() != null && query.getSize() > 0 ? query.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // 这里只做简单条件，复杂条件建议用Specification
        Page<SerialNumber> serialPage;
        if (query.getSegmentId() != null) {
            serialPage = serialNumberRepository.findBySegmentId(query.getSegmentId(), pageable);
        } else {
            serialPage = serialNumberRepository.findAll(pageable);
        }

        List<SerialNumberListItem> items = serialPage.getContent().stream().map(sn -> {
            SerialNumberListItem item = new SerialNumberListItem();
            item.setId(sn.getId());
            item.setBillNumber(sn.getBillNumber());
            item.setStatus(sn.getStatus());
            item.setEntryDate(sn.getEntryDate() != null ? sn.getEntryDate().toString() : "");
            item.setRemark(sn.getRemark());
            return item;
        }).collect(Collectors.toList());

        SerialNumberListResponse resp = new SerialNumberListResponse();
        resp.setTotal((int) serialPage.getTotalElements());
        resp.setPage(page + 1);
        resp.setSize(size);
        resp.setSerialNumbers(items);
        return resp;
    }

    @Override
    public SerialNumberDetailResponse getDetail(Long id) {
        SerialNumber sn = serialNumberRepository.findById(id).orElse(null);
        if (sn == null) return null;
        SerialNumberDetailResponse resp = new SerialNumberDetailResponse();
        resp.setId(sn.getId());
        resp.setBillNumber(sn.getBillNumber());
        resp.setStatus(sn.getStatus());
        resp.setEntryDate(sn.getEntryDate() != null ? sn.getEntryDate().toString() : "");
        resp.setRemark(sn.getRemark());
        // 日志暂为空
        resp.setHistory(List.of());
        return resp;
    }


    @Override
    public List<SerialNumberLogItem> getLogs(Long id) {
        // 查询 operation_log 表，target_type = 'SERIAL'，target_id = id，按操作时间倒序
        List<com.sinotrans.blankbill.entity.OperationLog> logs = operationLogRepository.findByTargetTypeAndTargetIdOrderByOperateTimeDesc("SERIAL", id);
        return logs.stream().map(log -> {
            SerialNumberLogItem item = new SerialNumberLogItem();
            item.setId(log.getId());
            item.setAction(log.getActionName()); // 取 action_name 字段
            item.setOperator(log.getOperatorName());
            item.setOperateTime(log.getOperateTime() != null ? log.getOperateTime().toString() : "");
            item.setBeforeSnapshot(log.getBeforeSnapshot());
            item.setAfterSnapshot(log.getAfterSnapshot());
            item.setRemark(log.getRemark());
            return item;
        }).collect(Collectors.toList());
    }
    

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SerialNumberRecoverResponse recover(SerialNumberRecoverRequest request) {
        SerialNumberRecoverResponse resp = new SerialNumberRecoverResponse();
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            resp.setSuccess(false);
            resp.setMessage("参数不能为空");
            return resp;
        }
        List<Long> ids = request.getIds();
        List<Long> recoveredIds = ids.stream().filter(id -> {
            SerialNumber sn = serialNumberRepository.findById(id).orElse(null);
            if (sn != null && !"RECOVERED".equals(sn.getStatus())) {
                sn.setStatus("RECOVERED");
                sn.setRecoveredRemark(request.getRemark());
                sn.setRecoveredAt(java.time.LocalDateTime.now());
                serialNumberRepository.save(sn);
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        SerialNumberRecoverResponse.DataObj data = new SerialNumberRecoverResponse.DataObj();
        data.setRecoveredIds(recoveredIds);
        data.setCount(recoveredIds.size());
        resp.setSuccess(true);
        resp.setMessage("回收成功");
        resp.setData(data);
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SerialNumberVoidResponse voidSerialNumbers(SerialNumberVoidRequest request) {
        SerialNumberVoidResponse response = new SerialNumberVoidResponse();
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            response.setCount(0);
            return response;
        }

        int count = 0;
        for (Long id : request.getIds()) {
            SerialNumber serialNumber = serialNumberRepository.findById(id).orElse(null);
            if (serialNumber == null || "VOIDED".equals(serialNumber.getStatus())) {
                continue;
            }

            serialNumber.setStatus("VOIDED");
            serialNumber.setVoidReason(request.getReason());
            serialNumber.setVoidRemark(request.getRemark());
            serialNumber.setVoidAt(java.time.LocalDateTime.now());
            serialNumberRepository.save(serialNumber);
            count++;
        }

        response.setCount(count);
        return response;
    }
}
