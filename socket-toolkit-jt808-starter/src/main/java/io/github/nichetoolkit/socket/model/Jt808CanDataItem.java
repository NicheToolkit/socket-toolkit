package io.github.nichetoolkit.socket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>CanDataItem</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class Jt808CanDataItem implements Serializable {

    /**
     * CAN总线ID
     */
    private int canID;
    /**
     * 通道号
     * 0 CAN1 | 1 CAN2
     */
    private int canTunnel;

    /**
     * 帧类型
     * 0 标准帧 | 1 扩展帧
     */
    private int frameType;

    /**
     * 数据采集方式
     * 0 原始数据 | 1 采集区间的平均值
     */
    private int dataCollectModel;

    /**
     * CAN 数据
     */
    private byte[] data;
}
