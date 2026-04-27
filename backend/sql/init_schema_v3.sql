-- 建议先手动创建数据库（如已存在可跳过）
CREATE DATABASE IF NOT EXISTS sinotrans_blankbill DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sinotrans_blankbill;
-- Sinotrans 空白提单管理系统 · 数据库初始化脚本 v2
-- 依据《后端架构设计文档》标准化表结构、字段、索引、约束

-- Sinotrans 空白提单管理系统 Demo v1 数据库初始化脚本
-- 适用MySQL 8.x

-- 1. 用户表
CREATE TABLE IF NOT EXISTS user_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    real_name VARCHAR(64) NOT NULL,
    role_code VARCHAR(32) NOT NULL DEFAULT 'DEMO_USER',
    port_code VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    password VARCHAR(128),
    last_login_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_account_username UNIQUE (username),
    INDEX idx_user_account_status (status),
    INDEX idx_user_account_port_code (port_code)
);

-- 2. 船司主数据表
CREATE TABLE IF NOT EXISTS shipping_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_code VARCHAR(32) NOT NULL UNIQUE,
    company_name VARCHAR(64) NOT NULL,
    short_name VARCHAR(32),
    status VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_shipping_company_code UNIQUE (company_code),
    INDEX idx_shipping_company_name (company_name),
    INDEX idx_shipping_company_status_sort (status, sort_order)
);

-- 3. 空白提单号码段表
CREATE TABLE IF NOT EXISTS blank_bill_segment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    segment_code VARCHAR(64) NOT NULL UNIQUE,
    shipping_company_id BIGINT NOT NULL,
    port_code VARCHAR(32) NOT NULL,
    start_number VARCHAR(8) NOT NULL,
    end_number VARCHAR(8) NOT NULL,
    quantity INT NOT NULL,
    used_quantity INT NOT NULL DEFAULT 0,
    void_quantity INT NOT NULL DEFAULT 0,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    entry_date DATE NOT NULL,
    archived_at DATETIME,
    remark VARCHAR(200),
    created_by BIGINT,
    updated_by BIGINT,
    version INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_blank_bill_segment_code UNIQUE (segment_code),
    CONSTRAINT uk_blank_bill_segment_range UNIQUE (shipping_company_id, port_code, start_number, end_number),
    INDEX idx_blank_bill_segment_company_entry (shipping_company_id, entry_date),
    INDEX idx_blank_bill_segment_status_entry (status, entry_date),
    INDEX idx_blank_bill_segment_company_range (shipping_company_id, start_number, end_number),
    CONSTRAINT chk_blank_bill_segment_range CHECK (start_number <= end_number),
    CONSTRAINT chk_blank_bill_segment_totals CHECK (used_quantity + void_quantity <= quantity)
);

-- 4. 空白提单序列号表
CREATE TABLE IF NOT EXISTS blank_bill_serial (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    segment_id BIGINT NOT NULL,
    shipping_company_id BIGINT NOT NULL,
    serial_number VARCHAR(32) NOT NULL UNIQUE,
    raw_number VARCHAR(8) NOT NULL,
    bill_number VARCHAR(32),
    status VARCHAR(16) NOT NULL DEFAULT 'NEW',
    entry_date DATE NOT NULL,
    remark VARCHAR(200),
    used_by BIGINT,
    used_at DATETIME,
    recovered_by BIGINT,
    recovered_at DATETIME,
    recovered_remark VARCHAR(200),
    void_reason VARCHAR(64),
    void_remark VARCHAR(200),
    void_by BIGINT,
    void_at DATETIME,
    archived_at DATETIME,
    version INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_blank_bill_serial_number UNIQUE (serial_number),
    CONSTRAINT uk_blank_bill_serial_segment_raw UNIQUE (segment_id, raw_number),
    INDEX idx_blank_bill_serial_bill_number (bill_number),
    INDEX idx_blank_bill_serial_status_entry (status, entry_date),
    INDEX idx_blank_bill_serial_company_raw (shipping_company_id, raw_number),
    INDEX idx_blank_bill_serial_segment_status (segment_id, status),
    INDEX idx_blank_bill_serial_void_reason (void_reason),
    CONSTRAINT chk_blank_bill_serial_status CHECK (status IN ('NEW','USED','RECOVERED','VOIDED','ARCHIVED'))
);

-- 5. 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trace_id VARCHAR(64),
    operator_id BIGINT,
    operator_name VARCHAR(64),
    operator_port_code VARCHAR(32),
    business_module VARCHAR(32),
    action_code VARCHAR(32),
    action_name VARCHAR(64),
    target_type VARCHAR(16),
    target_id BIGINT,
    target_code VARCHAR(64),
    success_flag TINYINT(1) NOT NULL DEFAULT 1,
    remark VARCHAR(200),
    before_snapshot JSON,
    after_snapshot JSON,
    operate_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_operation_log_operator_name (operator_name),
    INDEX idx_operation_log_action_code (action_code),
    INDEX idx_operation_log_operate_time (operate_time),
    INDEX idx_operation_log_target (target_type, target_id),
    INDEX idx_operation_log_trace_id (trace_id),
    CONSTRAINT chk_operation_log_success_flag CHECK (success_flag IN (0,1))
);

-- ========== 初始化演示数据 ==========

-- 用户表
INSERT INTO user_account (username, real_name, role_code, port_code, status, password) VALUES
  ('demo', '演示用户', 'DEMO_USER', 'SHA', 'ACTIVE', '$2a$10$demo'),
  ('admin', '管理员', 'DEMO_USER', 'SHA', 'ACTIVE', '$2a$10$admin');

-- 船司主数据表
INSERT INTO shipping_company (company_code, company_name, short_name, status, sort_order) VALUES
  ('COSCO', '中远海运', 'COSCO', 'ENABLED', 1),
  ('MAERSK', '马士基', 'MAERSK', 'ENABLED', 2),
  ('ONE', '海洋网联', 'ONE', 'ENABLED', 3);

-- 空白提单号码段表
INSERT INTO blank_bill_segment (segment_code, shipping_company_id, port_code, start_number, end_number, quantity, used_quantity, void_quantity, status, entry_date, remark)
VALUES
  ('SEG20260401001', 1, 'SHA', '00010001', '00010100', 100, 10, 2, 'ACTIVE', '2026-04-01', '演示号码段1'),
  ('SEG20260401002', 2, 'SHA', '00020001', '00020100', 100, 0, 0, 'ARCHIVED', '2026-03-28', '演示号码段2');

-- 空白提单序列号表
INSERT INTO blank_bill_serial (segment_id, shipping_company_id, serial_number, raw_number, status, entry_date)
VALUES
  (1, 1, 'COSCO-00010001', '00010001', 'NEW', '2026-04-01'),
  (1, 1, 'COSCO-00010002', '00010002', 'USED', '2026-04-01'),
  (2, 2, 'MAERSK-00020001', '00020001', 'NEW', '2026-03-28');

-- 操作日志表
INSERT INTO operation_log (trace_id, operator_id, operator_name, business_module, action_code, action_name, target_type, target_id, success_flag, remark)
VALUES
  ('TRACE001', 1, '演示用户', 'SEGMENT', 'SEGMENT_CREATE', '新建号码段', 'SEGMENT', 1, 1, '初始化导入'),
  ('TRACE002', 2, '管理员', 'SERIAL', 'SERIAL_VOID', '作废序列号', 'SERIAL', 2, 1, '演示作废');
