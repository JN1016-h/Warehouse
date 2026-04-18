-- 为rukuxinxi表添加payment_status和payment_time字段
ALTER TABLE rukuxinxi ADD COLUMN payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '付款状态：PAID-已付款, UNPAID-未付款';
ALTER TABLE rukuxinxi ADD COLUMN payment_time DATETIME NULL COMMENT '付款时间';
