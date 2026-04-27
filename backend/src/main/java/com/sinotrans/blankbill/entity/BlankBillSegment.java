package com.sinotrans.blankbill.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "blank_bill_segment")
public class BlankBillSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long segmentId;

    @Column(name = "segment_code", nullable = false)
    private String segmentCode;

    @Column(name = "shipping_company_id", nullable = false)
    private Long carrierId;

    @Column(name = "port_code", nullable = false)
    private String portCode;

    @Column(name = "start_number", nullable = false, length = 8)
    private String startNumber;

    @Column(name = "end_number", nullable = false, length = 8)
    private String endNumber;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "used_quantity")
    private Integer usedQuantity;

    @Column(name = "void_quantity")
    private Integer voidQuantity;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "archived_at")
    private java.time.LocalDateTime archivedAt;

    @Column(name = "remark", length = 200)
    private String remark;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "version")
    private Integer version;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
}
