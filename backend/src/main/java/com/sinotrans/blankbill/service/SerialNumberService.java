package com.sinotrans.blankbill.service;

import com.sinotrans.ai.dto.SerialNumberVoidRequest;
import com.sinotrans.ai.dto.SerialNumberVoidResponse;
import com.sinotrans.blankbill.dto.SerialNumberDetailResponse;
import com.sinotrans.blankbill.dto.SerialNumberListQuery;
import com.sinotrans.blankbill.dto.SerialNumberListResponse;

public interface SerialNumberService {
    SerialNumberListResponse getSerialNumbers(SerialNumberListQuery query);
    SerialNumberDetailResponse getDetail(Long id);
    java.util.List<com.sinotrans.blankbill.dto.SerialNumberLogItem> getLogs(Long id);
    com.sinotrans.blankbill.dto.SerialNumberRecoverResponse recover(com.sinotrans.blankbill.dto.SerialNumberRecoverRequest request);
    SerialNumberVoidResponse voidSerialNumbers(SerialNumberVoidRequest request);
}
