package com.sinotrans.blankbill.repository;

import com.sinotrans.blankbill.entity.BlankBillSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlankBillSegmentRepository extends JpaRepository<BlankBillSegment, Long> {
    // 根据船司ID查询所有号码段
    java.util.List<BlankBillSegment> findByCarrierId(Long carrierId);
}
