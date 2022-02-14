package io.github.nichetoolkit.socket.service;

import io.github.nichetoolkit.rest.util.GeneralUtils;
import io.github.nichetoolkit.rest.util.JsonUtils;
import io.github.nichetoolkit.socket.configure.SocketJt808Properties;
import io.github.nichetoolkit.socket.model.Jt808Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Jt808DefaultService</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class Jt808DefaultDataService implements Jt808DataService {

    private SocketJt808Properties jt808Properties;

    @Autowired
    public Jt808DefaultDataService(SocketJt808Properties jt808Properties) {
        this.jt808Properties = jt808Properties;
    }

    @Override
    public void terminalLocation(String phone, Jt808Location location, Integer mediaId) {
        log.debug("device {} location info: {}", phone, JsonUtils.parseJson(location));
    }

    @Override
    public String terminalRegister(String phone, int province, int city, String manufacturer, String deviceType, String deviceId, byte licenseColor, String registerLicense) {
        String authCode;
        if (jt808Properties.getUseConstantAuth()) {
            authCode = jt808Properties.getConstantAuth();
        } else {
            authCode = GeneralUtils.randomHex(jt808Properties.getRandomAuthSize());
        }
        log.debug("device {} register with auth: {}", phone, authCode);
        return authCode;
    }
}
