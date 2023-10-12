package top.zlhy7.service;

import com.alibaba.fastjson2.JSONObject;
import com.github.linyuzai.connection.loadbalance.core.extension.PathMessage;
import com.github.linyuzai.connection.loadbalance.core.extension.UserMessage;
import com.github.linyuzai.connection.loadbalance.core.message.MessageReceiveEvent;
import com.github.linyuzai.connection.loadbalance.websocket.EnableWebSocketLoadBalanceConcept;
import com.github.linyuzai.connection.loadbalance.websocket.concept.WebSocketLoadBalanceConcept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import top.zlhy7.model.DecryptWebSocketBody;

import javax.annotation.PostConstruct;

/**
 * @author 沙福林
 * @date 2023/9/27 下午7:41
 * @description websocket服务
 */
@Slf4j
@Service
@EnableWebSocketLoadBalanceConcept
public class WebSocketService {
    /**
     * 监听文件
     */
    @Autowired
    private MonitoredFileService monitoredFileService;
    /**
     * WebSocket操作
     */
    @Autowired
    private WebSocketLoadBalanceConcept webSocketLoadBalanceConcept;

    /**
     * 初始化
     */
    @PostConstruct
    public void init(){
        monitoredFileService.setWebSocketService(this);
    }
    /**
     * 监听消息接收事件,相当于接收消息以及消息处理
     * @param messageReceiveEvent
     */
    @EventListener
    public void MessageReceiveEventHanlder(MessageReceiveEvent messageReceiveEvent){
        String body = messageReceiveEvent.getMessage().getPayload().toString();
        log.info("接收到消息:\n{}", body);
        if(!body.startsWith("{")){
            // 这里就是心跳消息
            return;
        }
        DecryptWebSocketBody decryptWebSocketBody = JSONObject.parseObject(body, DecryptWebSocketBody.class);
        // 心跳就跳过
        if (decryptWebSocketBody.isHeartbeat()) {
            return;
        }
        // 要处理信息就一直打印
        try {
            monitoredFileService.decrypt(decryptWebSocketBody,this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 发送消息
     * @param msgFormat 字符串模板
     * @param objs 参数
     * @return
     * @author 沙福林 on 2023/9/27 下午8:13
     */
    public void sendPathMsg(String msgFormat,Object ... objs){
        webSocketLoadBalanceConcept.send(new PathMessage(String.format(msgFormat,objs), "sample"));
    }
    /**
     * 发送消息
     * @param userId 发给谁，0自定义解密 1配置文件动态监控
     * @param msgFormat 字符串模板
     * @param objs 参数
     * @return
     * @author 沙福林 on 2023-10-12 15:19:12
     */
    public void sendUserMsg(String userId,String msgFormat,Object ... objs){
        webSocketLoadBalanceConcept.send(new UserMessage(String.format(msgFormat,objs), userId));
    }
}
