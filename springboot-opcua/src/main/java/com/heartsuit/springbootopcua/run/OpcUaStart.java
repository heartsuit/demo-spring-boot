package com.heartsuit.springbootopcua.run;

import com.heartsuit.springbootopcua.service.OpcUaClientService;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2022-12-11
 */
@Service
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
//        OpcUaClientUtil.subscribe(client, 2, "Demo.1500PLC.D1");

        // 批量订阅多个节点
        List<String> identifiers = new ArrayList<>();
        identifiers.add("Demo.1500PLC.D1");
        identifiers.add("Demo.1500PLC.D2");

        opcUaClientService.setBatchNamespaceIndex(2);
        opcUaClientService.setBatchIdentifiers(identifiers);

//        OpcUaClientUtil.subscribeBatch(client);
        opcUaClientService.subscribeBatchWithReconnect(client);
    }
}
