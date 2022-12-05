package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Event 事件 </p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class Event {
    private byte id;
    private byte length;
    private byte[] content;
}
