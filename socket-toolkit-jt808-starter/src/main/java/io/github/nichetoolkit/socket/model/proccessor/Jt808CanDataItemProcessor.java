package io.github.nichetoolkit.socket.model.proccessor;

import io.github.nichetoolkit.socket.model.Jt808CanDataItem;

/**
 * <p>CanDataItemProcessor</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 * @company 郑州高维空间技术有限公司(c) 2021-2022
 * @date created on 14:06 2021/11/17
 */
public interface Jt808CanDataItemProcessor {
    /**
     * 处理 CAN 总线数据项
     * @param canDataItem can总线数据项
     */
    void process(Jt808CanDataItem canDataItem);
}
