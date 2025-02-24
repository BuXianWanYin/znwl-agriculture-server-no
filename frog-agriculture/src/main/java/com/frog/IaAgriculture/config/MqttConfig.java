//package com.frog.IaAgriculture.config;
//
//
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.Resource;
//
///**
// * MQTT配置
// */
//@Configuration
//public class MqttConfig {
//
//    // 定义日志记录器
//    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);
//
//    @Value("${mqtt.broker}")
//    private String broker;
//    @Value("${mqtt.clientId}")
//    private String clientId;
//    @Value("${mqtt.userName}")
//    private String userName;
//    @Value("${mqtt.password}")
//    private String password;
//    @Value("${mqtt.subTopic}")
//    private String subTopic;
//    @Value("${mqtt.qos:1}")
//    private int qos;
//    @Value("${mqtt.reconnectInterval:10}")
//    private int reconnectInterval;
//
//    @Resource
//    private OnMessageCallback onMessageCallback;
//
//    private MqttClient client;
//
//    /**
//     * 创建并返回Mqtt客户端
//     *
//     * @return Mqtt客户端
//     * @throws MqttException 异常
//     */
//    @Bean
//    public MqttClient mqttClient() throws MqttException {
//        connectClient(); // 连接客户端
//        return client;
//    }
//
//    /**
//     * 连接MQTT客户端并设置回调
//     */
//
//
////    任务描述：
////    实现 MQTT 客户端的初始化和连接，并确保客户端在连接成功后，能够订阅指定的主题
////    技术点描述：
////    MQTT 客户端初始化与连接：
////    使用 MemoryPersistence 初始化客户端。
////    配置用户名、密码、自动重连、连接超时、心跳等选项，确保稳定连接。
////    尝试与服务器建立连接，连接失败时启动自动重连机制。
////    MQTT 订阅主题：
////    成功连接后，使用 client.subscribe() 订阅 subTopic 主题。
////    设置 QoS 等级，保证消息传输可靠性。
////    订阅失败时自动重试，确保消息接收的连续性。
//    private void connectClient() throws MqttException {
//        MemoryPersistence persistence = new MemoryPersistence();
//        client = new MqttClient(broker, clientId, persistence);
//
//        // 配置连接选项
//        MqttConnectOptions connOpts = new MqttConnectOptions();
//        connOpts.setUserName(userName);
//        connOpts.setPassword(password.toCharArray());
//        connOpts.setCleanSession(true);
//        connOpts.setAutomaticReconnect(true);
//        connOpts.setConnectionTimeout(10); // 设置连接超时时间
//        connOpts.setKeepAliveInterval(120); // 设置心跳时间
//
//        // 设置回调函数
//        client.setCallback(onMessageCallback);
//
//        try {
//            // 尝试建立连接并订阅主题
//           // logger.info("正在连接站点: {}", broker);
//            client.connect(connOpts);
//            client.subscribe(subTopic, qos);
//           // logger.info("已连接并订阅主题: {}", subTopic);
//        } catch (MqttException e) {
//          //  logger.error("连接站点失败，{} 秒后重试: {}", reconnectInterval, e.getMessage());
//            retryConnect(); // 连接失败，尝试重连
//        }
//    }
//
//    /**
//     * 异步发布消息，提高并发处理能力
//     *
//     * @param topic   发布的主题
//     * @param payload 消息内容
//     */
//
//    public void publishAsync(String topic, String payload) {
//        if (client != null && client.isConnected()) {
//            try {
//                MqttMessage message = new MqttMessage(payload.getBytes());
//                message.setQos(qos);
//                client.publish(topic, message);
//              //  logger.info("成功发布消息到主题: {}，消息内容: {}，使用的站点: {}", topic, payload, broker);
//            } catch (MqttException e) {
//              //  logger.error("发布消息失败，重新尝试: {}", e.getMessage());
//                retryPublish(topic, payload); // 发布失败，尝试重发
//            }
//        } else {
//         //   logger.error("MQTT客户端未连接，无法发布消息");
//            retryConnect(); // 尝试重新连接
//        }
//    }
//
//    /**
//     * 重试连接客户端
//     */
//    private void retryConnect() {
//        new Thread(() -> {
//            while (!client.isConnected()) {
//                try {
//                    Thread.sleep(reconnectInterval * 1000L);
//                   // logger.info("尝试重新连接...");
//                    client.connect();
//                    client.subscribe(subTopic, qos);
//                   // logger.info("重新连接并订阅主题: {}", subTopic);
//                } catch (InterruptedException | MqttException e) {
//                   // logger.error("重连失败: {}", e.getMessage());
//                }
//            }
//        }).start();
//    }
//
//
//
//    /**
//     * 重试发布消息
//     *
//     * @param topic   发布的主题
//     * @param payload 消息内容
//     */
//    private void retryPublish(String topic, String payload) {
//        new Thread(() -> {
//            while (client == null || !client.isConnected()) {
//                try {
//                    Thread.sleep(reconnectInterval * 1000L);
//                   // logger.info("等待客户端重新连接...");
//                } catch (InterruptedException e) {
//                   // logger.error("等待连接时发生错误: {}", e.getMessage());
//                }
//            }
//            try {
//                MqttMessage message = new MqttMessage(payload.getBytes());
//                message.setQos(qos);
//                client.publish(topic, message);
//               // logger.info("重试发布消息到主题: {}，消息内容: {}", topic, payload);
//            } catch (MqttException e) {
//                //logger.error("重试发布消息失败: {}", e.getMessage());
//            }
//        }).start();
//    }
//}
