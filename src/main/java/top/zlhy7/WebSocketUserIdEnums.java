package top.zlhy7;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 沙福林
 * @date 2023/10/12 下午3:27
 * @description
 */
@RequiredArgsConstructor
@Getter
public enum WebSocketUserIdEnums {
    CUSTOM_PATH("1","自定义解密"),
    LOCAL_MONITOR("2","本地文件监控");
    /**
     * 用户id
     */
    private final String userId;
    /**
     * 描述
     */
    private final String description;
}
