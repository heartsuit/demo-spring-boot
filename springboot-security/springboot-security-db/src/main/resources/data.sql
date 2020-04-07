-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.7.19 - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  10.3.0.5771
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 spring_security 的数据库结构
CREATE DATABASE IF NOT EXISTS `spring_security` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `spring_security`;

-- 导出  表 spring_security.t_permission 结构
CREATE TABLE IF NOT EXISTS `t_permission` (
  `id` varchar(32) NOT NULL,
  `code` varchar(32) NOT NULL COMMENT '权限标识',
  `description` varchar(64) DEFAULT NULL COMMENT '描述',
  `url` varchar(128) DEFAULT NULL COMMENT '请求地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  spring_security.t_permission 的数据：~2 rows (大约)
/*!40000 ALTER TABLE `t_permission` DISABLE KEYS */;
INSERT INTO `t_permission` (`id`, `code`, `description`, `url`) VALUES
	('1', 'p1', '访问资源1', '/user/add'),
	('2', 'p2', '访问资源2', '/user/query');
/*!40000 ALTER TABLE `t_permission` ENABLE KEYS */;

-- 导出  表 spring_security.t_role 结构
CREATE TABLE IF NOT EXISTS `t_role` (
  `id` varchar(32) NOT NULL,
  `role_name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  spring_security.t_role 的数据：~2 rows (大约)
/*!40000 ALTER TABLE `t_role` DISABLE KEYS */;
INSERT INTO `t_role` (`id`, `role_name`, `description`, `create_time`, `update_time`, `status`) VALUES
	('1', '开发管理员', NULL, NULL, NULL, 1),
	('2', '测试管理员', NULL, NULL, NULL, 1);
/*!40000 ALTER TABLE `t_role` ENABLE KEYS */;

-- 导出  表 spring_security.t_role_permission 结构
CREATE TABLE IF NOT EXISTS `t_role_permission` (
  `role_id` varchar(32) NOT NULL,
  `permission_id` varchar(32) NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  spring_security.t_role_permission 的数据：~3 rows (大约)
/*!40000 ALTER TABLE `t_role_permission` DISABLE KEYS */;
INSERT INTO `t_role_permission` (`role_id`, `permission_id`) VALUES
	('1', '1'),
	('1', '2'),
	('2', '2');
/*!40000 ALTER TABLE `t_role_permission` ENABLE KEYS */;

-- 导出  表 spring_security.t_user 结构
CREATE TABLE IF NOT EXISTS `t_user` (
  `id` bigint(20) NOT NULL COMMENT '用户id',
  `username` varchar(64) NOT NULL,
  `password` varchar(64) NOT NULL,
  `realname` varchar(255) NOT NULL COMMENT '真实姓名',
  `mobile` varchar(11) DEFAULT NULL COMMENT '手机号',
  `enabled` tinyint(1) DEFAULT NULL COMMENT '是否启用',
  `accountNonExpired` tinyint(1) NOT NULL DEFAULT '1',
  `accountNonLocked` tinyint(1) NOT NULL DEFAULT '1',
  `credentialsNonExpired` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- 正在导出表  spring_security.t_user 的数据：~2 rows (大约)
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` (`id`, `username`, `password`, `realname`, `mobile`, `enabled`, `accountNonExpired`, `accountNonLocked`, `credentialsNonExpired`) VALUES
	(1, 'dev', '$2a$10$IwyZkXIDuMJjmwBGyBuzlOKbpPN7cwL5sjWnYuSbWN9jL7lR9mv.a', '开发人员', NULL, 1, 1, 1, 1),
	(2, 'test', '$2a$10$IwyZkXIDuMJjmwBGyBuzlOKbpPN7cwL5sjWnYuSbWN9jL7lR9mv.a', '测试人员', NULL, 1, 1, 1, 1);
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;

-- 导出  表 spring_security.t_user_role 结构
CREATE TABLE IF NOT EXISTS `t_user_role` (
  `user_id` varchar(32) NOT NULL,
  `role_id` varchar(32) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  spring_security.t_user_role 的数据：~2 rows (大约)
/*!40000 ALTER TABLE `t_user_role` DISABLE KEYS */;
INSERT INTO `t_user_role` (`user_id`, `role_id`, `create_time`, `creator`) VALUES
	('1', '1', NULL, NULL),
	('2', '2', NULL, NULL);
/*!40000 ALTER TABLE `t_user_role` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
