package io.github.nichetoolkit.socket.service;

import io.github.nichetoolkit.rest.util.GeneralUtils;
import io.github.nichetoolkit.socket.constant.SocketJt808Constants;
import io.github.nichetoolkit.socket.model.*;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>Jt808Service</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
public interface Jt808DataService {

    /**
     * 保存终端上传的 RSA 公钥
     * @param phone 对应的12位电话号码 默认首位为0
     * @param e RSA 公钥 {e, n} 中的 e
     * @param n RSA 公钥 {e, n} 中的 n
     */
    default void terminalRsa(String phone, byte[] e, byte[] n) {}

    /**
     * 获取终端RSA
     * @param phone 卡号
     * @return RSA en
     */
    default byte[] terminalRsa(String phone) { return null;}

    /**
     * 终端应答消息
     * @param phone 终端对应 12 位电话号码
     * @param flowId 应答对应平台流水号
     * @param answerId 应答对应平台消息ID
     * @param messageId 消息ID
     * @param messageBodyBytes 对应应答的消息体数据
     */
    default void terminalAnswer(String phone, int flowId, String answerId, String messageId, byte[] messageBodyBytes) {}

    /**
     * 终端心跳
     * @param phone 终端对应 12 位电话号码
     */
    default void terminalHeartbeat(String phone) {}

    /**
     * 终端注销
     * @param phone 终端对应 12 位电话号码
     */
    default void terminalCancel(String phone) {}

    /**
     * 终端注册
     * @param phone 终端对应 12 位电话号码
     * @param province 省域ID
     * @param city 市县域ID
     * @param manufacturer 制造商ID
     * @param deviceType 终端型号
     * @param deviceId 终端ID
     * @param licenseColor 车牌颜色
     * @param registerLicense 车牌号码[车牌颜色为0 时 表示VIN-车辆大架号]
     * @return 鉴权码 返回值有 0000001 车辆被注册 0000002 无车辆 0000003 终端被注册 0000004 或无终端 者 真正的鉴权码
     */
    default String terminalRegister(String phone, int province, int city, String manufacturer, String deviceType, String deviceId, byte licenseColor, String registerLicense) {
        return null;
    }

    /**
     * 保存定位信息、报警信息（包含持续和瞬间）、附加信息
     * @param phone 终端对应 12 位电话号码
     * @param location 定位信息 + 报警信息 + 附加信息 | 计算后的平台信息（可以放到计划任务延迟计算）
     * @param mediaId 连接多媒体数据的ID
     */
    default void terminalLocation(String phone, Jt808Location location, Integer mediaId) {}

    /**
     * 事件上报
     * @param phone 终端对应 12 位电话号码
     * @param eventReportAnswerId 应答
     */
    default void eventReport(String phone, byte eventReportAnswerId) {}

    /**
     * @param phone 终端对应 12 位电话号码
     * @param type 订阅消息类型
     */
    default void orderInfo(String phone, byte type) {}

    /**
     * @param phone 终端对应 12 位电话号码
     * @param type 取消订阅消息类型
     */
    default void cancelOrderInfo(String phone, byte type) {}

    /**
     * 电子运单
     * @param phone 终端对应 12 位电话号码
     * @param data 电子运单数据包内容
     */
    default void eBill(String phone, byte[] data) {}

    /**
     * 保存驾驶员信息
     * @param phone 终端对应 12 位电话号码
     * @param driver 驾驶员信息
     */
    default void driverInfo(String phone, Jt808Driver driver) {}

    /**
     * 保存CAN总线数据
     * @param phone 终端对应 12 位电话号码
     * @param canData CAN 总线数据
     */
    default void canData(String phone, Jt808CanData canData) {}

    /**
     * 保存多媒体信息
     * @param phone 终端对应 12 位电话号码
     * @param media 多媒体信息
     */
    default void mediaInfo(String phone, Jt808Media media) {}

    /**
     * 存储多媒体实体信息
     * @param phone 终端对应 12 位电话号码
     * @param mediaData 数据包
     * @param mediaId 连接多媒体数据的ID
     */
    default void mediaPackage(String phone, byte[] mediaData, Integer mediaId) {}

    /**
     * 保存透传数据
     * @param phone 终端对应 12 位电话号码
     * @param dataTransport 上行透传数据
     */
    default void dataTransport(String phone, Jt808DataTransport dataTransport) {}

    /**
     * 保存压缩上报数据
     * @param phone 终端对应 12 位电话号码
     * @param data 上报数据
     */
    default void compressData(String phone, byte[] data) {}

    /**
     * 终端鉴权
     * @param phone 卡号
     * @param authId 鉴权码
     * @param imei 2019版的 IMEI 号
     * @param softVersion 2019版的 软件版本号
     */
    default void terminalAuth(String phone, String authId, String imei, String softVersion) {}

}
