-- 已有库若缺 config.url 列（与 ConfigEntity / 管理端「系统配置」一致），在目标库执行本脚本一次。
-- MySQL 5.7+

ALTER TABLE `config`
  ADD COLUMN `url` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外链或展示用url' AFTER `value`;
