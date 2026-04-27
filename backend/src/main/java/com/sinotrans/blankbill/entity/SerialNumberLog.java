package com.sinotrans.blankbill.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "serial_number_log")
public class SerialNumberLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number_id", nullable = false)
    private Long serialNumberId;

    @Column(name = "action", nullable = false, length = 32)
    private String action;

    @Column(name = "operator", length = 64)
    private String operator;

    @Column(name = "operate_time")
    private LocalDateTime operateTime;

    @Column(name = "before_snapshot", columnDefinition = "TEXT")
    private String beforeSnapshot;

    @Column(name = "after_snapshot", columnDefinition = "TEXT")
    private String afterSnapshot;

    @Column(name = "remark", length = 200)
    private String remark;
}
