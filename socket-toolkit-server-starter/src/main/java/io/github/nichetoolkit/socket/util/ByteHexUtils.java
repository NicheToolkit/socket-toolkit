package io.github.nichetoolkit.socket.util;


/**
 * <p>ByteHexUtils</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class ByteHexUtils {

    private final static String HEX_DIGITS = "0123456789ABCDEF";

    /**
     * hex 转 四字节数组
     * @param buffer long
     * @return byte[] byte数组
     */
    public long parseLong(byte[] buffer) {
        long values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8;
            values |= (buffer[i] & 0xff);
        }
        return values;
    }

    /**
     * hex 转 四字节数组
     * @param values long
     * @return byte[] byte数组
     */
    public static byte[] parseByte(long values) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((values >> offset) & 0xff);
        }
        return buffer;
    }

    /**
     * hex 转 四字节数组
     * @param hex String
     * @return byte[] byte数组
     */
    public static byte[] parseByte(String hex) {
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        hex = hex.toUpperCase();
        byte[] res = new byte[hex.length() / 2];
        for (int i = 0; i < res.length; i++) {
            int n = i * 2;
            int n_1 = n + 1;
            char c = hex.charAt(n);
            char c_1 = hex.charAt(n_1);
            int buf = HEX_DIGITS.indexOf(c);
            int buf_1 = HEX_DIGITS.indexOf(c_1);
            res[i] = (byte) (((buf << 4) & 0x000000F0) ^ (buf_1 & 0x0000000f));
        }
        return res;
    }

    /**
     * int 转 四字节数组
     * @param n int
     * @return byte[] byte数组
     */
    public static byte[] parseFourByte(int n) {
        byte[] buf = new byte[4];
        buf[0] = (byte) ((n >>> 24) & 0x000000ff);
        buf[1] = (byte) ((n >>> 16) & 0x000000ff);
        buf[2] = (byte) ((n >>> 8) & 0x000000ff);
        buf[3] = (byte) (n & 0x000000ff);
        return buf;
    }

    /**
     * int 转 二字节数组
     * @param n int
     * @return byte[] byte数组
     */
    public static byte[] parseTwoByte(int n) {
        byte[] buffer = new byte[2];
        buffer[0] = (byte) ((n >>> 8) & 0x000000ff);
        buffer[1] = (byte) (n & 0x000000ff);
        return buffer;
    }

    /**
     * 二字节数组转int
     * @param bytes 字节数组
     * @return int
     */
    public static int parseTwoInt(byte[] bytes) {
        return ((bytes[0] << 8) & 0xff00) ^ (bytes[1] & 0x00ff);
    }

    /**
     * 四字节数组转int
     * @param bytes 字节数组
     * @return int
     */
    public static int parseFourInt(byte[] bytes) {
        return ((((bytes[0] << 24) & 0xff000000) ^ ((bytes[1] << 16) & 0x00ff0000))
                ^ ((bytes[2] << 8) & 0x0000ff00)) ^ (bytes[3] & 0x000000ff);
    }

    /**
     * 截取指定位置的字节数组 [start end是数组脚标 从0开始 算start 不算end]
     * @param bytes 字节数组
     * @param start 开始脚标
     * @param end   结束脚标
     * @return byte[] 截取数组
     */
    public static byte[] subbyte(byte[] bytes, int start, int end) {
        byte[] buffer = new byte[end - start];
        for (int n = 0, i = start; i < end; i++, n++) {
            buffer[n] = bytes[i];
        }
        return buffer;
    }

    /**
     * 截取指定位置到末尾的字节数组 [end是数组脚标 从0开始]
     * @param bytes 字节数组
     * @param start 开始脚标
     * @return byte[] 截取数组
     */
    public static byte[] subbyte(byte[] bytes, int start) {
        byte[] buffer = new byte[bytes.length - start];
        for (int n = 0, i = start; i < bytes.length; i++, n++) {
            buffer[n] = bytes[i];
        }
        return buffer;
    }

    /**
     * 拼接多个字节数组
     * @param bytesArray 字节数组数组
     * @return byte[] 字节数组
     */
    public static byte[] union(byte[]... bytesArray) {
        byte[] buffer;
        int length = 0;
        for (byte[] bytes : bytesArray) {
            length += bytes.length;
        }
        buffer = new byte[length];
        int pos = 0;
        for (byte[] bytes : bytesArray) {
            for (byte buf : bytes) {
                buffer[pos] = buf;
                pos++;
            }
        }
        return buffer;
    }

    /**
     * 拼接两个字节数组
     * @param source 源字节数组
     * @param target 目标字节数组
     * @return byte[] 字节数组
     */
    public static byte[] union(byte[] source, byte[] target) {
        byte[] buffer = new byte[source.length + target.length];
        System.arraycopy(source, 0, buffer, 0, source.length);
        System.arraycopy(target, 0, buffer, source.length, target.length);
        return buffer;
    }

    /**
     * 将字节 转为 16进制字符串
     * @param bytes 将字节
     * @return String 16进制字符串
     */
    public static String parseHex(byte bytes) {
        String hex = Integer.toHexString(bytes);
        if (hex.length() > 2) {
            hex = hex.substring(hex.length() - 2);
        } else if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * 将字节数组 转为 16进制字符串
     * @param bytes 将字节数组
     * @return String 16进制字符串
     */
    public static String parseHex(byte[] bytes) {
        StringBuilder hexBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i]);
            if (hex.length() > 2) {
                hex = hex.substring(hex.length() - 2);
            } else if (hex.length() < 2) {
                hex = "0" + hex;
            }
            hexBuilder.append(hex);
        }
        return hexBuilder.toString().toUpperCase();
    }

    /**
     * 将字节数组 转为 BCD码字符串 [字节数组长度为1]
     * @param bytes 字节数组
     * @return String BCD码字符串
     */
    public static String parseBcd(byte[] bytes) {
        byte b = bytes[0];
        byte c = (byte) (b & 0x0f);
        byte d = (byte) ((b >>> 4) & 0x0f);
        String bcd = ("" + d) + c;
        if (bcd.length() == 2) {
            return bcd;
        } else {
            return "99";
        }
    }

    /**
     * 将字节数组 转为 BCD码字符串 [字节数组长度为1]
     * @param bytes 字节数组
     * @return String BCD码字符串
     */
    public static String parseBcds(byte[] bytes) {
        StringBuilder bcdBuilder = new StringBuilder();
        for (byte buf : bytes) {
            bcdBuilder.append(parseBcd(new byte[]{buf}));
        }
        return bcdBuilder.toString();
    }
}
