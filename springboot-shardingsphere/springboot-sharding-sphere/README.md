1. 单库水平分表
singledb

单库：sharding-sphere1
```sql
CREATE TABLE `t_order_1` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`order_type` INT(11) NULL DEFAULT NULL,
	`customer_id` INT(11) NULL DEFAULT NULL,
	`amount` DECIMAL(10,2) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `t_order_2` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`order_type` INT(11) NULL DEFAULT NULL,
	`customer_id` INT(11) NULL DEFAULT NULL,
	`amount` DECIMAL(10,2) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
```

2. 默认分库分表
default
两个库：sharding-sphere1与sharding-sphere2，各有t_order_1与t_order_2两张表

3. 水平分库分表
horizontal
两个库：sharding-sphere1与sharding-sphere2，各有t_order_1与t_order_2两张表

4. 垂直分库分表（按业务分）
vertical
两个库：sharding-sphere1与sharding-sphere2，sharding-sphere1有t_order一张表，sharding-sphere2有t_customer一张表

5. 广播表（公共表/字典表）
两个库：sharding-sphere1与sharding-sphere2，均有dict_order_type一张表
```sql
CREATE TABLE `dict_order_type` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`order_type` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
```

6. 绑定表
两个库：sharding-sphere1与sharding-sphere2，各有t_order_1与t_order_2以及t_order_detail_1，t_order_detail_2四张表

```sql
CREATE TABLE `t_order_1` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`order_type` INT(11) NULL DEFAULT NULL,
	`customer_id` INT(11) NULL DEFAULT NULL,
	`amount` DECIMAL(10,2) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `t_order_2` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`order_type` INT(11) NULL DEFAULT NULL,
	`customer_id` INT(11) NULL DEFAULT NULL,
	`amount` DECIMAL(10,2) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `t_order_detail_1` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`detail` VARCHAR(50) NOT NULL,
	`order_id` INT(11) NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `t_order_detail_2` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`detail` VARCHAR(50) NOT NULL,
	`order_id` INT(11) NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
```

未手动设置id时报错：
org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.exceptions.PersistenceException: 
### Error updating database.  Cause: java.lang.IllegalArgumentException: Sharding value must implements Comparable.