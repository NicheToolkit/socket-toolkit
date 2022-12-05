package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Contact 联系人 </p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class Contact {
    private byte sign;
    private byte phoneNumLength;
    private byte[] phoneNum;
    private byte nameLength;
    private byte[] name;
}
