-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.7.19-log - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  10.3.0.5771
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 boost-admin 的数据库结构
CREATE DATABASE IF NOT EXISTS `boost-admin` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `boost-admin`;

-- 导出  表 boost-admin.demo_employee 结构
CREATE TABLE IF NOT EXISTS `demo_employee` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `age` int(11) NOT NULL DEFAULT '0' COMMENT '年龄',
  `phone` varchar(15) DEFAULT NULL COMMENT '手机号码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1240569421151879170 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='测试MyBatisPlus';

-- 正在导出表  boost-admin.demo_employee 的数据：~4 rows (大约)
/*!40000 ALTER TABLE `demo_employee` DISABLE KEYS */;
INSERT INTO `demo_employee` (`id`, `name`, `age`, `phone`, `create_time`) VALUES
	(1, 'admin', 23, '18888888888', '2018-08-23 09:11:56'),
	(2, 'test啊是的撒', 28, '17777777777', '2018-12-27 20:05:26'),
	(1240569057522528258, '李文亮', 35, '15521344568', '2020-03-19 17:21:26'),
	(1240569421151879169, '李兰娟', 63, '15521344568', '2020-03-19 17:22:53');
/*!40000 ALTER TABLE `demo_employee` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
