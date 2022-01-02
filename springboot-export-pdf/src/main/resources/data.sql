-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.7.19-log - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 导出 standard-core 的数据库结构
CREATE DATABASE IF NOT EXISTS `standard-core` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `standard-core`;

-- 导出  表 standard-core.std_committee 结构
CREATE TABLE IF NOT EXISTS `std_committee` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `code` varchar(50) NOT NULL DEFAULT '' COMMENT '编号',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '技术委员会名称',
  `national_organization` varchar(50) NOT NULL DEFAULT '' COMMENT '对口全国组织',
  `number_session` tinyint(2) unsigned NOT NULL COMMENT '届数',
  `establish_date` date NOT NULL COMMENT '本届成立时间',
  `first_establish_date` date NOT NULL COMMENT '第一届成立时间',
  `build_unit_name` varchar(50) NOT NULL DEFAULT '' COMMENT '筹建单位名称',
  `guide_unit_name` varchar(50) NOT NULL DEFAULT '' COMMENT '业务指导单位名称',
  `professional_field` varchar(255) NOT NULL DEFAULT '' COMMENT '负责制修订地方标准的专业领域',
  `apply_id` bigint(20) NOT NULL COMMENT '申报人id',
  `approval_id` bigint(20) DEFAULT NULL COMMENT '审批人id',
  `all_approval_id` varchar(255) DEFAULT NULL COMMENT '所有审批人id，用逗号分割',
  `approval_status` char(1) NOT NULL DEFAULT '0' COMMENT '审批状态：0-未提交，1-已提交，2-待审批，3-已通过，4-被驳回',
  `create_by` varchar(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(20) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='技术委员会表';

-- 正在导出表  standard-core.std_committee 的数据：~1 rows (大约)
/*!40000 ALTER TABLE `std_committee` DISABLE KEYS */;
INSERT INTO `std_committee` (`id`, `code`, `name`, `national_organization`, `number_session`, `establish_date`, `first_establish_date`, `build_unit_name`, `guide_unit_name`, `professional_field`, `apply_id`, `approval_id`, `all_approval_id`, `approval_status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
	(1476760729380982786, '111', '111', '111', 1, '2021-02-02', '2021-02-02', 'qq', 'qq', 'qq', 10, 6, '10,6', '3', 'tc', '2021-12-31 11:42:49', 'taiyuan', '2021-12-31 11:43:53'),
	(1477482360448090113, 'demoData', 'demoData', 'demoData', 1, '2021-12-14', '2021-12-14', 'demoData', 'demoData', 'demoData', 10, 1, 'demoData', '1', 'tc', '2022-01-02 11:30:20', NULL, NULL);
/*!40000 ALTER TABLE `std_committee` ENABLE KEYS */;

-- 导出  表 standard-core.std_committee_branch 结构
CREATE TABLE IF NOT EXISTS `std_committee_branch` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `committee_id` bigint(20) NOT NULL COMMENT '技术委员会id',
  `code` varchar(50) NOT NULL DEFAULT '' COMMENT '编号',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
  `number_member` tinyint(2) unsigned NOT NULL COMMENT '委员数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='技术委员会下设分技术委员会或标准化技术专家组表';

-- 正在导出表  standard-core.std_committee_branch 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `std_committee_branch` DISABLE KEYS */;
INSERT INTO `std_committee_branch` (`id`, `committee_id`, `code`, `name`, `number_member`) VALUES
	(1, 1477482360448090113, 'demoData', 'demoData', 2),
	(2, 1477482360448090113, 'demoData', 'demoData', 2),
	(3, 1477482360448090113, 'demoData', 'demoData', 2),
	(4, 1477482360448090113, 'demoData', 'demoData', 2),
	(5, 1477482360448090113, 'demoData', 'demoData', 2),
	(6, 1477482360448090113, 'demoData', 'demoData', 2),
	(7, 1477482360448090113, 'demoData', 'demoData', 2),
	(8, 1477482360448090113, 'demoData', 'demoData', 2),
	(9, 1477482360448090113, 'demoData', 'demoData', 2),
	(10, 1477482360448090113, 'demoData', 'demoData', 2),
	(11, 1477482360448090113, 'demoData', 'demoData', 2),
	(12, 1477482360448090113, 'demoData', 'demoData', 2),
	(13, 1477482360448090113, 'demoData', 'demoData', 2),
	(14, 1477482360448090113, 'demoData', 'demoData', 2),
	(15, 1477482360448090113, 'demoData', 'demoData', 2),
	(16, 1477482360448090113, 'demoData', 'demoData', 2),
	(17, 1477482360448090113, 'demoData', 'demoData', 2),
	(18, 1477482360448090113, 'demoData', 'demoData', 2),
	(19, 1477482360448090113, 'demoData', 'demoData', 2),
	(20, 1477482360448090113, 'demoData', 'demoData', 2),
	(21, 1477482360448090113, 'demoData', 'demoData', 2),
	(22, 1477482360448090113, 'demoData', 'demoData', 2),
	(23, 1477482360448090113, 'demoData', 'demoData', 2),
	(24, 1477482360448090113, 'demoData', 'demoData', 2),
	(25, 1477482360448090113, 'demoData', 'demoData', 2),
	(26, 1477482360448090113, 'demoData', 'demoData', 2),
	(27, 1477482360448090113, 'demoData', 'demoData', 2),
	(28, 1477482360448090113, 'demoData', 'demoData', 2),
	(29, 1477482360448090113, 'demoData', 'demoData', 2),
	(30, 1477482360448090113, 'demoData', 'demoData', 2),
	(31, 1477482360448090113, 'demoData', 'demoData', 2),
	(32, 1477482360448090113, 'demoData', 'demoData', 2),
	(33, 1477482360448090113, 'demoData', 'demoData', 2),
	(34, 1477482360448090113, 'demoData', 'demoData', 2),
	(35, 1477482360448090113, 'demoData', 'demoData', 2),
	(36, 1477482360448090113, 'demoData', 'demoData', 2),
	(37, 1477482360448090113, 'demoData', 'demoData', 2),
	(38, 1477482360448090113, 'demoData', 'demoData', 2),
	(39, 1477482360448090113, 'demoData', 'demoData', 2),
	(40, 1477482360448090113, 'demoData', 'demoData', 2),
	(41, 1477482360448090113, 'demoData', 'demoData', 2),
	(42, 1477482360448090113, 'demoData', 'demoData', 2),
	(43, 1477482360448090113, 'demoData', 'demoData', 2),
	(44, 1477482360448090113, 'demoData', 'demoData', 2),
	(1477482361106595841, 1477482360448090113, 'demoData', 'demoData', 1),
	(1477482361119178754, 1477482360448090113, 'demoData', 'demoData', 2);
/*!40000 ALTER TABLE `std_committee_branch` ENABLE KEYS */;

-- 导出  表 standard-core.std_committee_secretariat 结构
CREATE TABLE IF NOT EXISTS `std_committee_secretariat` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
  `committee_id` bigint(20) NOT NULL COMMENT '技术委员会id',
  `unify_social_credit_code` varchar(50) NOT NULL DEFAULT '' COMMENT '统一社会信用代码',
  `unit_property` varchar(50) NOT NULL DEFAULT '' COMMENT '单位性质',
  `communication_address` varchar(50) NOT NULL DEFAULT '' COMMENT '通信地址',
  `postal_code` varchar(50) NOT NULL DEFAULT '' COMMENT '邮政编码',
  `phone` char(11) NOT NULL DEFAULT '' COMMENT '电话',
  `email` varchar(50) NOT NULL DEFAULT '' COMMENT '电子邮箱',
  `fax` varchar(50) NOT NULL DEFAULT '' COMMENT '传真',
  `number_committee_member` tinyint(2) unsigned NOT NULL COMMENT '委员数',
  `number_adviser` tinyint(2) unsigned NOT NULL COMMENT '顾问数',
  `number_observer` tinyint(2) unsigned NOT NULL COMMENT '观察员数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='技术委员会秘书处承担单位表';

-- 正在导出表  standard-core.std_committee_secretariat 的数据：~1 rows (大约)
/*!40000 ALTER TABLE `std_committee_secretariat` DISABLE KEYS */;
INSERT INTO `std_committee_secretariat` (`id`, `name`, `committee_id`, `unify_social_credit_code`, `unit_property`, `communication_address`, `postal_code`, `phone`, `email`, `fax`, `number_committee_member`, `number_adviser`, `number_observer`) VALUES
	(1476760729448091649, 'qq', 1476760729380982786, '33', '1', 'qq', '33', '33', 'qq', 'qq', 2, 1, 3),
	(1477482360586502145, 'demoData', 1477482360448090113, 'demoData', 'demoData', 'demoData', 'demoData', 'demoData', 'demoData', 'demoData', 1, 1, 1);
/*!40000 ALTER TABLE `std_committee_secretariat` ENABLE KEYS */;

-- 导出  表 standard-core.std_committee_secretariat_staff 结构
CREATE TABLE IF NOT EXISTS `std_committee_secretariat_staff` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `committee_secretariat_id` bigint(20) NOT NULL COMMENT '技术委员会秘书处id',
  `name` varchar(50) DEFAULT '' COMMENT '姓名',
  `type` varchar(50) DEFAULT '' COMMENT '秘书类型',
  `professional_title` varchar(50) DEFAULT '' COMMENT '职务/职称',
  `birthday` date DEFAULT NULL COMMENT '出生年月',
  `qualification` varchar(50) DEFAULT '' COMMENT '学历',
  `phone` char(11) DEFAULT '' COMMENT '电话',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='技术委员会秘书处工作人员表';

-- 正在导出表  standard-core.std_committee_secretariat_staff 的数据：~1 rows (大约)
/*!40000 ALTER TABLE `std_committee_secretariat_staff` DISABLE KEYS */;
INSERT INTO `std_committee_secretariat_staff` (`id`, `committee_secretariat_id`, `name`, `type`, `professional_title`, `birthday`, `qualification`, `phone`) VALUES
	(1476760729485840385, 1476760729448091649, '', '', '', NULL, '', ''),
	(1477482360926240769, 1477482360586502145, 'demoData', 'demoData', 'demoData', '2021-12-14', 'demoData', 'demoData'),
	(1477482360943017985, 1477482360586502145, 'demoData', 'demoData', 'demoData', '2021-12-14', 'demoData', 'demoData');
/*!40000 ALTER TABLE `std_committee_secretariat_staff` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
