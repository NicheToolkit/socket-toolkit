package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>CircleArea 圆形区域 </p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class CircleArea {
    private byte[] id;
    private byte[] prop;
    private byte[] lat;
    private byte[] lon;
    private byte[] radius;
    private byte[] beginTime;
    private byte[] endTime;
    private byte[] highestSpeed;
    private byte overSpeedTime;
}
