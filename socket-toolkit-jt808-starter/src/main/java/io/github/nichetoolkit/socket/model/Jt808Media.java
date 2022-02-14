/*
 *  Copyright (c) 2020. 衷于栖 All rights reserved.
 *
 *  版权所有 衷于栖 并保留所有权利 2020。
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  作者：衷于栖（feedback@zhoyq.com）
 *  博客：https://www.zhoyq.com
 *  创建时间：2020
 *
 */

package io.github.nichetoolkit.socket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>MediaInfo </p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 * @company 郑州高维空间技术有限公司(c) 2021-2022
 * @date created on 9:04 2021/11/17
 */
@Builder
@Setter
@Getter
public class Jt808Media implements Serializable {
    /**
     * 多媒体数据ID
     */
    private int mediaId;

    /**
     * 多媒体数据类型
     * 0：图像；1：音频；2：视频
     */
    private int mediaType;

    /**
     * 多媒体数据格式
     * 0：JPEG；1：TIF；2：MP3；3：WAV；4：WMV； 其他保留
     */
    private int mediaFormat;

    /**
     * 事件项编码
     * 0：平台下发指令；1：定时动作；2：抢劫报警触发；3：碰撞侧翻报警触发；4：门开拍照；
     * 5：门关拍照；6：车门由开变关，时速从＜20 公里到超过 20 公里；7：定距拍照；
     * 其他保留
     */
    private int eventNumber;

    /**
     * 通道ID
     */
    private int tunnelId;
}
