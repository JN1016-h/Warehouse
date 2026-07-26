-- 为yonghu表添加user_role字段
-- 检查字段是否存在，如果不存在则添加
SET @exist_user_role := (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE()
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
