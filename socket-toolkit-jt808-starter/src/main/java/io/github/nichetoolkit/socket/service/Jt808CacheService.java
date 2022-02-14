package io.github.nichetoolkit.socket.service;

import io.github.nichetoolkit.socket.manager.Jt808SessionManager;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Map;

/**
 * <p>Jt808CacheService</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public interface Jt808CacheService {
    /**
     * 是否含此电话号码对应的包
     * @param phone 终端对应 12 位电话号码
     * @return 是否含有
     */
    default boolean containsPackages(String phone) {
        Map<String, Map<Integer, byte[]>> stringMapMap = Jt808SessionManager.PACKAGE_MAP_REFERENCE.get();
        if (stringMapMap != null) {
            return stringMapMap.containsKey(phone);
        }
        return false;
    }

    /**
     * 设置电话号码对应的包组
     * @param phone    终端对应 12 位电话号码
     * @param packages 包列表
     */
    default void setPackages(String phone, Map<Integer, byte[]> packages) {
        Map<String, Map<Integer, byte[]>> stringMapMap = Jt808SessionManager.PACKAGE_MAP_REFERENCE.get();
        if (stringMapMap != null) {
            stringMapMap.put(phone, packages);
        } else {
            Jt808SessionManager.PACKAGE_MAP_REFERENCE = new SoftReference<>(Collections.singletonMap(phone, packages));
        }
    }

    /**
     * 获取电话号码对应的包组
     * @param phone 终端对应 12 位电话号码
     * @return 包列表
     */
    default Map<Integer, byte[]> getPackages(String phone) {
        Map<String, Map<Integer, byte[]>> stringMapMap = Jt808SessionManager.PACKAGE_MAP_REFERENCE.get();
        if (stringMapMap != null) {
            return stringMapMap.get(phone);
        }
        return null;
    }

    /**
     * 电话号码对应的会话是否已经鉴权
     * @param phone 终端对应 12 位电话号码
     * @return 鉴权与否
     */
    default boolean containsAuth(String phone) {
        return Jt808SessionManager.AUTH_MAP.containsKey(phone);
    }

    /**
     * 去掉电话号码对应的鉴权信息
     * @param phone 终端对应 12 位电话号码
     */
    default void removeAuth(String phone) {
        Jt808SessionManager.AUTH_MAP.remove(phone);
    }

    /**
     * 通过电话号码获取鉴权码
     * @param phone 终端对应 12 位电话号码
     * @return 鉴权码
     */
    default String getAuth(String phone) {
        return Jt808SessionManager.AUTH_MAP.get(phone);
    }

    /**
     * 设置电话号码对应的鉴权码
     * @param phone 终端对应 12 位电话号码
     * @param str   鉴权码
     */
    default void setAuth(String phone, String str) {
        Jt808SessionManager.AUTH_MAP.put(phone, str);
    }

    /**
     * 是否含此电话号码对应的包
     * @param phone 终端对应 12 位电话号码
     * @return 是否含有
     */
    default boolean containsSentPackages(String phone) {
        return Jt808SessionManager.SENT_PACKAGE_MAP.containsKey(phone);
    }

    /**
     * 设置电话号码对应的包组
     * @param phone    终端对应 12 位电话号码
     * @param packages 包列表
     */
    default void setSentPackages(String phone, Map<Integer, byte[]> packages) {
        Jt808SessionManager.SENT_PACKAGE_MAP.put(phone, packages);
    }

    /**
     * 获取电话号码对应的包组
     * @param phone 终端对应 12 位电话号码
     * @return 包列表
     */
    default Map<Integer, byte[]> getSentPackages(String phone) {
        return Jt808SessionManager.SENT_PACKAGE_MAP.get(phone);
    }
}
