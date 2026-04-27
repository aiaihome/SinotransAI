
package com.sinotrans.blankbill.repository;


import com.sinotrans.blankbill.entity.SerialNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface SerialNumberRepository extends JpaRepository<SerialNumber, Long> {
    Page<SerialNumber> findBySegmentId(Long segmentId, Pageable pageable);
}
