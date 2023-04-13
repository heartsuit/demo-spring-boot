package com.heartsuit.springbootopcua.service;

import lombok.Data;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscriptionManager;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedDataItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Heartsuit
 * @Date 2022-12-11
 */
@Data
public class OpcUaClientService {
    // 批量订阅namespaceIndex默认为2
    private int batchNamespaceIndex = 2;

    // 批量订阅时的identifiers
    private List<String> batchIdentifiers;

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
            //排除系统行性节点，这些系统性节点名称一般都是以"_"开头
            if (Objects.requireNonNull(nd.getBrowseName().getName()).contains("_")) {
                continue;
            }
            System.out.println("Node= " + nd.getBrowseName().getName());
            listNode(client, nd);
        }
    }

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

    /**
     * 读取指定节点的值的重载方法
     * @param client
     * @param nodeId
     * @throws Exception
     */
    public void readNodeValue(OpcUaClient client, NodeId nodeId) throws Exception {
        //读取节点数据
        DataValue value = client.readValue(0.0, TimestampsToReturn.Neither, nodeId).get();

        // 状态
        System.out.println("Status: " + value.getStatusCode());

        //标识符
        String id = String.valueOf(nodeId.getIdentifier());
        System.out.println(id + ": " + value.getValue().getValue());
    }

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

    /**
     * 订阅单个节点的重载方法
     * @param client
     * @param nodeId
     * @throws Exception
     */
    public void subscribe(OpcUaClient client, NodeId nodeId) throws Exception {
        //创建发布间隔1000ms的订阅对象
        client
                .getSubscriptionManager()
                .createSubscription(1000.0)
                .thenAccept(t -> {
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

    /**
     * 批量订阅
     *
     * @param client
     * @throws Exception
     */
    public void subscribeBatchWithReconnect(OpcUaClient client) throws Exception {
        final CountDownLatch eventLatch = new CountDownLatch(1);

        //添加订阅监听器，用于处理断线重连后的订阅问题
        client.getSubscriptionManager().addSubscriptionListener(new CustomSubscriptionListener(client));

        //处理订阅业务
        handlerMultipleNode(client);

        //持续监听
        eventLatch.await();
    }

    /**
     *
     * 自定义订阅监听
     */
    private class CustomSubscriptionListener implements UaSubscriptionManager.SubscriptionListener {
        private final OpcUaClient client;

        CustomSubscriptionListener(OpcUaClient client) {
            this.client = client;
        }

        public void onKeepAlive(UaSubscription subscription, DateTime publishTime) {
            System.out.println("onKeepAlive");
        }

        public void onStatusChanged(UaSubscription subscription, StatusCode status) {
            System.out.println("onStatusChanged");
        }

        public void onPublishFailure(UaException exception) {
            System.out.println("onPublishFailure");
        }

        public void onNotificationDataLost(UaSubscription subscription) {
            System.out.println("onNotificationDataLost");
        }

        /**
         * 重连时，尝试恢复之前的订阅失败时，会调用此方法
         *
         * @param uaSubscription 订阅
         * @param statusCode     状态
         */
        public void onSubscriptionTransferFailed(UaSubscription uaSubscription, StatusCode statusCode) {
            System.out.println("恢复订阅失败 需要重新订阅");
            //在回调方法中重新订阅
            handlerMultipleNode(client);
        }
    }
}
