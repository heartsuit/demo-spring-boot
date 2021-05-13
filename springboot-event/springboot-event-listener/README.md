1. 自定义事件MyEvent，继承自ApplicationEvent，其中可自定义事件数据类型；
2. 自定义监听器，作为Component交给Spring管理，通过注解@EventListener可在监听到对应事件时进行自定义操作。
3. 通过ApplicationContext的实例方法：publishEvent来发布自定义事件。
