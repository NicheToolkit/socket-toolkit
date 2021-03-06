package io.github.nichetoolkit.socket.manager;

import io.github.nichetoolkit.rest.RestException;
import io.github.nichetoolkit.rest.util.GeneralUtils;
import io.github.nichetoolkit.socket.configure.SocketJt808Properties;
import io.github.nichetoolkit.socket.constant.SocketJt808Constants;
import io.github.nichetoolkit.socket.model.Jt808Message;
import io.github.nichetoolkit.socket.server.handler.SocketHandlerManager;
import io.github.nichetoolkit.socket.server.handler.SocketMessageHandler;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.service.Jt808CacheService;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Jt808MessageHandler</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
@Component
public class Jt808MessageHandler implements SocketMessageHandler {

    @Autowired
    private Jt808CacheService cacheService;

    @Autowired
    private SocketJt808Properties jt808Properties;



    @Override
    public void beforeMessage(Object jt808Message) throws RestException {

    }

    @Override
    public void doHandle(Object session, Object message) throws RestException {
        byte[] messageBytes = (byte[]) message;
        Jt808Message jt808Message = parseMessage(messageBytes);
        handleSession(jt808Message, session);
        handleMessage(session,jt808Message);
    }

    @Override
    public void afterSession(Object session) throws RestException {

    }

    public void handleMessage(Object session,Jt808Message jt808Message) throws RestException {
        byte[] response = null;
        String phone = jt808Message.getPhone();
        byte[] originDataBytes = jt808Message.getOriginDataBytes();
        if (jt808Message.isSlicePackage()) {
            int totalPackageSize = ByteHexUtils.parseTwoInt(jt808Message.getPackageSizeBytes());
            int currentPackageIndex = ByteHexUtils.parseTwoInt(jt808Message.getPackageIndexBytes());
            /** ????????????????????????????????? ???????????????????????????????????? ??????????????????????????????????????? */
            if (totalPackageSize >= currentPackageIndex) {
                if (!cacheService.containsPackages(phone)) {
                    ConcurrentHashMap<Integer, byte[]> packageBufferMap = new ConcurrentHashMap<>(totalPackageSize);
                    cacheService.setPackages(phone, packageBufferMap);
                }
                Map<Integer, byte[]> packageBufferMap = cacheService.getPackages(phone);
                if (packageBufferMap != null) {
                    packageBufferMap.put(currentPackageIndex, originDataBytes);
                }
            }
            /** ????????????????????????????????????????????????????????????????????? ?????????????????????????????? ??????????????????????????? */
            if (totalPackageSize == currentPackageIndex) {
                /** ????????? ??????????????????????????? */
                if (isFinishPackage(phone, totalPackageSize)) {
                    /** ??????????????? ????????? */
                    byte[] bytes = mergePackage(phone, totalPackageSize);
                    if (GeneralUtils.isNotEmpty(bytes)) {
                        response = handlePackage(bytes);
                    }
                } else {
                    /** ?????????????????? ???????????? ???????????????????????? */
                    byte[] flowId = null;
                    /** ??????id?????? */
                    byte[] idList = new byte[]{};
                    /** ???????????? */
                    byte count = 0;
                    Map<Integer, byte[]> packageBufferMap = cacheService.getPackages(phone);
                    if (GeneralUtils.isNotEmpty(packageBufferMap)) {
                        for (int i = 1; i <= totalPackageSize; i++) {
                            if (GeneralUtils.isEmpty(flowId)) {
                                if (jt808Message.isVersion2019()) {
                                    flowId = ByteHexUtils.subbyte(packageBufferMap.get(1), 15, 17);
                                } else {
                                    flowId = ByteHexUtils.subbyte(packageBufferMap.get(1), 10, 12);
                                }
                            }
                            if (!packageBufferMap.containsKey(i)) {
                                count++;
                                if (jt808Message.isVersion2019()) {
                                    idList = ByteHexUtils.union(idList, ByteHexUtils.subbyte(packageBufferMap.get(i), 19, 21));
                                } else {
                                    idList = ByteHexUtils.union(idList, ByteHexUtils.subbyte(packageBufferMap.get(i), 14, 16));
                                }
                            }
                        }
                    }
                    if (GeneralUtils.isNotEmpty(flowId)) {
                        response = Jt808Utils.buildJt8003(jt808Message.getPhoneBytes(), flowId, count, idList);
                    }
                }
            }
        } else {
            response = handlePackage(originDataBytes);
        }
        if (response == null) {
            response = Jt808Utils.buildJt8001(jt808Message.getPhoneBytes(), jt808Message.getFlowIdBytes(), jt808Message.getMessageIdBytes(), (byte) 0x00);
        }
        /** ????????????????????? */
        int packageLength = SocketJt808Constants.PACKAGE_LENGTH;
        if (response.length > packageLength) {
            /** ???????????? */
            sentPackage(response, Jt808SessionManager.get(phone));
        } else {
            if (session instanceof IoSession) {
                ((IoSession) Jt808SessionManager.get(phone)).write(response);
            } else if (session instanceof ChannelHandlerContext) {
                ((ChannelHandlerContext) Jt808SessionManager.get(phone)).writeAndFlush(response);
            }
        }
    }

