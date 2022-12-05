package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Parameter 终端控制参数</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class Parameter {
    /**
     * 参数ID
     */
    private byte[] parameterId;
    /**
     * 参数值长度
     */
    private byte length;
    /**
     * 参数值
     */
    private byte[] value;
}

