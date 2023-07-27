## 背景

前面我们搭建了一个本地的 `PLC` 仿真环境，并通过 `KEPServerEX6` 读取 `PLC` 上的数据，最后还使用 `UAExpert` 作为OPC客户端完成从 `KEPServerEX6` 这个OPC服务器的数据读取与订阅功能。在这篇文章中，我们将通过 `SpringBoot` 集成 `Milo` 库实现一个 `OPC UA` 客户端，包括连接、遍历节点、读取、写入、订阅与批量订阅等功能。

## Milo库

`Milo` 库的 `GitHub` 地址：[https://github.com/eclipse/milo](https://github.com/eclipse/milo)

`Milo` 库提供了 `OPC UA` 的服务端和客户端 `SDK` ，显然，我们这里仅用到了**OPC UA Client SDK**。

## 引入依赖

`SpringBoot` 后端项目中引入 `Milo` 库依赖（客户端 `SDK` ）。

## 实现OPCUA客户端

### 连接

```java
    /**
     * 创建OPC UA客户端
     *
     * @param ip
     * @param port
     * @param suffix
     * @return
     * @throws Exception
     */
    public OpcUaClient connectOpcUaServer(String ip, String port, String suffix) throws Exception {
        String endPointUrl = "opc.tcp://" + ip + ":" + port + suffix;
        Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
        Files.createDirectories(securityTempDir);
        if (!Files.exists(securityTempDir)) {
            throw new Exception("unable to create security dir: " + securityTempDir);
        }
        OpcUaClient opcUaClient = OpcUaClient.create(endPointUrl,
                endpoints ->
                        endpoints.stream()
                                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                                .findFirst(),
                configBuilder ->
                        configBuilder
                                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                                .setApplicationUri("urn:eclipse:milo:examples:client")
                                //访问方式
                                .setIdentityProvider(new AnonymousProvider())
                                .setRequestTimeout(UInteger.valueOf(5000))
                                .build()
        );
        opcUaClient.connect().get();
        Thread.sleep(2000); // 线程休眠一下再返回对象，给创建过程一个时间。
        return opcUaClient;
    }
```

### 遍历节点

```java
    /**
     * 遍历树形节点
     *
     * @param client OPC UA客户端
     * @param uaNode 节点
     * @throws Exception
     */
    public void listNode(OpcUaClient client, UaNode uaNode) throws Exception {
        List<? extends UaNode> nodes;
        if (uaNode == null) {
            nodes = client.getAddressSpace().browseNodes(Identifiers.ObjectsFolder);
        } else {
            nodes = client.getAddressSpace().browseNodes(uaNode);
        }
        for (UaNode nd : nodes) {
            //排除系统性节点，这些系统性节点名称一般都是以"_"开头
            if (Objects.requireNonNull(nd.getBrowseName().getName()).contains("_")) {
                continue;
            }
            System.out.println("Node= " + nd.getBrowseName().getName());
            listNode(client, nd);
        }
    }
```

### 读取指定节点

```java
    /**
     * 读取节点数据
     *
     * namespaceIndex可以通过UaExpert客户端去查询，一般来说这个值是2。
     * identifier也可以通过UaExpert客户端去查询，这个值=通道名称.设备名称.标记名称
     *
     * @param client
     * @param namespaceIndex
     * @param identifier
     * @throws Exception
     */
    public void readNodeValue(OpcUaClient client, int namespaceIndex, String identifier) throws Exception {
        //节点
        NodeId nodeId = new NodeId(namespaceIndex, identifier);

        //读取节点数据
        DataValue value = client.readValue(0.0, TimestampsToReturn.Neither, nodeId).get();

        // 状态
        System.out.println("Status: " + value.getStatusCode());

        //标识符
        String id = String.valueOf(nodeId.getIdentifier());
        System.out.println(id + ": " + value.getValue().getValue());
    }
```

### 写入指定节点

```java
    /**
     * 写入节点数据
     *
     * @param client
     * @param namespaceIndex
     * @param identifier
     * @param value
     * @throws Exception
     */
    public void writeNodeValue(OpcUaClient client, int namespaceIndex, String identifier, Float value) throws Exception {
        //节点
        NodeId nodeId = new NodeId(namespaceIndex, identifier);
        //创建数据对象,此处的数据对象一定要定义类型，不然会出现类型错误，导致无法写入
        DataValue newValue = new DataValue(new Variant(value), null, null);
        //写入节点数据
        StatusCode statusCode = client.writeValue(nodeId, newValue).join();
        System.out.println("结果：" + statusCode.isGood());
    }
```

### 订阅指定节点

```java
    /**
     * 订阅(单个)
     *
     * @param client
     * @param namespaceIndex
     * @param identifier
     * @throws Exception
     */
    private static final AtomicInteger atomic = new AtomicInteger();

    public void subscribe(OpcUaClient client, int namespaceIndex, String identifier) throws Exception {
        //创建发布间隔1000ms的订阅对象
        client
                .getSubscriptionManager()
                .createSubscription(1000.0)
                .thenAccept(t -> {
                    //节点
                    NodeId nodeId = new NodeId(namespaceIndex, identifier);
                    ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);
                    //创建监控的参数
                    MonitoringParameters parameters = new MonitoringParameters(UInteger.valueOf(atomic.getAndIncrement()), 1000.0, null, UInteger.valueOf(10), true);
                    //创建监控项请求
                    //该请求最后用于创建订阅。
                    MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
                    List<MonitoredItemCreateRequest> requests = new ArrayList<>();
                    requests.add(request);
                    //创建监控项，并且注册变量值改变时候的回调函数。
                    t.createMonitoredItems(
                            TimestampsToReturn.Both,
                            requests,
                            (item, id) -> item.setValueConsumer((it, val) -> {
                                System.out.println("nodeid :" + it.getReadValueId().getNodeId());
                                System.out.println("value :" + val.getValue().getValue());
                            })
                    );
                }).get();

        //持续订阅
        Thread.sleep(Long.MAX_VALUE);
    }
```

### 批量订阅指定节点

```java
    /**
     * 批量订阅
     *
     * @param client
     * @throws Exception
     */
    public void subscribeBatch(OpcUaClient client) throws Exception {
        final CountDownLatch eventLatch = new CountDownLatch(1);
        //处理订阅业务
        handlerMultipleNode(client);
        //持续监听
        eventLatch.await();
    }

    /**
     * 处理订阅业务
     *
     * @param client OPC UA客户端
     */
    private void handlerMultipleNode(OpcUaClient client) {
        try {
            //创建订阅
            ManagedSubscription subscription = ManagedSubscription.create(client);
            List<NodeId> nodeIdList = new ArrayList<>();
            for (String id : batchIdentifiers) {
                nodeIdList.add(new NodeId(batchNamespaceIndex, id));
            }
            //监听
            List<ManagedDataItem> dataItemList = subscription.createDataItems(nodeIdList);
            for (ManagedDataItem managedDataItem : dataItemList) {
                managedDataItem.addDataValueListener((t) -> {
                    System.out.println(managedDataItem.getNodeId().getIdentifier().toString() + ":" + t.getValue().getValue().toString());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

关于**断线重连的批量订阅**，可以参考文末源码，我没有进行实际测试。

## 测试

### 连接KEPServerEX6的OPC UA服务器

将上一篇文章中的 `KEPServerEX6` 作为 `OPC UA` 服务器来测试我们实现的客户端功能。这里 `namespaceIndex` 和 `identifier` 参考 `KEPServerEX6` 的配置或者 `UAExpert` 的右上角 `Attribute` 显示。

![2023-03-26-15.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2023-03-26-15.jpg)

```java
public class OpcUaStart {
    public void start() throws Exception {
        OpcUaClientService opcUaClientService = new OpcUaClientService();

        // 与OPC UA服务端建立连接，并返回客户端实例
        OpcUaClient client = opcUaClientService.connectOpcUaServer("127.0.0.1", "49320", "");

        // 遍历所有节点
        opcUaClientService.listNode(client, null);

        // 读取指定节点的值
//        opcUaClientService.readNodeValue(client, 2, "Demo.1500PLC.D1");
//        opcUaClientService.readNodeValue(client, 2, "Demo.1500PLC.D2");

        // 向指定节点写入数据
        opcUaClientService.writeNodeValue(client, 2, "Demo.1500PLC.D1", 6f);

        // 订阅指定节点
//        opcUaClientService.subscribe(client, 2, "Demo.1500PLC.D1");

        // 批量订阅多个节点
        List<String> identifiers = new ArrayList<>();
        identifiers.add("Demo.1500PLC.D1");
        identifiers.add("Demo.1500PLC.D2");

        opcUaClientService.setBatchNamespaceIndex(2);
        opcUaClientService.setBatchIdentifiers(identifiers);

//        opcUaClientService.subscribeBatch(client);
        opcUaClientService.subscribeBatchWithReconnect(client);
    }
}
```

记得在启动类中开启 `OPC UA` 的客户端。

```java
@SpringBootApplication
public class SpringbootOpcuaApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringbootOpcuaApplication.class, args);
        OpcUaStart opcUa = new OpcUaStart();
        opcUa.start();
    }
}
```

### 连接Milo提供的测试性OPC UA服务器

`Milo` 官方提供了一个开放的 `OPC UA` 服务器： `opc.tcp://milo.digitalpetri.com:62541/milo` ，可以先使用 `UAExpert` 测试连接（我用的是匿名连接），查看其中的节点及地址信息。

