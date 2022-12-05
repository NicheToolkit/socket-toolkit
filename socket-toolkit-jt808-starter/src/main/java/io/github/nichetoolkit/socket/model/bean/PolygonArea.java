package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>PolygonArea 多边形区域 </p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class PolygonArea {
    private byte[] id;
    private byte[] prop;
    private byte[] beginTime;
    private byte[] endTime;
    private byte[] highestSpeed;
    private byte overSpeedTime;
    private byte[] pointNum;
    private List<Point> list;
}
