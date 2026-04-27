package com.sinotrans.blankbill.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_log")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "operator_name")
    private String operatorName;

    @Column(name = "operator_port_code")
    private String operatorPortCode;

    @Column(name = "business_module")
    private String businessModule;

    @Column(name = "action_code")
    private String actionCode;

    @Column(name = "action_name")
    private String actionName;

    @Column(name = "target_type")
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "target_code")
    private String targetCode;

    @Column(name = "success_flag")
    private Boolean successFlag;

    @Column(name = "remark")
    private String remark;

    @Column(name = "before_snapshot", columnDefinition = "JSON")
    private String beforeSnapshot;

    @Column(name = "after_snapshot", columnDefinition = "JSON")
    private String afterSnapshot;

    @Column(name = "operate_time")
    private LocalDateTime operateTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
