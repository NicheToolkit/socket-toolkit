package io.github.nichetoolkit.socket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Jt808Location</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Builder
@Setter
@Getter
public class Jt808Location implements Serializable {
    /**
     * 报警信息
     */
    private Jt808Alarm alarm;

    /**
     * 状态信息
     */
    private Jt808Status status;

    /**
     * 纬度
     */
    private double longitude;

    /**
     * 经度
     */
    private double latitude;

    /**
     * 高程
     */
    private int height;

    /**
     * 速度
     */
    private double speed;

    /**
     * 方向
     */
    private int direction;

    /**
     * 时间 YY-MM-DD-hh-mm-ss GMT+8 时间
     */
    private String datetime;

    /**
     * 附加信息
     */
    private List<Jt808LocationAttach> attaches;
}
