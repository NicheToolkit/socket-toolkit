package io.github.nichetoolkit.socket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * <p>Jt808Message</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class Jt808Message implements Serializable {
    @JsonIgnore
    private byte[] originDataBytes;
    @JsonIgnore
    private byte[] messageIdBytes;
    @JsonIgnore
    private byte[] messageBodyPropsBytes;
    @JsonIgnore
    private byte[] protocolVersionBytes;
    @JsonIgnore
    private byte[] phoneBytes;
    @JsonIgnore
    private byte[] flowIdBytes;
    @JsonIgnore
    private byte[] packageSizeBytes;
    @JsonIgnore
    private byte[] packageIndexBytes;

    @JsonIgnore
    private String originData;

    private Integer messageId;

    private String messageHex;

    private String messageBodyProps;

    private String protocolVersion;

    private String phone;

    private Integer flowId;

    private Integer packageSize;

    private Integer packageIndex;

    private boolean isSlicePackage;
    private boolean isVersion2019;

    public Jt808Message() {
    }

    public String getMessageHex() {
        return messageHex;
    }

    public void setMessageHex(String messageHex) {
        this.messageHex = messageHex;
    }

    public byte[] getOriginDataBytes() {
        return originDataBytes;
    }

    public void setOriginDataBytes(byte[] originDataBytes) {
        this.originDataBytes = originDataBytes;
    }

    public byte[] getMessageIdBytes() {
        return messageIdBytes;
    }

    public void setMessageIdBytes(byte[] messageIdBytes) {
        this.messageIdBytes = messageIdBytes;
    }

    public byte[] getMessageBodyPropsBytes() {
        return messageBodyPropsBytes;
    }

    public void setMessageBodyPropsBytes(byte[] messageBodyPropsBytes) {
        this.messageBodyPropsBytes = messageBodyPropsBytes;
    }

    public byte[] getProtocolVersionBytes() {
        return protocolVersionBytes;
    }

    public void setProtocolVersionBytes(byte[] protocolVersionBytes) {
        this.protocolVersionBytes = protocolVersionBytes;
    }

    public byte[] getPhoneBytes() {
        return phoneBytes;
    }

    public void setPhoneBytes(byte[] phoneBytes) {
        this.phoneBytes = phoneBytes;
    }

    public byte[] getFlowIdBytes() {
        return flowIdBytes;
    }

    public void setFlowIdBytes(byte[] flowIdBytes) {
        this.flowIdBytes = flowIdBytes;
    }

    public byte[] getPackageSizeBytes() {
        return packageSizeBytes;
    }

    public void setPackageSizeBytes(byte[] packageSizeBytes) {
        this.packageSizeBytes = packageSizeBytes;
    }

    public byte[] getPackageIndexBytes() {
        return packageIndexBytes;
    }

    public void setPackageIndexBytes(byte[] packageIndexBytes) {
        this.packageIndexBytes = packageIndexBytes;
    }

    public String getOriginData() {
        return originData;
    }

    public void setOriginData(String originData) {
        this.originData = originData;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getMessageBodyProps() {
        return messageBodyProps;
    }

    public void setMessageBodyProps(String messageBodyProps) {
        this.messageBodyProps = messageBodyProps;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public Integer getFlowId() {
        return flowId;
    }

    public void setFlowId(Integer flowId) {
        this.flowId = flowId;
    }

    public Integer getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(Integer packageSize) {
        this.packageSize = packageSize;
    }

    public Integer getPackageIndex() {
        return packageIndex;
    }

    public void setPackageIndex(Integer packageIndex) {
        this.packageIndex = packageIndex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isSlicePackage() {
        return isSlicePackage;
    }

    public void setSlicePackage(boolean slicePackage) {
        isSlicePackage = slicePackage;
    }

    public boolean isVersion2019() {
        return isVersion2019;
    }

    public void setVersion2019(boolean version2019) {
        isVersion2019 = version2019;
    }
}
