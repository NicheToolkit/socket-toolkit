package io.github.nichetoolkit.socket.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>SocketJt808Properties</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Component
@ConfigurationProperties(prefix = "nichetoolkit.socket.jt808")
public class SocketJt808Properties {
    /** 是否鉴权 */
    private Boolean auth = true;
    /** 鉴权消息Id */
    private String[] authIds = new String[]{"0102", "0100"};

    private Boolean useConstantAuth = false;

    private Integer randomAuthSize = 18;
    //736875616E67776569
    private String constantAuth = "736875616E67776569";

    public SocketJt808Properties() {
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    public String[] getAuthIds() {
        return authIds;
    }

    public void setAuthIds(String[] authIds) {
        this.authIds = authIds;
    }

    public Boolean getUseConstantAuth() {
        return useConstantAuth;
    }

    public void setUseConstantAuth(Boolean useConstantAuth) {
        this.useConstantAuth = useConstantAuth;
    }

    public Integer getRandomAuthSize() {
        return randomAuthSize;
    }

    public void setRandomAuthSize(Integer randomAuthSize) {
        this.randomAuthSize = randomAuthSize;
    }

    public String getConstantAuth() {
        return constantAuth;
    }

    public void setConstantAuth(String constantAuth) {
        this.constantAuth = constantAuth;
    }
}
