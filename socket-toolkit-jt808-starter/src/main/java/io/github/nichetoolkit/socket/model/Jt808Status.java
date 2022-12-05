package io.github.nichetoolkit.socket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>StatusInfo 车辆定位中的状态信息</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class Jt808Status implements Serializable {
    /**
     * 0：ACC 关；1： ACC 开
     */
    private boolean acc;

    /**
     * 0：未定位；1：定位
     */
    private boolean positioning;

    /**
     * 0：北纬；1：南纬
     */
    private boolean south;

    /**
     * 0：东经；1：西经
     */
    private boolean west;

    /**
     * 0：运营状态；1：停运状态
     */
    private boolean suspended;

    /**
     * 0：经纬度未经保密插件加密；1：经纬度已经保密插件加密
     */
    private boolean encryption;

    /**
     * 1 紧急刹车系统采集的前撞预警
     */
    private boolean brakeSystemWarning;

    /**
     * 1 车道偏移预警
     */
    private boolean laneDepartureWarning;

    /**
     * 0：空车；1：半载；2：保留；3：满载
     */
    private int cargo;

    /**
     * 0：车辆油路正常；1：车辆油路断开
     */
    private boolean oilBreak;

    /**
     * 0：车辆电路正常；1：车辆电路断开
     */
    private boolean circuitBreak;

    /**
     * 0：车门解锁；1：车门加锁
     */
    private boolean locking;

    /**
     * 0：门 1 关；1：门 1 开（前门）
     */
    private boolean opening1;

    /**
     * 0：门 2 关；1：门 2 开（中门）
     */
    private boolean opening2;

    /**
     * 0：门 3 关；1：门 3 开（后门）
     */
    private boolean opening3;

    /**
     * 0：门 4 关；1：门 4 开（驾驶席门）
     */
    private boolean opening4;

    /**
     * 0：门 5 关；1：门 5 开（自定义）
     */
    private boolean opening5;

    /**
     * 0：未使用 GPS 卫星进行定位；1：使用 GPS 卫星进行定位
     */
    private boolean gps;

    /**
     * 0：未使用北斗卫星进行定位；1：使用北斗卫星进行定位
     */
    private boolean beidou;

    /**
     * 0：未使用 GLONASS 卫星进行定位；1：使用 GLONASS 卫星进行定位
     */
    private boolean glonass;

    /**
     * 0：未使用 Galileo 卫星进行定位；1：使用 Galileo 卫星进行定位
     */
    private boolean galileo;

    /**
     * 0 车辆处于停止状态 1 车辆处于行驶状态
     */
    private boolean vehicleStatus;
}
