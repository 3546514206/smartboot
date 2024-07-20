-- 引擎表
CREATE TABLE IF NOT EXISTS `engine_table` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `engine_name` varchar(256) NOT NULL COMMENT '引擎名称，唯一',
    `content` text COMMENT '引擎配置内容，必须是xml形式',
    `version` int NOT NULL DEFAULT '1' COMMENT '版本',
    `status` int NOT NULL DEFAULT '0' COMMENT '状态 0-正常 1-删除',
    `created` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_engine_name` (`engine_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='引擎配置表';

-- 引擎历史表
CREATE TABLE IF NOT EXISTS `engine_table_history`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `engine_id` bigint NOT NULL COMMENT '引擎id',
    `engine_name` varchar(256) NOT NULL COMMENT '引擎名称',
    `content` text COMMENT '引擎配置内容，必须是xml形式',
    `version` int NOT NULL DEFAULT '1' COMMENT '版本',
    `created` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='引擎历史记录表';

-- 引擎结构快照
CREATE TABLE IF NOT EXISTS `engine_table_snapshot`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `engine_name` varchar(256) NOT NULL COMMENT '引擎名称',
    `content` text COMMENT '引擎配置内容，必须是xml形式',
    `md5` varchar(256) COMMENT 'content md5加密字符串',
    `created` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name_md5` (`engine_name`, `md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='引擎快照表';

-- 执行耗时
CREATE TABLE IF NOT EXISTS `engine_table_metrics`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `engine_name` varchar(256) NOT NULL COMMENT '引擎名称',
    `md5` varchar(256) COMMENT 'content md5加密字符串',
    `address` varchar(64) COMMENT 'IP地址',
    `host` varchar(128) COMMENT '主机名',
    `content` text COMMENT 'json格式',
    `report_time` timestamp COMMENT '上报时间',
    `created` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_engine_name` (`engine_name`),
    KEY `idx_host` (`host`),
    KEY `idx_created`(`created`),
    KEY `idx_report_time` (`report_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='引擎执行数据表';

-- 执行trace
CREATE TABLE IF NOT EXISTS `engine_table_trace`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `engine_name` varchar(256) NOT NULL COMMENT '引擎名称',
    `address` varchar(64) COMMENT 'IP地址',
    `host` varchar(128) COMMENT '主机名',
    `md5` varchar(256) COMMENT 'content md5加密字符串',
    `trace_id` varchar(256) COMMENT 'traceId',
    `status` int(4) COMMENT 'status',
    `message` TEXT COMMENT 'message',
    `content` text COMMENT 'json格式',
    `report_time` timestamp COMMENT '上报时间',
    `request` text COMMENT 'json格式',
    `result` text COMMENT 'json格式',
    `trace_time` timestamp COMMENT '采集开始时间',
    `end_time` timestamp COMMENT '采集结束时间',
    `escaped` bigint COMMENT '链路耗时',
    `created` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_trace_id` (`trace_id`),
    KEY `idx_name` (`engine_name`),
    KEY `idx_host_name` (`host`),
    KEY `idx_report` (`report_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='引擎执行链路表';

-- 执行trace
CREATE TABLE IF NOT EXISTS `engine_g6_config`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `engine_name` varchar(256) NOT NULL COMMENT '引擎名称',
    `md5` varchar(256) COMMENT 'content md5加密字符串',
    `content` text COMMENT 'g6配置json串',
    `created` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_engine_name_md5` (`engine_name`, `md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='引擎g6配置';