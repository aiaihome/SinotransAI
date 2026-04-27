-- 建议先手动创建数据库（如已存在可跳过）
CREATE DATABASE IF NOT EXISTS sinotrans_blankbill DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sinotrans_blankbill;
-- Sinotrans 空白提单管理系统 · 数据库初始化脚本 v2
-- 依据《后端架构设计文档》标准化表结构、字段、索引、约束

-- 1. 船司主数据
CREATE TABLE IF NOT EXISTS shipping_company (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    code            VARCHAR(32)  NOT NULL UNIQUE,
    status          VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 空白提单号码段
CREATE TABLE IF NOT EXISTS blank_bl_range (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    shipping_company_id BIGINT      NOT NULL,
    port_code           VARCHAR(16) NOT NULL,
    start_no            VARCHAR(32) NOT NULL,
    end_no              VARCHAR(32) NOT NULL,
    total_qty           INT         NOT NULL,
    used_qty            INT         NOT NULL DEFAULT 0,
    void_qty            INT         NOT NULL DEFAULT 0,
    status              VARCHAR(16) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE/ARCHIVED
    entry_date          DATE        NOT NULL,
    remark              VARCHAR(200),
    created_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_range (shipping_company_id, port_code, start_no, end_no),
    CONSTRAINT fk_range_company FOREIGN KEY (shipping_company_id) REFERENCES shipping_company(id)
);

-- 3. 空白提单序列号
CREATE TABLE IF NOT EXISTS blank_bl_serial (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    range_id        BIGINT      NOT NULL,
    serial_no       VARCHAR(32) NOT NULL,
    status          VARCHAR(16) NOT NULL DEFAULT 'NEW', -- NEW/USED/RECOVERED/VOIDED/ARCHIVED
    bl_no           VARCHAR(64),
    void_reason     VARCHAR(100),
    void_remark     VARCHAR(200),
    void_by         VARCHAR(64),
    void_date       DATETIME,
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_serial (serial_no),
    CONSTRAINT fk_serial_range FOREIGN KEY (range_id) REFERENCES blank_bl_range(id)
);

-- 4. 操作日志
CREATE TABLE IF NOT EXISTS operation_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         VARCHAR(64)  NOT NULL,
    operation_type  VARCHAR(32)  NOT NULL, -- 枚举：IN/EDIT/ARCHIVE/RECOVER/VOID/EXPORT
    target_type     VARCHAR(32)  NOT NULL, -- 枚举：RANGE/SERIAL
    target_id       BIGINT       NOT NULL,
    summary         VARCHAR(200),
    before_value    TEXT,
    after_value     TEXT,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. 用户账号（预留，Demo可不实现）
CREATE TABLE IF NOT EXISTS user_account (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64) NOT NULL UNIQUE,
    real_name   VARCHAR(64),
    status      VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引优化
CREATE INDEX idx_range_status ON blank_bl_range(status);
CREATE INDEX idx_serial_status ON blank_bl_serial(status);
CREATE INDEX idx_log_type ON operation_log(operation_type, target_type, target_id);
