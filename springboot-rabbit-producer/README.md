## Rabbit 基本操作 
- Windows环境

rabbitmq-service start   - Start the RabbitMQ service
rabbitmq-service stop    - Stop the RabbitMQ service
rabbitmq-service disable - Disable the RabbitMQ service
rabbitmq-service enable  - Enable the RabbitMQ service
rabbitmqctl start_app - 启动RabbitMQ
rabbitmqctl stop_app - 停止RabbitMQ

加用户
添加用户：rabbitmqctl add_user [username] [password]
删除用户：rabbitmqctl delete_user [username]

分配角色：rabbitmqctl set_user_tags [username] administrator

// 使用户user1具有vhost1这个virtual host中所有资源的配置、写、读权限以便管理其中的资源
rabbitmqctl  set_permissions -p vhost1 hello .* .* .* 

用户列表
rabbitmqctl list_users

删除用户：rabbitmqctl delete_user [username]
修改密码：rabbitmqctl change_password [username] [newpassword]

// 查看权限
rabbitmqctl list_user_permissions hello

rabbitmqctl list_permissions -p vhost1

// 清除权限
rabbitmqctl clear_permissions [-p VHostPath] User


虚拟主机列表
rabbitmqctl list_vhosts

交换器列表
rabbitmqctl list_exchanges

添加、删除虚拟主机
rabbitmqctl add_vhost [vhost_name]
rabbitmqctl delete_vhost [vhost_name]

修改虚拟机权限
rabbitmqctl set_permissions -p [vhost_name] [username] '.*' '.*' '.*'

设置角色
rabbitmqctl set_user_tags [username] administrator

启用web管理界面插件（15672）
rabbitmq-plugins enable rabbitmq_management

清空指定队列
rabbitmqctl purge_queue queue_name


## RabbitMQ 实现延迟消息发送

- 下载rabbitmq插件

地址：http://www.rabbitmq.com/community-plugins.html

- 启动插件

rabbitmq-plugins enable rabbitmq_delayed_message_exchange

- 重启MQ

rabbitmq-service stop    - Stop the RabbitMQ service
rabbitmq-service start   - Start the RabbitMQ service
