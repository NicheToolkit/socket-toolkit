package io.github.nichetoolkit.socket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>DriverAlarmInfo 驾驶员身份信息采集报警</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class Jt808DriverAlarm implements Serializable {
    /**
     * IC卡拔出
     */
    private boolean pullOutCard;

    /**
     * 密钥认证未通过
     */
    private boolean unAuthentication;

    /**
     * 卡片已经锁定
     */
    private boolean locked;

    /**
     * 卡被拔出 ?
     */
    private boolean pullOut;

    /**
     * 数据校验失败
     */
    private boolean checkFailed;
}
