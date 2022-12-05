package io.github.nichetoolkit.socket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>DriverInfo</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class Jt808Driver implements Serializable {
    /**
     * 司机名称
     */
    private String driverName;
    /**
     * 司机身份证号 仅2011 2019
     */
    private String idCardNumber;
    /**
     * 司机从业资格证号码
     */
    private String certificateNumber;
    /**
     * 发证机构名称
     */
    private String certificatePublishAgentName;
    /**
     * 证件有效期 仅2013
     */
    private String certificateLimitDate;

    /**
     * 驾驶员身份采集问题
     */
    private Jt808DriverAlarm driverAlarm;

    /**
     * 插拔卡时间 YY-MM-DD-hh-mm-ss
     */
    private String datetime;

    /**
     * 是否采集成功 因为需要采集报警所以不能只传回空值
     */
    private boolean success;
}
