## 定时任务

可通过Spring的@EnableScheduling，quartz，xxl-job, elastic-job等实现。

1. 通过Spring的注解实现极简的定时任务示例；
2. quartz普通模式的定时任务示例；
3. quartz的cron表达式定时任务示例；
4. 使用数据库表实现定时任务示例；

## 数据表

Quartz提供了11张数据表

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

| 序号   | 表名                   | 说明                             |
| -------| -----------------------| ---------------------------------|
| 1      |QRTZ_CALENDARS    |存储Quartz日历信息|
| 2      |QRTZ_CRON_TRIGGERS      |存放Cron类型的Trigger，包括Cron表达式和时区信息|
| 3      |QRTZ_FIRED_TRIGGERS      |存储与已触发的Trigger相关的状态信息，以及相联Job的执行信息|
| 4      |QRTZ_PAUSED_TRIGGER_GRPS    |存储已暂停的Trigger组的信息|
| 5      |QRTZ_SCHEDULER_STATE    |存储少量的Scheduler相关的状态信息|
| 6      |QRTZ_LOCKS      |存储锁信息，为多个节点调度提供分布式锁，实现分布式调度，默认有2个锁:STATE_ACCESS, TRIGGER_ACCESS|
| 7      |QRTZ_JOB_DETAILS      |存储每一个已配置的JobDetail信息|
| 8      |QRTZ_SIMPLE_TRIGGERS    |存储Simple类型的Trigger，包括重复次数、间隔、以及已触的次数|
| 9      |QRTZ_BLOG_TRIGGERS    |以Blob类型存储的Trigger|
| 10      |QRTZ_TRIGGERS      |存储已配置的Trigger的基本信息|
| 11      |QRTZ_SIMPROP_TRIGGERS      |存储CalendarIntervalTrigger和DailyTimeIntervalTrigger两种类型的触发器|

11张表的详细信息，参考：https://blog.csdn.net/xiaoniu_888/article/details/83181078

这里使用innodb的SQL文件，可在IDEA里的外部依赖中找到：

> .m2\repository\org\quartz-scheduler\quartz\2.3.2\quartz-2.3.2.jar!\org\quartz\impl\jdbcjobstore\tables_mysql_innodb.sql

Note：cron方式需要用到的4张数据表：QRTZ_TRIGGERS，QRTZ_CRON_TRIGGERS，QRTZ_FIRED_TRIGGERS，QRTZ_JOB_DETAILS。

## 任务管理

实现了RESTful 的任务管理：

- 添加任务
- 修改任务
- 暂停任务
- 恢复任务
- 删除任务
- 获取任务列表

统一响应封装，全局异常拦截，分页查询，Swagger3接口文档

## 分布式集群任务调度

无论集群中有多少应用实例，定时任务只会触发一次。

## Reference

https://blog.csdn.net/u013810234/article/details/122895512?spm=1001.2014.3001.5501