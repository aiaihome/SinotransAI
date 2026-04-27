package com.sinotrans.blankbill.repository;

import com.sinotrans.blankbill.entity.SerialNumberLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SerialNumberLogRepository extends JpaRepository<SerialNumberLog, Long> {
    List<SerialNumberLog> findBySerialNumberIdOrderByOperateTimeDesc(Long serialNumberId);
}
