package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>RectangleArea 矩形区域</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class RectangleArea {
    private byte[] id;
    private byte[] prop;
    private byte[] leftUpLat;
    private byte[] leftUpLon;
    private byte[] rightDownLat;
    private byte[] rightDownLon;
    private byte[] beginTime;
    private byte[] endTime;
    private byte[] highestSpeed;
    private byte overSpeedTime;
}
