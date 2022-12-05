package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>TerminalUpdatePkg 终端升级包</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class TerminalUpdatePkg {
    private byte updateType;
    private byte[] producerId;
    private byte versionLength;
    private byte[] version;
    private byte[] dataLength;
    private byte[] data;
}
