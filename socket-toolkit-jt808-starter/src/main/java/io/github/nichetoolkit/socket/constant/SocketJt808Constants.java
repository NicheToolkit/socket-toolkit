package io.github.nichetoolkit.socket.constant;

import java.nio.charset.Charset;

/**
 * <p>SocketJt808Constants</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class SocketJt808Constants {

    public static final String GBK_ENCODING = "GBK";

    public static final Charset GBK_CHARSET = Charset.forName(GBK_ENCODING);

    public final static String FLOW_ID = "flowId";

    public final static String TCP = "tcp";

    public final static String TERMINAL_REG_HAS_VEHICLE = "0000001";
    public final static String TERMINAL_REG_NO_VEHICLE = "0000002";
    public final static String TERMINAL_REG_HAS_TERMINAL = "0000003";
    public final static String TERMINAL_REG_NO_TERMINAL = "0000004";

    public final static Integer PACKAGE_LENGTH = 1024;
    public final static Integer VERSION_2011 = 2011;
    public final static Integer VERSION_2013 = 2013;
    public final static Integer VERSION_2019 = 2019;
    public final static Integer VERSION_20132011 = 20132011;

    public final static Integer PHONE_2019_LENGTH = 10;
    public final static Integer PHONE_2013_LENGTH = 37;

    public final static byte BIN_0X01 = 0x01;
    public final static byte BIN_0X02 = 0x02;
    public final static byte BIN_0X04 = 0x04;
    public final static byte BIN_0X08 = 0x08;
    public final static byte BIN_0X10 = 0x10;
    public final static byte BIN_0X20 = 0x20;
    public final static byte BIN_0X40 = 0x40;
    public final static byte BIN_0X80 = (byte) 0x80;

    public final static int NUMBER_0 = 0;
    public final static int NUMBER_1 = 1;
    public final static int NUMBER_2 = 2;
    public final static int NUMBER_3 = 3;
    public final static int NUMBER_4 = 4;
    public final static int NUMBER_7 = 7;
    public final static int NUMBER_8 = 8;
    public final static int NUMBER_9 = 9;
    public final static int NUMBER_10 = 10;

    public final static int YEAR_2011 = 2011;
    public final static int YEAR_2013 = 2013;
    public final static int YEAR_2019 = 2019;
}
