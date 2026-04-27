package com.sinotrans.blankbill.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blank_bill_serial")
public class SerialNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "segment_id", nullable = false)
    private Long segmentId;

    @Column(name = "shipping_company_id", nullable = false)
    private Long shippingCompanyId;

    @Column(name = "serial_number", nullable = false, unique = true, length = 32)
    private String serialNumber;

    @Column(name = "raw_number", nullable = false, length = 8)
    private String rawNumber;

    @Column(name = "bill_number", length = 32)
    private String billNumber;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "remark", length = 200)
    private String remark;

    @Column(name = "used_by")
    private Long usedBy;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "recovered_by")
    private Long recoveredBy;

    @Column(name = "recovered_at")
    private LocalDateTime recoveredAt;

    @Column(name = "recovered_remark", length = 200)
    private String recoveredRemark;

    @Column(name = "void_reason", length = 64)
    private String voidReason;

    @Column(name = "void_remark", length = 200)
    private String voidRemark;

    @Column(name = "void_by")
    private Long voidBy;

    @Column(name = "void_at")
    private LocalDateTime voidAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "version")
    private Integer version;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
