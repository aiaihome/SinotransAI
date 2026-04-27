package com.sinotrans.ai.service;

import com.sinotrans.ai.dto.SerialNumberVoidRequest;
import com.sinotrans.ai.dto.SerialNumberVoidResponse;

public interface SerialNumberService {
    SerialNumberVoidResponse voidSerialNumbers(SerialNumberVoidRequest request);
}
