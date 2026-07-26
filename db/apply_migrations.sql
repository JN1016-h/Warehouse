-- 应用数据库迁移脚本
-- 请在MySQL中执行此脚本

USE springboot38hdw40x;

-- V1: 为yonghu表添加user_role字段
SET @exist_user_role := (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'springboot38hdw40x' 
    AND TABLE_NAME = 'yonghu' 
    AND COLUMN_NAME = 'user_role'
);

SET @sql_user_role := IF(@exist_user_role = 0,
    'ALTER TABLE yonghu ADD COLUMN user_role VARCHAR(50) DEFAULT NULL COMMENT ''用户角色：DEALER-经销商, INTERNAL_STAFF-内部员工, PLATFORM_ADMIN-平台管理员, WAREHOUSE_ADMIN-仓库管理员''',
    'SELECT ''user_role字段已存在于yonghu表'' AS message'
);

PREPARE stmt FROM @sql_user_role;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- V2: 为rukuxinxi表添加payment_status和payment_time字段
-- 检查字段是否存在，如果不存在则添加
SET @exist_payment_status_ruku := (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'springboot38hdw40x' 
    AND TABLE_NAME = 'rukuxinxi' 
    AND COLUMN_NAME = 'payment_status'
);

SET @sql_ruku_status := IF(@exist_payment_status_ruku = 0,
    'ALTER TABLE rukuxinxi ADD COLUMN payment_status VARCHAR(20) DEFAULT ''UNPAID'' COMMENT ''付款状态：PAID-已付款, UNPAID-未付款''',
    'SELECT ''payment_status字段已存在于rukuxinxi表'' AS message'
);

PREPARE stmt FROM @sql_ruku_status;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exist_payment_time_ruku := (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'springboot38hdw40x' 
    AND TABLE_NAME = 'rukuxinxi' 
    AND COLUMN_NAME = 'payment_time'
);

SET @sql_ruku_time := IF(@exist_payment_time_ruku = 0,
    'ALTER TABLE rukuxinxi ADD COLUMN payment_time DATETIME NULL COMMENT ''付款时间''',
    'SELECT ''payment_time字段已存在于rukuxinxi表'' AS message'
);

PREPARE stmt FROM @sql_ruku_time;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- V3: 为chukuxinxi表添加payment_status和payment_time字段
SET @exist_payment_status_chuku := (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'springboot38hdw40x' 
    AND TABLE_NAME = 'chukuxinxi' 
    AND COLUMN_NAME = 'payment_status'
);

SET @sql_chuku_status := IF(@exist_payment_status_chuku = 0,
    'ALTER TABLE chukuxinxi ADD COLUMN payment_status VARCHAR(20) DEFAULT ''UNPAID'' COMMENT ''付款状态：PAID-已付款, UNPAID-未付款''',
    'SELECT ''payment_status字段已存在于chukuxinxi表'' AS message'
);

PREPARE stmt FROM @sql_chuku_status;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exist_payment_time_chuku := (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'springboot38hdw40x' 
    AND TABLE_NAME = 'chukuxinxi' 
    AND COLUMN_NAME = 'payment_time'
);

SET @sql_chuku_time := IF(@exist_payment_time_chuku = 0,
    'ALTER TABLE chukuxinxi ADD COLUMN payment_time DATETIME NULL COMMENT ''付款时间''',
    'SELECT ''payment_time字段已存在于chukuxinxi表'' AS message'
);

PREPARE stmt FROM @sql_chuku_time;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 验证字段是否添加成功
SELECT 'yonghu表的user_role字段:' AS info;
SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_DEFAULT, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'springboot38hdw40x' 
AND TABLE_NAME = 'yonghu' 
AND COLUMN_NAME = 'user_role';

SELECT 'rukuxinxi表的payment字段:' AS info;
SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_DEFAULT, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'springboot38hdw40x' 
AND TABLE_NAME = 'rukuxinxi' 
AND COLUMN_NAME LIKE 'payment%';

SELECT 'chukuxinxi表的payment字段:' AS info;
SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_DEFAULT, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'springboot38hdw40x' 
AND TABLE_NAME = 'chukuxinxi' 
AND COLUMN_NAME LIKE 'payment%';
