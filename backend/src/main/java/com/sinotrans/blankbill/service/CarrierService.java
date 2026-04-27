package com.sinotrans.blankbill.service;

import com.sinotrans.blankbill.dto.CarrierResponse;

public interface CarrierService {
    CarrierResponse getCarriers(String keyword);
}
