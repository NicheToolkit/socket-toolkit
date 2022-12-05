package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>TurnPoint 拐点</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class TurnPoint {
    private byte[] id;
    private byte[] routeId;
    private byte[] lat;
    private byte[] lon;
    private byte width;
    private byte prop;
    private byte[] driveOverValue;
    private byte[] driveLowerValue;
    private byte[] highestSpeed;
    private byte overSpeedTime;
}