![2023-04-15-MiloServer.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2023-04-15-MiloServer.jpg)

```java
public class OpcUaStart {
    public void start() throws Exception {
        OpcUaClientService opcUaClientService = new OpcUaClientService();

        // 与OPC UA服务端建立连接，并返回客户端实例
        OpcUaClient client = opcUaClientService.connectOpcUaServer("milo.digitalpetri.com", "62541", "/milo");

        // 遍历所有节点
//        opcUaClientService.listNode(client, null);

        // 读取指定节点的值
        opcUaClientService.readNodeValue(client, 2, "Dynamic/RandomInt32");
        opcUaClientService.readNodeValue(client, 2, "Dynamic/RandomInt64");

        // 向指定节点写入数据
//        opcUaClientService.writeNodeValue(client, 2, "Demo.1500PLC.D1", 6f);

        // 订阅指定节点
//        opcUaClientService.subscribe(client, 2, "Dynamic/RandomDouble");

        // 批量订阅多个节点
        List<String> identifiers = new ArrayList<>();
        identifiers.add("Dynamic/RandomDouble");
        identifiers.add("Dynamic/RandomFloat");

        opcUaClientService.setBatchNamespaceIndex(2);
        opcUaClientService.setBatchIdentifiers(identifiers);

//        opcUaClientService.subscribeBatch(client);
        opcUaClientService.subscribeBatchWithReconnect(client);
    }
}
```

测试结果如下：

![2023-04-15-MiloResult.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2023-04-15-MiloResult.jpg)

## 可能遇到的问题

> UaException: status=Bad_SessionClosed, message=The session was closed by the client.

原因分析： `opcUaClient.connect().get();` 是一个异步的过程，可能在读写的时候，连接还没有创建好。

解决方法： `Thread.sleep(2000);` // 线程休眠一下再返回对象，给创建过程一个时间。

## Reference

[https://blog.csdn.net/u013457145/article/details/121283612](https://blog.csdn.net/u013457145/article/details/121283612)

## Source Code

[https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-opcua](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-opcua)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***