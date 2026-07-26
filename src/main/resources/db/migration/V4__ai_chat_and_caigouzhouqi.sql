-- AI 智能分析助手：对话表 + 商品采购周期字段

-- 商品可选采购周期（天），空则使用配置默认 14
SET @exist_caigouzhouqi := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'shangpinxinxi'
      AND COLUMN_NAME = 'caigouzhouqi'
);

SET @sql_caigouzhouqi := IF(@exist_caigouzhouqi = 0,
    'ALTER TABLE shangpinxinxi ADD COLUMN caigouzhouqi INT NULL COMMENT ''采购周期（天），空则用系统默认''',
    'SELECT ''caigouzhouqi already exists'' AS message'
);

PREPARE stmt FROM @sql_caigouzhouqi;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS `ai_chat_session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `table_name` varchar(50) DEFAULT NULL COMMENT '登录表 users/yonghu',
  `title` varchar(200) DEFAULT NULL COMMENT '会话标题',
  `style` varchar(20) DEFAULT 'SIMPLE' COMMENT 'SIMPLE/DETAILED',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ai_session_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='AI对话会话';

CREATE TABLE IF NOT EXISTS `ai_chat_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `session_id` bigint(20) NOT NULL COMMENT '会话ID',
  `role` varchar(20) NOT NULL COMMENT 'user/assistant/system',
  `content` mediumtext COMMENT '消息内容',
  `data_snapshot` mediumtext COMMENT '装配统计JSON摘要',
  `degraded` tinyint(1) DEFAULT 0 COMMENT '是否降级回答',
  `intent` varchar(50) DEFAULT NULL COMMENT '意图',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ai_msg_session` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='AI对话消息';
