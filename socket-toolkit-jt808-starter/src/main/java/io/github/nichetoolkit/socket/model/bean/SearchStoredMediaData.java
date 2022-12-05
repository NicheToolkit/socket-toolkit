package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>SearchStoredMediaData </p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class SearchStoredMediaData {
    private byte type;
    private byte routeId;
    private byte eventCode;
    private byte[] beginTime;
    private byte[] endTime;
}