    public byte[] handlePackage(byte[] messageBytes) throws RestException {
        byte[] messageBodyBytes;

        int offset = 0;

        final byte[] messageIdBytes = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        final byte[] messageBodyPropertyBytes = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        /** ?????????????????????????????????????????? ??????????????? 2019???????????? ????????????????????? */
        byte[] phoneBytes;
        if (Jt808Utils.isVersion2019(messageBodyPropertyBytes)) {
            /** ?????? ?????????????????? */
            offset++;
            phoneBytes = new byte[]{
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++],
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++]
            };
        } else {
            phoneBytes = new byte[]{
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++],
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++]
            };
        }
        final byte[] flowIdBytes = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        int messageLength = SocketJt808Constants.PACKAGE_LENGTH;
        if (messageBytes.length > messageLength) {
            /** ???????????????????????????????????????????????? ????????????????????????????????? ??????????????????????????????????????? */
            /** ??????????????????????????? */
            offset += 4;
            messageBodyBytes = ByteHexUtils.subbyte(messageBytes, offset);
        } else {
            int bodyLength = messageBytes.length - 1 - offset;
            messageBodyBytes = new byte[bodyLength];
            for (int i = 0; i < messageBodyBytes.length; i++) {
                messageBodyBytes[i] = messageBytes[offset++];
            }
        }
        String phone = ByteHexUtils.parseHex(phoneBytes);
        int messageId = ByteHexUtils.parseTwoInt(messageIdBytes);
        /** ?????????????????? ??????????????????????????? ?????????????????????????????????????????? */
        /** ??????????????????????????? ????????????????????????????????? ?????? ????????? ???????????????????????????Map */
        boolean isAuth = cacheService.containsAuth(phone);
        if (jt808Properties.getAuth()) {
            /** ??????????????? ??????????????? authMsgId ?????????????????? */
            if (!isAuth) {
                String[] authIds = jt808Properties.getAuthIds();
                for (String authId : authIds) {
                    if (authId.length() == 4 && messageId == ByteHexUtils.parseTwoInt(ByteHexUtils.parseByte(authId))) {
                        isAuth = true;
                        break;
                    }
                }
            }
        } else {
            /** ????????????????????? ???????????????true ????????? */
            isAuth = true;
        }
        if (!isAuth) {
            /** ???????????????????????? */
            return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x01);
        }
        SocketPackageHandler packageHandler = SocketHandlerManager.handler(messageId);
        return packageHandler.handle(phoneBytes, flowIdBytes, messageIdBytes, messageBodyBytes);
    }

    public void sentPackage(byte[] bytes, Object session) {
        byte[] messageBodyPropertyBytes = new byte[]{bytes[2], bytes[3]};
        boolean isVersion2019 = Jt808Utils.isVersion2019(messageBodyPropertyBytes);
        /** ?????????????????? */
        byte[] messageBodyBytes = ByteHexUtils.subbyte(bytes, 12);
        /** ????????? */
        int packageSize = messageBodyBytes.length % 1023 == 0 ? messageBodyBytes.length / 1023 : messageBodyBytes.length / 1023 + 1;

        /** ??????Id */
        byte[] messageIdBytes = ByteHexUtils.subbyte(bytes, 0, 2);
        /** Sim?????? */
        byte[] phoneBytes;
        if (isVersion2019) {
            phoneBytes = ByteHexUtils.subbyte(bytes, 5, 15);
        } else {
            phoneBytes = ByteHexUtils.subbyte(bytes, 4, 10);
        }
        /** ??????????????? ??????????????????????????????????????? */
        int flowId = Jt808Utils.parseFlowId(phoneBytes, packageSize);

        /** ?????????????????? */
        String phone = ByteHexUtils.parseHex(phoneBytes);
        Map<Integer, byte[]> sentPackages = cacheService.getSentPackages(phone);
        if (sentPackages == null) {
            cacheService.setSentPackages(phone, new HashMap<>());
            sentPackages = cacheService.getSentPackages(phone);
        } else {
            sentPackages.clear();
        }

        for(int i = 1; i <= packageSize; i++){
            byte[] bodyBufferBytes;
            if(packageSize == i){
                bodyBufferBytes = ByteHexUtils.subbyte(messageBodyBytes, (i-1) * 1023);
            }else{
                bodyBufferBytes = ByteHexUtils.subbyte(messageBodyBytes, (i-1) * 1023, i * 1023);
            }
            byte[] data = Jt808Utils.warp(
                    messageIdBytes,
                    phoneBytes,
                    packageSize,
                    i,
                    flowId,
                    bodyBufferBytes
            );
            sentPackages.put(i, data);
            if (session instanceof IoSession) {
                ((IoSession) session).write(data);
            } else if (session instanceof ChannelHandlerContext) {
                ((ChannelHandlerContext) session).writeAndFlush(data);
            }
            flowId ++;
        }
    }

    public byte[] mergePackage(String phone, int totalPackageSize) {
        Map<Integer, byte[]> packageBufferMap = cacheService.getPackages(phone);
        if (GeneralUtils.isEmpty(packageBufferMap)) {
            return new byte[0];
        }
        /** ????????????1????????? ??????????????? */
        byte[] bytes = ByteHexUtils.subbyte(packageBufferMap.get(1), 0, packageBufferMap.get(1).length - 1);
        for (int i = 2; i <= totalPackageSize; i++) {
            byte[] pkg = packageBufferMap.get(i);
            bytes = ByteHexUtils.union(bytes, ByteHexUtils.subbyte(pkg, phone.length() == 20 ? 21 : 16, packageBufferMap.get(1).length - 1));
        }
        packageBufferMap.clear();
        return bytes;
    }


    public boolean isFinishPackage(String phone, int totalPackageSize) {
        Map<Integer, byte[]> packageBufferMap = cacheService.getPackages(phone);
        if (packageBufferMap == null) {
            return false;
        }
        return cacheService.containsPackages(phone) && packageBufferMap.size() == totalPackageSize;
    }

    public void handleSession(Jt808Message jt808Message, Object session) throws RestException {
        /** ????????????????????????????????? ????????????????????? ??????????????????????????????????????????????????? ???????????????????????? */
        /** ??????????????? ????????? session ?????????????????? */
        String phone = jt808Message.getPhone();
        int flowId;
        if (Jt808SessionManager.contains(phone)) {
            if (session instanceof IoSession) {
                IoSession nextSession = (IoSession) session;
                IoSession prevSession = (IoSession) Jt808SessionManager.get(phone);
                flowId = (int) prevSession.getAttribute(SocketJt808Constants.FLOW_ID);
                if (prevSession.getId() != nextSession.getId()) {
                    prevSession.closeNow();
                }
                nextSession.setAttribute(phone, flowId);

            } else if (session instanceof ChannelHandlerContext) {
                ChannelHandlerContext nextSession = (ChannelHandlerContext) session;
                ChannelHandlerContext prevSession = (ChannelHandlerContext) Jt808SessionManager.get(phone);
                flowId = (int) prevSession.channel().attr(AttributeKey.valueOf(SocketJt808Constants.FLOW_ID)).get();
                if (!prevSession.name().equals(nextSession.name())) {
                    prevSession.close();
                }
                nextSession.channel().attr(AttributeKey.valueOf(SocketJt808Constants.FLOW_ID)).set(flowId);
            }

        }
        Jt808SessionManager.set(phone, session);

    }

    public Jt808Message parseMessage(byte[] messageBytes) throws RestException {
        Jt808Message jt808Message = new Jt808Message();
        jt808Message.setOriginDataBytes(messageBytes);
        jt808Message.setOriginData(ByteHexUtils.parseHex(messageBytes));
        int offset = 0;
        byte[] jt808MessageId = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        jt808Message.setMessageIdBytes(jt808MessageId);
        jt808Message.setMessageHex(ByteHexUtils.parseHex(jt808MessageId));
        jt808Message.setMessageId(ByteHexUtils.parseTwoInt(jt808MessageId));
        byte[] jt808MessageBodyProps = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        jt808Message.setMessageBodyPropsBytes(jt808MessageBodyProps);
        jt808Message.setMessageBodyProps(ByteHexUtils.parseHex(jt808MessageBodyProps));
        /** ?????????????????????????????????????????? ??????????????? 2019???????????? ????????????????????? */
        boolean isVersion2019 = Jt808Utils.isVersion2019(jt808MessageBodyProps);
        jt808Message.setVersion2019(isVersion2019);
        byte[] phoneBytes;
        if (isVersion2019) {
            byte[] protocolVersion = new byte[]{messageBytes[offset++]};
            jt808Message.setProtocolVersionBytes(protocolVersion);
            jt808Message.setProtocolVersion(ByteHexUtils.parseHex(protocolVersion));
            phoneBytes = new byte[]{
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++],
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++]
            };
        } else {
            phoneBytes = new byte[]{
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++],
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++]
            };
        }
        jt808Message.setPhoneBytes(phoneBytes);
        jt808Message.setPhone(ByteHexUtils.parseHex(phoneBytes));
        byte[] flowId = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        jt808Message.setFlowIdBytes(flowId);
        jt808Message.setFlowId(ByteHexUtils.parseTwoInt(flowId));
        boolean isSlicePackage = Jt808Utils.isSlicePackage(jt808MessageBodyProps);
        jt808Message.setSlicePackage(isSlicePackage);
        if (isSlicePackage) {
            byte[] packageSize = new byte[]{messageBytes[offset++], messageBytes[offset++]};
            jt808Message.setPackageSizeBytes(packageSize);
            jt808Message.setPackageSize(ByteHexUtils.parseTwoInt(packageSize));
            byte[] packageIndex = new byte[]{messageBytes[offset++], messageBytes[offset]};
            jt808Message.setPackageIndexBytes(packageIndex);
            jt808Message.setPackageIndex(ByteHexUtils.parseTwoInt(packageIndex));
        }
        return jt808Message;
    }
}
