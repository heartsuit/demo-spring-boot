CREATE TABLE `employee` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`name` VARCHAR(255) NULL DEFAULT NULL COMMENT '姓名' COLLATE 'utf8_general_ci',
	`age` INT(11) NOT NULL DEFAULT '0' COMMENT '年龄',
	`phone` VARCHAR(15) NULL DEFAULT NULL COMMENT '手机号码' COLLATE 'utf8_general_ci',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `username` (`name`) USING BTREE
)
COMMENT='测试MyBatis'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

INSERT INTO `employee` (`id`, `name`, `age`, `phone`, `create_time`) VALUES (1, 'admin', 23, '18888888888', '2018-08-23 09:11:56');
INSERT INTO `employee` (`id`, `name`, `age`, `phone`, `create_time`) VALUES (2, 'test啊是的撒', 28, '17777777777', '2018-12-27 20:05:26');
INSERT INTO `employee` (`id`, `name`, `age`, `phone`, `create_time`) VALUES (1240569057522528258, '李文亮', 35, '15521344568', '2020-03-19 17:21:26');
INSERT INTO `employee` (`id`, `name`, `age`, `phone`, `create_time`) VALUES (1240569421151879169, '李兰娟', 63, '15521344568', '2020-03-19 17:22:53');
INSERT INTO `employee` (`id`, `name`, `age`, `phone`, `create_time`) VALUES (1240569421151879172, '阿拉斯加', 34, '12345678901', '2021-08-09 12:24:32');
INSERT INTO `employee` (`id`, `name`, `age`, `phone`, `create_time`) VALUES (1240569421151879173, '5256d', 84, '18295551174', '2021-08-10 11:31:40');