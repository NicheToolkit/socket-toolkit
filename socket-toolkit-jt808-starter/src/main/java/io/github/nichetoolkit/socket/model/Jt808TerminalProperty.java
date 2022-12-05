package io.github.nichetoolkit.socket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>TerminalProperty</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Getter
@Setter
public class Jt808TerminalProperty implements Serializable {

    /**
     * 是否适用于客车车辆
     */
    private boolean supportBus;

    /**
     * 是否适用于危险品车辆
     */
    private boolean supportDangerVehicle;

    /**
     * 是否适用于普通货运车辆
     */
    private boolean supportFreightVehicle;

    /**
     * 是否适用于出租车辆
     */
    private boolean supportTaxi;

    /**
     * 是否支持硬盘录像
     */
    private boolean supportRecording;

    /**
     * 是否分体机 还是 一体机
     */
    private boolean supportExtension;

    /**
     * 终端制造商ID 16进制字符串
     */
    private String manufacturer;

    /**
     * 终端型号 16进制字符串
     */
    private String terminalModel;

    /**
     * 终端ID 按照GB18030字符编码 解析字符串
     */
    private String terminalId;

    /**
     * 终端SIM卡 ICCID
     */
    private String iccid;

    /**
     * 终端硬件版本号
     */
    private String terminalHardVersion;

    /**
     * 终端固件版本号
     */
    private String terminalSoftVersion;

    /**
     * 是否支持GPS定位
     */
    private boolean supportGps;
    /**
     * 是否支持北斗定位
     */
    private boolean supportBeidou;
    /**
     * 是否支持GLONASS定位
     */
    private boolean supportGlonass;
    /**
     * 是否支持Galileo定位
     */
    private boolean supportGalileo;

    /**
     * 是否支持GPRS通信
     */
    private boolean supportGprs;
    /**
     * 是否支持CDMA通信
     */
    private boolean supportCdma;
    /**
     * 是否支持TD-SCDMA通信
     */
    private boolean supportTdscdma;
    /**
     * 是否支持WCDMA通信
     */
    private boolean supportWcdma;
    /**
     * 是否支持CDMA2000通信
     */
    private boolean supportCdma2000;
    /**
     * 是否支持TD-LTE通信
     */
    private boolean supportTdlte;
    /**
     * 是否支持其他通信方式
     */
    private boolean supportOther;
}
