package com.sinotrans.ai.service.impl;

import com.sinotrans.ai.dto.SerialNumberVoidRequest;
import com.sinotrans.ai.dto.SerialNumberVoidResponse;
import com.sinotrans.ai.service.SerialNumberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SerialNumberServiceImpl implements SerialNumberService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SerialNumberVoidResponse voidSerialNumbers(SerialNumberVoidRequest request) {
        // TODO: 实现作废逻辑，更新数据库状态，记录日志
        log.info("作废序列号, 请求: {}", request);
        SerialNumberVoidResponse resp = new SerialNumberVoidResponse();
        resp.setCount(request.getIds() != null ? request.getIds().size() : 0);
        return resp;
    }
}
