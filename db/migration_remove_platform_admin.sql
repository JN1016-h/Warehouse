-- 删除 PLATFORM_ADMIN：历史数据迁移为 INTERNAL_STAFF
USE springboot38hdw40x;

UPDATE yonghu
SET user_role = 'INTERNAL_STAFF'
WHERE user_role = 'PLATFORM_ADMIN';
