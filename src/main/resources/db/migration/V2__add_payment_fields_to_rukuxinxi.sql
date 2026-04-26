-- 为 rukuxinxi 表添加 payment_status 和 payment_time（与 RukuxinxiEntity 一致，应付款项页使用）
-- 可重复执行：已存在时跳过
SET @db := DATABASE();

SET @exist_ps := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db
      AND TABLE_NAME = 'rukuxinxi'
      AND COLUMN_NAME = 'payment_status'
);
SET @sql_ps := IF(@exist_ps = 0,
    'ALTER TABLE rukuxinxi ADD COLUMN payment_status VARCHAR(20) DEFAULT ''UNPAID'' COMMENT ''付款状态：PAID-已付款, UNPAID-未付款''',
    'SELECT ''payment_status 已存在'' AS message'
);
PREPARE stmt FROM @sql_ps;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exist_pt := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db
      AND TABLE_NAME = 'rukuxinxi'
      AND COLUMN_NAME = 'payment_time'
);
SET @sql_pt := IF(@exist_pt = 0,
    'ALTER TABLE rukuxinxi ADD COLUMN payment_time DATETIME NULL COMMENT ''付款时间''',
    'SELECT ''payment_time 已存在'' AS message'
);
PREPARE stmt FROM @sql_pt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
