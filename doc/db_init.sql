drop database if exists pay;
CREATE DATABASE  pay DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

use pay;

-- ----------------------------
-- Table structure for third_bank_send
-- ----------------------------
DROP TABLE IF EXISTS third_bank_send;
CREATE TABLE third_bank_send (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  send_id char(32) DEFAULT NULL,
  req varchar(64) DEFAULT NULL COMMENT '流水号',
  user_id char(32) DEFAULT NULL COMMENT '用户id',
  real_name varchar(16) DEFAULT NULL COMMENT '真实姓名',
  id_no varchar(32) DEFAULT NULL COMMENT '身份证',
  bank_type varchar(8) DEFAULT NULL COMMENT '银行编码',
  card_no varchar(64) DEFAULT NULL COMMENT '银行卡号',
  mobile varchar(16) DEFAULT NULL COMMENT '银行预留手机号码',
  return_code varchar(16) DEFAULT NULL COMMENT '响应代码',
  return_msg varchar(128) DEFAULT NULL COMMENT '验证结果',
  is_valid char(8) DEFAULT NULL COMMENT '验证是否通过',
  send_time datetime DEFAULT NULL COMMENT '验证时间',
  channel varchar(8) DEFAULT NULL COMMENT '验证渠道',
  remark varchar(128) DEFAULT NULL COMMENT '备注',
  source_type varchar(16) DEFAULT NULL,
  bank_verify_type varchar(16) DEFAULT NULL COMMENT '验证类型',
  user_login_name varchar(16) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_bank_send_sid (send_id) USING BTREE,
  KEY idx_user_id_stime (user_id,send_time) USING BTREE,
  KEY idx_send_time (send_time) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='银行卡验证签约记录';



-- ----------------------------
-- Table structure for third_pay_quota
-- ----------------------------
DROP TABLE IF EXISTS third_pay_quota;
CREATE TABLE third_pay_quota (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  quota_id char(32) NOT NULL COMMENT '主键',
  pay_channel varchar(8) NOT NULL COMMENT '支付渠道',
  bank_type varchar(8) NOT NULL COMMENT '银行编号',
  single_amount decimal(20,8) NOT NULL COMMENT '单笔充值限制金额',
  remark varchar(255) DEFAULT '',
  PRIMARY KEY (id),
  UNIQUE KEY quota_id (quota_id),
  UNIQUE KEY channel_code (pay_channel,bank_type) USING BTREE,
  KEY pay_channel (pay_channel),
  KEY bank_type (bank_type)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='第三方支付充值限额表';

-- ----------------------------
-- Table structure for third_pay_record
-- ----------------------------
DROP TABLE IF EXISTS third_pay_record;
CREATE TABLE third_pay_record (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  trade_id char(32) DEFAULT NULL COMMENT '交易ID',
  user_id char(32) DEFAULT NULL COMMENT '关联用户',
  serialnumber varchar(32) DEFAULT NULL COMMENT '交易流水号',
  card_no varchar(64) DEFAULT NULL COMMENT '支付卡号',
  real_name varchar(16) DEFAULT NULL COMMENT '持卡人',
  bank_type varchar(16) DEFAULT NULL COMMENT '开户行编号',
  order_amount decimal(20,8) DEFAULT NULL COMMENT '交易金额',
  act_amount decimal(20,8) DEFAULT NULL COMMENT '实际发生金额',
  order_code varchar(32) DEFAULT NULL COMMENT '订单号',
  pay_type char(8) DEFAULT NULL COMMENT '支付类型',
  pay_channel char(8) DEFAULT NULL COMMENT '支付渠道',
  send_time datetime DEFAULT NULL COMMENT '发送时间',
  send_status varchar(8) DEFAULT NULL COMMENT '发送状态',
  send_count int(8) DEFAULT NULL COMMENT '发送次数',
  trade_status char(8) DEFAULT NULL COMMENT '交易状态',
  pay_code varchar(16) DEFAULT NULL COMMENT '响应代号',
  pay_message varchar(128) DEFAULT NULL COMMENT '响应信息',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  callback_status char(8) DEFAULT NULL COMMENT '业务回调状态',
  costs decimal(20,2) DEFAULT '0.00' COMMENT '第三方支付费用',
  success_time datetime DEFAULT NULL COMMENT '支付完成时间',
  settle_time datetime DEFAULT NULL COMMENT '支付公司清算时间',
  card_prop char(8) DEFAULT null COMMENT '银行卡属性,个人或企业',
  thirdnumber varchar(32) DEFAULT NULL COMMENT '第三方公司的订单号',
  PRIMARY KEY (id),
  UNIQUE KEY unique_serialnumber (serialnumber) USING BTREE,
  KEY unique_trade_id (trade_id) USING BTREE,
  KEY idx_third_pay_status (trade_status) USING BTREE,
  KEY idx_pay_record_cname (real_name) USING BTREE,
  KEY idx_order_no_id (order_code) USING BTREE,
  KEY idx_card_no (card_no) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='第三方交易记录表';

-- ----------------------------
-- Table structure for third_pay_record_dtl
-- ----------------------------
DROP TABLE IF EXISTS third_pay_record_dtl;
CREATE TABLE third_pay_record_dtl (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  trade_dtl_id char(32) DEFAULT NULL COMMENT '明细ID',
  trade_id char(32) DEFAULT NULL COMMENT '交易ID',
  dtl_serialnumber varchar(32) DEFAULT NULL COMMENT '明细流水号ID',
  amount double(10,2) DEFAULT NULL COMMENT '交易金额',
  trade_status varchar(8) DEFAULT NULL COMMENT '交易状态',
  trade_dtl_time datetime DEFAULT NULL COMMENT '交易时间',
  trade_dtl_code varchar(16) DEFAULT NULL COMMENT '明细代号',
  trade_dtl_msg varchar(128) DEFAULT NULL COMMENT '明细消息',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  status char(8) DEFAULT NULL COMMENT '状态',
  remark varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY index_trade_dtl_id (trade_dtl_id) USING BTREE,
  KEY index_third_dtl_trade_id (trade_id) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='第三方交易记录明细表';

-- ----------------------------
-- Table structure for third_pay_validcode
-- ----------------------------
DROP TABLE IF EXISTS third_pay_validcode;
CREATE TABLE third_pay_validcode (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  pay_id char(32) DEFAULT NULL,
  user_id char(32) DEFAULT NULL,
  amount decimal(20,8) DEFAULT NULL,
  serialnumber varchar(32) DEFAULT NULL,
  trade_status varchar(8) DEFAULT NULL,
  pay_channel varchar(8) DEFAULT NULL,
  ret_code varchar(16) DEFAULT NULL,
  ret_msg varchar(255) DEFAULT NULL,
  user_login_name varchar(32) DEFAULT NULL,
  token varchar(32) DEFAULT NULL,
  send_time datetime DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY pay_id (pay_id),
  KEY user_login_name (user_login_name),
  KEY user_id (user_id),
  KEY serialnumber (serialnumber)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='充值验证码记录';
