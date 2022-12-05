package io.github.nichetoolkit.socket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>MediaInfo </p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class Jt808Media implements Serializable {
    /**
     * 多媒体数据ID
     */
    private int mediaId;

    /**
     * 多媒体数据类型
     * 0：图像；1：音频；2：视频
     */
    private int mediaType;

    /**
     * 多媒体数据格式
     * 0：JPEG；1：TIF；2：MP3；3：WAV；4：WMV； 其他保留
     */
    private int mediaFormat;

    /**
     * 事件项编码
     * 0：平台下发指令；1：定时动作；2：抢劫报警触发；3：碰撞侧翻报警触发；4：门开拍照；
     * 5：门关拍照；6：车门由开变关，时速从＜20 公里到超过 20 公里；7：定距拍照；
     * 其他保留
     */
    private int eventNumber;

    /**
     * 通道ID
     */
    private int tunnelId;
}
