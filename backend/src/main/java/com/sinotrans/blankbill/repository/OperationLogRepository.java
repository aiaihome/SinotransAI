package com.sinotrans.blankbill.repository;

import com.sinotrans.blankbill.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    List<OperationLog> findByTargetTypeAndTargetIdOrderByOperateTimeDesc(String targetType, Long targetId);
}
