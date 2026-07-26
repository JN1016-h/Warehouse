-- 用户角色字段 + 演示角色/业务日期刷新（可重复执行）
USE springboot38hdw40x;

-- 1) user_role
SET @exist_user_role := (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'springboot38hdw40x'
      AND TABLE_NAME = 'yonghu'
      AND COLUMN_NAME = 'user_role'
);
SET @sql_user_role := IF(@exist_user_role = 0,
    'ALTER TABLE yonghu ADD COLUMN user_role VARCHAR(50) DEFAULT ''DEALER'' COMMENT ''DEALER/INTERNAL_STAFF/WAREHOUSE_ADMIN''',
    'SELECT ''user_role exists'' AS message'
);
PREPARE stmt FROM @sql_user_role;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) 样例角色：账号1内部员工，账号2仓管，其余经销商
UPDATE yonghu SET user_role = 'INTERNAL_STAFF' WHERE yonghuzhanghao = '用户账号1';
UPDATE yonghu SET user_role = 'WAREHOUSE_ADMIN' WHERE yonghuzhanghao = '用户账号2';
UPDATE yonghu SET user_role = 'DEALER'
WHERE yonghuzhanghao IN ('用户账号3','用户账号4','用户账号5','用户账号6','用户账号7','用户账号8')
  AND (user_role IS NULL OR user_role = '' OR user_role = 'PLATFORM_ADMIN');
UPDATE yonghu SET user_role = 'INTERNAL_STAFF' WHERE user_role = 'PLATFORM_ADMIN';
UPDATE yonghu SET user_role = 'DEALER' WHERE user_role IS NULL OR user_role = '';

-- 3) 把演示出入库/订货日期刷到近 20 天，保证「近月」有数
UPDATE chukuxinxi
SET jiaohuoshijian = DATE_SUB(CURDATE(), INTERVAL (id % 20) DAY)
WHERE jiaohuoshijian IS NOT NULL;

UPDATE rukuxinxi
SET rukushijian = DATE_SUB(CURDATE(), INTERVAL (id % 20) DAY)
WHERE rukushijian IS NOT NULL;

UPDATE dinghuoxinxi
SET dinghuoshijian = DATE_SUB(CURDATE(), INTERVAL (id % 20) DAY)
WHERE dinghuoshijian IS NOT NULL;
