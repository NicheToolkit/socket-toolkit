package io.github.nichetoolkit.socket.model.bean;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>CameraInfo 摄像机信息 </p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class CameraInfo {
    private byte id;
    private byte[] comm;
    private byte[] spaceTime;
    private byte saveSign;
    private byte resolution;
    private byte quality;
    private byte luminance;
    private byte contrast;
    private byte saturation;
    private byte tone;
}
