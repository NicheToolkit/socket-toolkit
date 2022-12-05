package io.github.nichetoolkit.socket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>Jt808CanData</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class Jt808CanData implements Serializable {

    /**
     * 数据接受时间戳
     */
    private long timestamp;

    /**
     * 数据接收时间 hh-mm-ss-msms
     */
    private String receiveTime;

    /**
     * CAN总线数据项
     */
    private byte[] data;

}
