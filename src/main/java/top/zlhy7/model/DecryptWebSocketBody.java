package top.zlhy7.model;

import lombok.Data;

/**
 * @author 沙福林
 * @date 2023/9/27 下午8:05
 * @description websocket消息体
 */
@Data
public class DecryptWebSocketBody {
    /**
     * 被监控的文件目录
     */
    private String monitoredFilePath;
    /**
     * 解密后目录
     */
    private String monitoredDecryptPath;
    /**
     * 心跳标识
     */
    private boolean heartbeat;
}
