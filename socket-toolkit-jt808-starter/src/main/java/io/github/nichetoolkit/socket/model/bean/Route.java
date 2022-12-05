package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>Route 路线</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class Route {
    private byte[] id;
    private byte[] prop;
    private byte[] beginTime;
    private byte[] endTime;
    private byte[] pointNum;
    private List<TurnPoint> list;
}
