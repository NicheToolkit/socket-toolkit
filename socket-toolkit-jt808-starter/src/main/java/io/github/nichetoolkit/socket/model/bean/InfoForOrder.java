package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>InfoForOrder 信息点播的信息项目 </p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class InfoForOrder {
    private byte type;
    private byte[] length;
    private byte[] name;
}
